package jp.ac.keio.sfc.ht.memsys.main.Main

import akka.actor._
import com.typesafe.config.ConfigFactory
import jp.ac.keio.sfc.ht.memsys.ghost.actor.{GatewayActor, Gateway}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask
import sample.{SampleApp, SampleTaskImpl}
import server.ControlServer

/**
 * Created by aqram on 9/24/14.
 */
object Main {

  val ID:Int = 0

  def main(args :Array[String]) :Unit = {

    startServer()

    if (args.isEmpty || args.head == "Gateway")
  //    startGatewaySystem()
    if (args.isEmpty || args.head == "Worker")
      startWorkerSystem()
  }

  def startServer(): Unit = {
    ControlServer.createServer(2555)
  }

  def startGatewaySystem() :Unit = {
    val system = ActorSystem("Gateway", ConfigFactory.load("gateway"))
    val gateway = TypedActor(system).typedActorOf(TypedProps(classOf[Gateway], new GatewayActor(ID)))

    //Sample Task Impl
    /*
    val sampleTask: OffloadableTask = new SampleTaskImpl
    val sampleApp = new SampleApp(gateway)
    sampleApp.runApp
    */
  }

  def startWorkerSystem() :Unit = {
    val system = ActorSystem("Worker", ConfigFactory.load("worker"))
  }

}