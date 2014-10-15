package jp.ac.keio.sfc.ht.memsys.ghost.actor;

import akka.actor.{OneForOneStrategy, Actor}
import akka.actor.SupervisorStrategy._

abstract class BaseActor extends Actor {

    override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10) {
        case _: ArithmeticException      => Resume
        case _: NullPointerException     => Restart
        case _: IllegalArgumentException => Stop
        case _: Exception                => Escalate
    }
}
