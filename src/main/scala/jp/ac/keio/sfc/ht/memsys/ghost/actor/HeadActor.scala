package jp.ac.keio.sfc.ht.memsys.ghost.actor

import akka.actor.{Actor, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.routing.{RoundRobinRouter}
import akka.util.Timeout
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.datatypes.{GhostResponseTypes, GhostRequestTypes}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.requests.{GhostResponse, BundleKeys, Bundle, GhostRequest}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by aqram on 10/2/14.
 */

object HeadActor {
  def props(id: String): Props = Props(new HeadActor(id))
}

class HeadActor(id: String) extends Actor {


  //TODO
  private val MAXACTORNUMS = 100
  val router = context.actorOf(MemberActor.props(id).withRouter(RoundRobinRouter(nrOfInstances = MAXACTORNUMS)))

  val log = Logging(context.system, this)
  log.info("Head Actor Parent is : " + context.parent.path.toString)


  def receive = {

    case request: GhostRequest => {
      request.TYPE match {
        case GhostRequestTypes.REGISTERTASK => {
          //TODO LinkedList -> mutable.LinearSeq

          log.info("recieved register task")

          val bundle: Bundle = request.PARAMS
          val taskId: String = bundle.getData(BundleKeys.TASK_ID)

          log.info("task id : " + taskId)

          val gateway = sender

          if (taskId != null) {
            val response = new GhostResponse(GhostResponseTypes.SUCCESS, "", null)
            gateway ! response
          } else {
            val response = new GhostResponse(GhostResponseTypes.FAIL, "", null)
            gateway ! response
          }
        }
        case GhostRequestTypes.EXECUTE => {

          log.info("Received Exec Request!")

          val bundle: Bundle = request.PARAMS
          val taskId: String = bundle.getData(BundleKeys.TASK_ID)
          val requestSeq: String = bundle.getData(BundleKeys.DATA_SEQ)

          log.info("task id : " + taskId)

          val params = new Bundle()
          params.putData(BundleKeys.TASK_ID, taskId)
          params.putData(BundleKeys.DATA_SEQ, requestSeq)

          log.info("Task Execute request to child from head")

          implicit val timeout = Timeout(5 seconds)
          val result: Future[GhostResponse] = ask(router, new GhostRequest(GhostRequestTypes.EXECUTE, params)).mapTo[GhostResponse]

          val parent = sender

          result onComplete {
            case Success(response) => {
              response.STATUS match {
                case GhostResponseTypes.SUCCESS => {
                  log.info("app id " + id + " Task id : " + taskId + " has been finished!")
                  parent ! new GhostResponse(GhostResponseTypes.SUCCESS, requestSeq, bundle)
                }
                case GhostResponseTypes.FAIL => {
                  log.info("app id " + id + " Task id : " + taskId + " has been Failed!")
                  val bundle = new Bundle()
                  bundle.putData(BundleKeys.MESSAGE, "Task Execute failed !! execute error!")
                  parent ! new GhostResponse(GhostResponseTypes.FAIL, requestSeq, bundle)
                }
              }
            }
            case Failure(t) => {
              val bundle = new Bundle()
              bundle.putData(BundleKeys.MESSAGE, "Task Execute failed !! waiting error")
              parent ! new GhostResponse(GhostResponseTypes.FAIL, requestSeq, bundle)
            }
          }
        }

        case GhostRequestTypes.HEALTH => {
          //TODO

        }
        case GhostRequestTypes.SHUTDOWN => {
          //TODO
        }
        case _ => {
          sender() ! new GhostResponse(GhostResponseTypes.UNKNOWN, "", null)
        }
      }
    }
    case response: GhostResponse => {

    }

    case _ => {
      log.info(id + "unhandled message !")
    }

  }

}
