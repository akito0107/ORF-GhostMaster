package jp.ac.keio.sfc.ht.memsys.ghost.actor

import akka.actor.{ActorLogging, Props}
import jp.ac.keio.sfc.ht.memsys.ghost.types.StatusTypes
import old.lib.commonlib.datatypes.GhostRequestTypes
import old.lib.commonlib.requests.{Bundle, GhostRequest}

/**
 * Created by aqram on 10/15/14.
 */

object MemberActor {
  def props(id: String): Props = Props(new MemberActor(id))
}

class MemberActor(taskId :String) extends BaseActor with ActorLogging{

  val ID = taskId;
  var Status :StatusTypes = StatusTypes.STANDBY

  def receive = {

    case request :GhostRequest => {
      request.TYPE match{
        case GhostRequestTypes.EXECUTE =>{
          val bundle :Bundle = request.PARAMS



        }
      }
    }
    case _ => {

    }
  }

}
