package jp.ac.keio.sfc.ht.memsys.ghost.actor

import akka.actor.{ActorLogging, Props}
import jp.ac.keio.sfc.ht.memsys.ghost.types.StatusTypes
import old.lib.commonlib.data.OffloadableData
import old.lib.commonlib.datatypes.{GhostResponseTypes, GhostRequestTypes}
import old.lib.commonlib.requests._
import old.lib.commonlib.tasks.OffloadableTask
import old.lib.commonlib.util.{Util, CacheContainer}
import org.infinispan.Cache
import org.infinispan.manager.EmbeddedCacheManager

/**
 * Created by aqram on 10/15/14.
 */

object MemberActor {
  def props(id: String): Props = Props(new MemberActor(id))
}

class MemberActor(AppId :String) extends BaseActor with ActorLogging{

  val ID = AppId;
  var Status :StatusTypes = StatusTypes.STANDBY

  val mCacheManager :EmbeddedCacheManager = CacheContainer.getCacheContainer()

  val mDataCache :Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.DATA_CACHE)
  val mTaskCache :Cache[String, OffloadableTask] = mCacheManager.getCache[String, OffloadableTask](CacheKeys.TASK_CACHE)
  val mResultCache :Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.RESULT_CACHE)

  var currentTaskId :String = ""
  var currentTask :OffloadableTask = null


  def receive = {

    case request :GhostRequest => {
      request.TYPE match{
        case GhostRequestTypes.EXECUTE =>{
          val bundle :Bundle = request.PARAMS

          val taskId = bundle.getData(BundleKeys.TASK_ID)
          val seq = bundle.getData(BundleKeys.DATA_SEQ)

          if(taskId != currentTaskId){
            currentTask = mTaskCache.get(taskId)
            currentTaskId =  taskId
          }

          val data = mDataCache.get(Util.dataPathBuilder(currentTaskId, seq))
          val result :OffloadableData = currentTask.run(data)
          mResultCache.put(Util.dataPathBuilder(currentTaskId, seq), result);

          val resultBundle :Bundle = new Bundle()
          bundle.putData(BundleKeys.TASK_ID, currentTaskId)
          bundle.putData(BundleKeys.DATA_SEQ, seq)

          //TODO error handling
          sender() ! new GhostResponse(GhostResponseTypes.SUCCESS, currentTaskId, resultBundle)
        }
      }
    }
    case _ => {

    }
  }

}
