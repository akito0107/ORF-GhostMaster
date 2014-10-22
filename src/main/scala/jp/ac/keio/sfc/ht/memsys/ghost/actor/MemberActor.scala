package jp.ac.keio.sfc.ht.memsys.ghost.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.Logging
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.datatypes.{GhostResponseTypes, GhostRequestTypes}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.requests._
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.util.Util
import jp.ac.keio.sfc.ht.memsys.ghost.types.StatusTypes
import org.infinispan.Cache
import org.infinispan.manager.EmbeddedCacheManager
import sample.CacheContainer

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
