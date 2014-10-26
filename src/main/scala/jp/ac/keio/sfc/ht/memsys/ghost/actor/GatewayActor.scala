package jp.ac.keio.sfc.ht.memsys.ghost.actor

import akka.actor._
import akka.event.Logging
import akka.remote.RemoteScope
import akka.util.Timeout
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.datatypes.{GhostResponseTypes, GhostRequestTypes}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.requests.{GhostResponse, BundleKeys, Bundle, GhostRequest}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.util.Util

import scala.concurrent.Future
import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import akka.pattern.ask

/**
 * Created by aqram on 9/24/14.
 * GatewayTraitのimpl
 */

class GatewayActor(id: Int) extends Gateway {

  private val mRefMap: mutable.HashMap[String, ActorRef] = new mutable.HashMap()

  private val mRequestTable: mutable.HashMap[String, TaskExecutionCallback] = new mutable.HashMap()

  val log = Logging(TypedActor.context.system, TypedActor.context.self)


  override def registerApplication(APPNAME :String) :String = {
    //TODO アドレスも返す

    val APP_ID :String = Util.makeSHA1Hash(APPNAME)
    //val ref: ActorRef = TypedActor.context.actorOf(HeadActor.props(APP_ID))
    val host = Address("akka.tcp", "Worker", "127.0.0.1", 2552)
    val ref = TypedActor.context.actorOf(HeadActor.props(APP_ID).withDeploy(Deploy(scope = RemoteScope(host))))

    mRefMap.put(APP_ID, ref)

    APP_ID
  }

  override def registerTask(request :GhostRequest) :Future[Any] = {

    val bundle :Bundle = request.PARAMS
    val appId = bundle.getData(BundleKeys.APP_ID)
    val taskId = bundle.getData(BundleKeys.TASK_ID)

    val head = mRefMap.get(appId)

    head match {
      case Some(ref) => {
        val bundle = new Bundle()
        bundle.putData(BundleKeys.APP_ID, appId)
        bundle.putData(BundleKeys.TASK_ID, taskId)
        val mes = new GhostRequest(GhostRequestTypes.REGISTERTASK, bundle)

        implicit val timeout = Timeout(10 seconds)
        val f = ref ? mes

        return f
      }
      case None => {
        Future{
          val bundle = new Bundle()
          bundle.putData(BundleKeys.APP_ID, appId)
          bundle.putData(BundleKeys.TASK_ID, taskId)
          bundle.putData(BundleKeys.MESSAGE, "ERROR::NO SUCH APPLICATION")
          new GhostResponse(GhostResponseTypes.FAIL,"", bundle)
        }
      }
    }
  }

  override def executeTask(request: GhostRequest): Future[Any] = {

    log.info("Gateway get execute request !")
    val bundle :Bundle = request.PARAMS
    val appId = bundle.getData(BundleKeys.APP_ID)
    val taskId = bundle.getData(BundleKeys.TASK_ID)
    val seq = bundle.getData(BundleKeys.DATA_SEQ)

    log.info("Gateway execute request appid " + appId + " taskId " + taskId + " seq " + seq)

    val head = mRefMap.get(appId)

    head match {
      case Some(ref) => {
        val bundle = new Bundle()
        bundle.putData(BundleKeys.APP_ID, appId)
        bundle.putData(BundleKeys.TASK_ID, taskId)
        bundle.putData(BundleKeys.DATA_SEQ, seq)
        val mes = new GhostRequest(GhostRequestTypes.EXECUTE, bundle)

        implicit val timeout = Timeout(10 seconds)
        val f = ref ? mes

        return f
      }
      case None => {
        Future{
          val bundle = new Bundle()
          bundle.putData(BundleKeys.APP_ID, appId)
          bundle.putData(BundleKeys.TASK_ID, taskId)
          bundle.putData(BundleKeys.MESSAGE, "ERROR::NO SUCH APPLICATION")
          new GhostResponse(GhostResponseTypes.FAIL,"", bundle)
        }
      }
    }
  }

  override def checkApplicationHealth(request: GhostRequest): Future[Any] = ???

  override def removeApplication(request: GhostRequest): Future[Any] = ???

  override def onReceive(message: Any, sender: ActorRef): Unit = {

    /*
    message match {
      case GhostResponse => {

      }
      case GhostRequest => {

      }
    }
    */

  }

}
