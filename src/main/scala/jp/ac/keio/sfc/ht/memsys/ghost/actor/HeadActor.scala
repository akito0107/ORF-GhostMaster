package jp.ac.keio.sfc.ht.memsys.ghost.actor

import akka.actor.{ActorRef, ActorLogging, Props}
import akka.pattern.ask
import jp.ac.keio.sfc.ht.memsys.ghost.types.StatusTypes
import old.lib.commonlib.datatypes.{GhostResponseTypes, GhostRequestTypes}
import old.lib.commonlib.requests.{BundleKeys, Bundle, GhostResponse, GhostRequest}

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.util.control.Breaks._

/**
 * Created by aqram on 10/2/14.
 */

object HeadActor {
  def props(id: String): Props = Props(new HeadActor(id))
}

class HeadActor(id: String) extends BaseActor with ActorLogging {

  private val mChildrenTable :mutable.HashMap[String, mutable.MutableList[(StatusTypes, ActorRef)]] = new mutable.HashMap()
  private val MAXACTORNUMS = 10

  def receive = {

    case request: GhostRequest => {
      request.TYPE match {
        case GhostRequestTypes.REGISTERTASK => {
          //TODO LinkedList -> mutable.LinearSeq

          val bundle: Bundle = request.PARAMS
          val taskId: String = bundle.getData(BundleKeys.TASK_ID)

          if (taskId != null) {
            val actorList = new mutable.MutableList[(StatusTypes, ActorRef)]()
            mChildrenTable.put(taskId, actorList)

            for(i <- 0 until MAXACTORNUMS){
              val worker = this.context.actorOf(MemberActor.props(taskId))
              actorList += ((StatusTypes.STANDBY, worker))
            }

            val response = new GhostResponse(GhostResponseTypes.SUCCESS, "", null)
            sender() ! response
          } else {
            val response = new GhostResponse(GhostResponseTypes.FAIL, "", null)
            sender() ! response
          }
        }
        case GhostRequestTypes.EXECUTE => {

          val bundle: Bundle = request.PARAMS
          val taskId: String = bundle.getData(BundleKeys.TASK_ID)
          val requestSeq: String = bundle.getData(BundleKeys.DATA_SEQ)

          val actors = mChildrenTable.get(taskId)

          actors match {
            case Some(refs) => {
              /*
              //TODO dynamic
              var i = 0
              if (refs.length == 0) {
                worker = this.context.actorOf(MemberActor.props(taskName))
                refs += ((StatusTypes.RUNNING, worker))
              } else {
                breakable {
                  for (ref <- refs) {
                    ref._1 match {
                      case StatusTypes.STANDBY => {
                        worker = ref._2
                        break()
                      }
                    }
                    i += 1
                  }
                }
                if (worker == null) {
                  worker = this.context.actorOf(MemberActor.props(taskName))
                  refs += ((StatusTypes.RUNNING, worker))
                }
              }

              val index = i
              */
              val worker = this.context.actorOf(MemberActor.props(taskId))

              val bundle = new Bundle()
              bundle.putData(BundleKeys.DATA_SEQ, requestSeq)

              val result: Future[GhostResponse] = ask(worker, new GhostRequest(GhostRequestTypes.EXECUTE, bundle)).mapTo[GhostResponse]

              result onComplete {
                case Success(response) => {
                  response.STATUS match {
                    case GhostResponseTypes.SUCCESS => {
                      sender() ! new GhostResponse(GhostResponseTypes.SUCCESS, requestSeq, bundle)
                      this.context.unwatch(worker)
                      this.context.stop(worker)
                    }
                    case GhostResponseTypes.FAIL => {
                      val bundle = new Bundle()
                      bundle.putData(BundleKeys.MESSAGE, "Task Execute failed !! execute error!")
                      sender() ! new GhostResponse(GhostResponseTypes.FAIL, requestSeq, bundle)
                      this.context.unwatch(worker)
                      this.context.stop(worker)
                    }
                  }
                }
                case Failure(t) => {
                  val bundle = new Bundle()
                  bundle.putData(BundleKeys.MESSAGE, "Task Execute failed !! waiting error")
                  sender() ! new GhostResponse(GhostResponseTypes.FAIL, requestSeq, bundle)
                  this.context.unwatch(worker)
                  this.context.stop(worker)
                }
              }
            }


            case None => {
              val bundle = new Bundle()
              bundle.putData(BundleKeys.MESSAGE, "Task Fetch failed !! No Such Task")
              sender() ! new GhostResponse(GhostResponseTypes.FAIL, requestSeq, bundle)
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
