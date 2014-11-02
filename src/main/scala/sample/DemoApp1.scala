package sample

import java.awt.image.BufferedImage
import java.util.concurrent.LinkedBlockingDeque
import jp.ac.keio.sfc.ht.memsys.ghost.actor.Gateway

import util.control.Breaks._

import demo1.{SIFTUtil, DemoApp1Callback}

/**
 * Created by aqram on 11/3/14.
 */
class DemoApp1(queue :LinkedBlockingDeque, gateway :Gateway) {

  val mQueue = queue
  val mGateway = gateway

  val APP_NAME = "SIFT_APP"

  var counter :Int = 0

  def start :Unit = {

    //APP regist
    val APP_ID = mGateway.registerApplication(APP_NAME)

    while(true) {
      breakable {
        mQueue.wait()
        val image = mQueue.getFirst

        var pixels :Array[Int] = SIFTUtil.getPixelsTab(image.asInstanceOf[BufferedImage])

      }
    }
  }

}
