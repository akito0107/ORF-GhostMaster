package jp.ac.keio.sfc.ht.memsys.ghost.actor

import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.requests.GhostResponse

/**
 * Created by aqram on 10/22/14.
 */
trait TaskExecutionCallback {

  def onReceive(response :GhostResponse) :Unit

}
