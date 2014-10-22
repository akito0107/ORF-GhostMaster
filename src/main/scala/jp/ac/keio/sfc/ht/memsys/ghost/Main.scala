package jp.ac.keio.sfc.ht.memsys.main.Main

import akka.actor._
import jp.ac.keio.sfc.ht.memsys.ghost.actor.{GatewayActor, Gateway}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask
import sample.{SampleApp, SampleTaskImpl}

/**
 * Created by aqram on 9/24/14.
 */
object Main extends App{



  val ID:Int = 0
  val system = ActorSystem("Main")

  val gateway = TypedActor(system).typedActorOf(TypedProps(classOf[Gateway], new GatewayActor(ID)))

  //Sample Task Impl
  val sampleTask :OffloadableTask = new SampleTaskImpl
  val sampleApp = new SampleApp(gateway)

  sampleApp.runApp

}