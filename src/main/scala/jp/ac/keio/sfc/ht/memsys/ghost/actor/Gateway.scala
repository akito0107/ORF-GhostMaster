package jp.ac.keio.sfc.ht.memsys.ghost.actor

import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.requests.GhostRequest
import scala.concurrent.Future

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
