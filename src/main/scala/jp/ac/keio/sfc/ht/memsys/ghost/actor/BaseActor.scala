package jp.ac.keio.sfc.ht.memsys.ghost.actor;

import akka.actor.{OneForOneStrategy, Actor}
import akka.actor.SupervisorStrategy._
import akka.event.Logging

abstract class BaseActor extends Actor {

  val log = Logging(context.system, this)

    override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10) {
        case _: ArithmeticException      => Resume
        case _: NullPointerException     => Restart
        case _: IllegalArgumentException => Stop
        case _: Exception                => Escalate
    }
}