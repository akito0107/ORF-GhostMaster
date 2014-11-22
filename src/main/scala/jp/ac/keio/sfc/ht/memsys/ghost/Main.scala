package jp.ac.keio.sfc.ht.memsys.main.Main

import java.awt.image.BufferedImage
import java.util.concurrent.LinkedBlockingQueue

import akka.actor._
import com.typesafe.config.ConfigFactory
import jp.ac.keio.sfc.ht.memsys.ghost.actor.{GatewayActor, Gateway}
import sample.{DemoApp2}
import stream.agent.StreamClient

/**
 * Created by aqram on 9/24/14.
 */
object Main {

  val ID: Int = 0

  def main(args: Array[String]): Unit = {

    if (args.head == "Gateway") {
      println("Start Gateway")
      startGatewaySystem()
    }
    if (args.head == "Worker") {
      println("Start Worker")
      startWorkerSystem()
    }


  }

  def startGatewaySystem(): Unit = {
    val system = ActorSystem("Gateway", ConfigFactory.load("gateway"))
    val gateway = TypedActor(system).typedActorOf(TypedProps(classOf[Gateway], new GatewayActor(ID)))

    val inqueue = new LinkedBlockingQueue[BufferedImage]()
    val outqueue = new LinkedBlockingQueue[BufferedImage]()


    val s = new Thread(new Runnable {
      override def run(): Unit = {
        val server = new StreamClient(inqueue, outqueue)
        server.run()
      }
    })

    val t = new Thread(new Runnable {
      override def run(): Unit = {
        val app = new DemoApp2(inqueue, outqueue, gateway)
        app.startApp
      }
    })

    s.start()
    t.start()

    //Sample Task Impl
    /*
    val sampleTask: OffloadableTask = new SampleTaskImpl
    val sampleApp = new SampleApp(gateway)
    sampleApp.runApp
    */
  }

  def startWorkerSystem(): Unit = {
    val system = ActorSystem("Worker", ConfigFactory.load("worker"))
  }

}