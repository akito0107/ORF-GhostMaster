package jp.ac.keio.sfc.ht.memsys.ghost.actor

import java.util

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.Logging
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.datatypes.{GhostResponseTypes, GhostRequestTypes}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.requests._
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.util.Util
import jp.ac.keio.sfc.ht.memsys.ghost.types.StatusTypes
import org.infinispan.Cache
import org.infinispan.client.hotrod.RemoteCache
import org.infinispan.manager.EmbeddedCacheManager
import sample.{RemoteCacheContainer, SampleTaskKeys, CacheContainer}

/**
 * Created by aqram on 10/15/14.
 */

object MemberActor {
  def props(id: String): Props = Props(new MemberActor(id))
}

class MemberActor(AppId :String) extends Actor{

  val log = Logging(context.system, this)

  val ID = AppId;
  var Status :StatusTypes = StatusTypes.STANDBY


  //local
  /*
  val cacheContainer = CacheContainer.getInstance()
  val mCacheManager :EmbeddedCacheManager = cacheContainer.getCacheContainer()

  val mDataCache :Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.DATA_CACHE)
  val mTaskCache :Cache[String, OffloadableTask] = mCacheManager.getCache[String, OffloadableTask](CacheKeys.TASK_CACHE)
  val mResultCache :Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.RESULT_CACHE)
  val mPermanentDataCache :Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.PDATA_CACHE)
  */

   //Remote Cache
  val cacheContainer = RemoteCacheContainer.getInstance()
  val mDataCache :RemoteCache[String, OffloadableData] = cacheContainer.getCache[String, OffloadableData](CacheKeys.DATA_CACHE)
  val mPermanentDataCache :RemoteCache[String, OffloadableData] = cacheContainer.getCache[String, OffloadableData](CacheKeys.PDATA_CACHE)
  val mTaskCache :RemoteCache[String, OffloadableTask] = cacheContainer.getCache[String, OffloadableTask](CacheKeys.TASK_CACHE)
  val mResultCache :RemoteCache[String, OffloadableData] = cacheContainer.getCache[String, OffloadableData](CacheKeys.RESULT_CACHE)

  var currentTaskId :String = ""
  var currentTask :OffloadableTask = null


  def receive = {

    case request :GhostRequest => {
      request.TYPE match{
        case GhostRequestTypes.EXECUTE =>{

          val head = sender()

          log.info("Member received execute")
          val bundle :Bundle = request.PARAMS

          val taskId = bundle.getData(BundleKeys.TASK_ID)
          val seq = bundle.getData(BundleKeys.DATA_SEQ)
          //val p = bundle.getData(BundleKeys.PDATA)
          log.info("member : taskId : " + taskId + " seq " + seq)

          if(taskId != currentTaskId){
            currentTask = mTaskCache.get(taskId)
            currentTaskId =  taskId
          }

          val data :OffloadableData= mDataCache.get(Util.dataPathBuilder(currentTaskId, seq))
          val d_seq = data.getData("SEQ")
          val pdata :OffloadableData = mPermanentDataCache.get(Util.permanentDataPathBuilder(currentTaskId,"ORIGINAL_IMAGE"+d_seq))

          if(data == null){
            log.info("ERROR DATA IS NULL")
            head ! new GhostResponse(GhostResponseTypes.FAIL, currentTaskId, null)
          }

          val result :OffloadableData = currentTask.run(data, pdata)
          mResultCache.put(Util.dataPathBuilder(currentTaskId, seq), result);

          val resultBundle :Bundle = new Bundle()
          bundle.putData(BundleKeys.TASK_ID, currentTaskId)
          bundle.putData(BundleKeys.DATA_SEQ, seq)

          //TODO error handling
          head ! new GhostResponse(GhostResponseTypes.SUCCESS, currentTaskId, resultBundle)
        }
      }
    }
    case _ => {

    }
  }

}
