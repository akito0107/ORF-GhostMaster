package jp.ac.keio.sfc.ht.memsys.main.Main

import java.util.concurrent.LinkedBlockingQueue

import akka.actor._
import com.typesafe.config.ConfigFactory
import jp.ac.keio.sfc.ht.memsys.ghost.actor.{GatewayActor, Gateway}
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask
import sample.{DemoApp1, SampleApp, SampleTaskImpl}
import server.ControlServer

/**
 * Created by aqram on 9/24/14.
 */
object Main {

  val ID:Int = 0
  val queue = new LinkedBlockingQueue[Object]()
  val aqueue = new LinkedBlockingQueue[Object]()

  def main(args :Array[String]) :Unit = {

    if (args.head == "Gateway") {
      println("Start Gateway")
      startServer()
      startGatewaySystem()
    }
    if (args.head == "Worker") {
      println("Start Worker")
      startWorkerSystem()
    }

  }

  def startServer(): Unit = {
    ControlServer.createServer(2555, queue)
    ControlServer.createServer(2556, aqueue)
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


    println("DEMO1 start...")
    val demo1 :DemoApp1 = new DemoApp1(queue, gateway)
    demo1.startApp

  }

  def startWorkerSystem() :Unit = {
    val system = ActorSystem("Worker", ConfigFactory.load("worker"))
  }

}