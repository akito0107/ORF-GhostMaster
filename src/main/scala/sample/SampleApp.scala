package sample

import akka.util.Timeout
import jp.ac.keio.sfc.ht.memsys.ghost.actor.{Gateway, GatewayActor}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.datatypes.{GhostResponseTypes, GhostRequestTypes}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.requests._
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.util.Util
import jp.ac.keio.sfc.ht.memsys.ghost.types.StatusTypes
import org.infinispan.Cache
import org.infinispan.manager.EmbeddedCacheManager

import scala.concurrent.{Await, Future}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

/**
 * Created by aqram on 10/17/14.
 */
class SampleApp(_gateway: Gateway) {

  val gateway = _gateway

  val cacheContainer = CacheContainer.getInstance()
  val mCacheManager: EmbeddedCacheManager = cacheContainer.getCacheContainer()
  val mDataCache: Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.DATA_CACHE)
  val mTaskCache: Cache[String, OffloadableTask] = mCacheManager.getCache[String, OffloadableTask](CacheKeys.TASK_CACHE)
  val mResultCache: Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.RESULT_CACHE)

  val APP_NAME = "sample_app"
  val TASK_NAME = "sample_task"

  def runApp: Unit = {
    //register application

    println("Start App")
    val APP_ID = gateway.registerApplication(APP_NAME)

    println("APP ID : " + APP_ID)
    //register task for cache
    val TASK_ID = Util.taskPathBuilder(APP_ID, TASK_NAME)
    mTaskCache.put(TASK_ID, new SampleTaskImpl())

    //init task params
    val bundle: Bundle = new Bundle()
    bundle.putData(BundleKeys.APP_ID, APP_ID)
    bundle.putData(BundleKeys.TASK_ID, TASK_ID)

    val request: GhostRequest = new GhostRequest(GhostRequestTypes.REGISTERTASK, bundle)
    val fTask: Future[Any] = gateway.registerTask(request)

    implicit val timeout = Timeout(10 seconds)
    //waiting for register task....
    val result = Await.result(fTask, timeout.duration).asInstanceOf[GhostResponse]
    println("Register Task DONE")

    val seq: String = "0"

    //offload the data
    println("Offload the data")
    val data: OffloadableData = SampleUtil.genData(TASK_ID, seq)
    data.putData(SampleTaskKeys.DEBUG, null)
    //    data.debugData()

    mDataCache.put(Util.dataPathBuilder(TASK_ID, seq), data)

    println("Gen the data path ://" + Util.dataPathBuilder(TASK_ID, seq))

    val eBundle: Bundle = new Bundle()
    eBundle.putData(BundleKeys.APP_ID, APP_ID)
    eBundle.putData(BundleKeys.TASK_ID, TASK_ID)
    eBundle.putData(BundleKeys.DATA_SEQ, seq)

    val eRequest: GhostRequest = new GhostRequest(GhostRequestTypes.EXECUTE, eBundle)

    val res = gateway.executeTask(eRequest)
    val rlt = Await.result(res, timeout.duration).asInstanceOf[GhostResponse]

    val o: OffloadableData = mResultCache.get(Util.dataPathBuilder(TASK_ID, seq))
    val ad = o.getData(SampleTaskKeys.DATA)

    println("print result")
    for (i <- 0 to ad.length - 1) {
      println(ad(i))
    }

  }

}
