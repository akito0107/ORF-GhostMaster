package jp.ac.keio.sfc.ht.memsys.ghost.actor

import akka.actor.{TypedActor, ActorContext, ActorRef, Props}
import old.lib.commonlib.datatypes.{GhostResponseTypes, GhostRequestTypes}
import old.lib.commonlib.requests.{BundleKeys, Bundle, GhostRequest, GhostResponse}

import old.lib.commonlib.util.Util;

import scala.concurrent.Future
import scala.collection.mutable

import akka.pattern.ask

/**
 * Created by aqram on 9/24/14.
 * GatewayTraitのimpl
 */

class GatewayActor(id: Int) extends Gateway {

  private val mRefMap: mutable.HashMap[String, ActorRef] = new mutable.HashMap()

  override def registerApplication(APPNAME :String) :String = {
    //TODO アドレスも返す

    val APP_ID :String = Util.makeSHA1Hash(APPNAME)
    val ref: ActorRef = TypedActor.context.actorOf(HeadActor.props(APP_ID))
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

    val bundle :Bundle = request.PARAMS
    val appId = bundle.getData(BundleKeys.APP_ID)
    val taskId = bundle.getData(BundleKeys.TASK_ID)
    val seq = bundle.getData(BundleKeys.DATA_SEQ)

    val head = mRefMap.get(appId)

    head match {
      case Some(ref) => {
        val bundle = new Bundle()
        bundle.putData(BundleKeys.APP_ID, appId)
        bundle.putData(BundleKeys.TASK_ID, taskId)
        bundle.putData(BundleKeys.DATA_SEQ, seq)
        val mes = new GhostRequest(GhostRequestTypes.EXECUTE, bundle)

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

}

