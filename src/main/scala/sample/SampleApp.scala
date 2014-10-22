package sample

import jp.ac.keio.sfc.ht.memsys.ghost.actor.{Gateway, GatewayActor}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.datatypes.GhostRequestTypes
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.requests._
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.util.Util
import org.infinispan.Cache
import org.infinispan.manager.EmbeddedCacheManager

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * Created by aqram on 10/17/14.
 */
class SampleApp(_gateway :Gateway) {

  val gateway = _gateway

  val mCacheManager :EmbeddedCacheManager = CacheContainer.getCacheContainer()
  val mDataCache :Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.DATA_CACHE)
  val mTaskCache :Cache[String, OffloadableTask] = mCacheManager.getCache[String, OffloadableTask](CacheKeys.TASK_CACHE)
  val mResultCache :Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.RESULT_CACHE)

  val APP_NAME = "sample_app"
  val TASK_NAME = "sample_task"

  def runApp :Unit = {
    //register application
    val APP_ID = gateway.registerApplication(APP_NAME)

    //register task for cache
    val TASK_ID = Util.taskPathBuilder(APP_ID, TASK_NAME)
    mTaskCache.put(TASK_ID, new SampleTaskImpl())

    //init task params
    val bundle: Bundle = new Bundle()
    bundle.putData(BundleKeys.APP_ID, APP_ID)
    bundle.putData(BundleKeys.TASK_ID, TASK_ID)

    val request: GhostRequest = new GhostRequest(GhostRequestTypes.REGISTERTASK, bundle)
    val fTask: Future[Any] = gateway.registerTask(request)

    //waiting for register task....
    val result = Await.result(fTask, Duration.Inf).asInstanceOf[GhostResponse]

    val seq :String = "0"

    //offload the data
    val data :OffloadableData = SampleUtil.genData(TASK_ID, seq)
    mDataCache.put(Util.dataPathBuilder(TASK_NAME, seq), data)

    val eBundle :Bundle = new Bundle()
    eBundle.putData(BundleKeys.DATA_SEQ, Util.dataPathBuilder(TASK_NAME, seq))

    val eRequest :GhostRequest = new GhostRequest(GhostRequestTypes.EXECUTE, eBundle)

    gateway.executeTask(eRequest)
  }

}
