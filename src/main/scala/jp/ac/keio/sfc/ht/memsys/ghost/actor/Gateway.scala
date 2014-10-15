package jp.ac.keio.sfc.ht.memsys.ghost.actor

import jp.ac.keio.sfc.ht.memsys.Actors.Manager.ApplicationManager
import jp.ac.keio.sfc.ht.memsys.common.OffloadableTaskPipeline
import old.lib.commonlib.requests.{GhostRequest, GhostResponse}
import old.lib.commonlib.tasks.OffloadableTaskPipeline

import scala.concurrent.Future
import akka.actor.ActorRef
import jp.ac.keio.sfc.ht.memsys.Data.FGhostResponse

/**
 * Created by aqram on 10/2/14.
 */
trait Gateway {

  def registerApplication(APPNAME :String) :String

  def registerTask(request :GhostRequest) :Future[Any]

  //TODO def registerTaskPipeline(request :GhostRequest) :Future[GhostResponse]

  def checkApplicationHealth(request :GhostRequest) :Future[Any]

  def executeTask(request :GhostRequest) :Future[Any]

  //TODO def stopTask(request :GhostRequest) :Future[GhostResponse]

  def removeApplication(request :GhostRequest) :Future[Any]

}
