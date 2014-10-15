package jp.ac.keio.sfc.ht.memsys.ghost.actor

import akka.actor.{ActorRef, ActorLogging, Props}
import jp.ac.keio.sfc.ht.memsys.ghost.types.StatusTypes
import old.lib.commonlib.datatypes.{GhostResponseTypes, GhostRequestTypes}
import old.lib.commonlib.requests.{BundleKeys, Bundle, GhostResponse, GhostRequest}

import scala.collection.mutable

/**
 * Created by aqram on 10/2/14.
 */

object HeadActor {
  def props(id: String): Props = Props(new HeadActor(id))
}

class HeadActor(id: String) extends BaseActor with ActorLogging{

  private val mChildrenTable: mutable.HashMap[String, mutable.LinkedList[(StatusTypes, ActorRef)]] = new mutable.HashMap()

  def receive = {

    case request :GhostRequest => {
      request.TYPE match{
        case GhostRequestTypes.REGISTERTASK => {

          val bundle :Bundle = request.PARAMS
          val taskName :String = bundle.getData(BundleKeys.TASK_NAME)

          if (taskName != null) {
            val actorList: mutable.LinkedList[(StatusTypes, ActorRef)] = new mutable.LinkedList[(StatusTypes, ActorRef)]()
            mChildrenTable.put(taskName, actorList)

            val response = new GhostResponse(GhostResponseTypes.SUCCESS, "",null)
            sender() ! response
          }else{
            val response = new GhostResponse(GhostResponseTypes.FAIL, "",null)
            sender() ! response
          }
        }
        case GhostRequestTypes.EXECUTE => {

        }
        case GhostRequestTypes.HEALTH => {
          //TODO

        }
        case GhostRequestTypes.SHUTDOWN => {
          //TODO
        }
        case _ => {
          sender() ! new GhostResponse(GhostResponseTypes.UNKNOWN, "",null)
        }
      }
    }

    case response :GhostResponse => {

    }

    case _ => {
      log.info(id + "unhandled message !")
    }

  }

}
