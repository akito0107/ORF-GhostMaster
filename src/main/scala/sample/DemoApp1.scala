package sample

import akka.util.Timeout
import java.awt.image.BufferedImage
import java.util
import java.util.Collections
import java.util.concurrent.{LinkedBlockingQueue, LinkedBlockingDeque}
import akka.util.Timeout
import jp.ac.keio.sfc.ht.memsys.ghost.actor.Gateway
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.datatypes.GhostRequestTypes
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.requests._
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.util.Util
import org.infinispan.client.hotrod.RemoteCache
import sift._

import demo1.{SIFTUtil, DemoApp1Callback}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
 * Created by aqram on 11/3/14.
 */
class DemoApp1(queue: LinkedBlockingQueue[Object], gateway: Gateway) {

  val mQueue = queue
  val mGateway = gateway

  val APP_NAME = "SIFT_APP"
  val TASK_NAME = "SIFT_TASK"

  var counter: Int = 0

  /**
  constants
    */
  private val steps: Int = 5
  private val initial_sigma: Float = 1.6f
  private val fdsize: Int = 4
  private val fdbins: Int = 8
  private val min_size: Int = 64
  private val max_size: Int = 1024

  val cacheContainer = RemoteCacheContainer.getInstance()
  val mDataCache :RemoteCache[String, OffloadableData] = cacheContainer.getCache[String, OffloadableData](CacheKeys.DATA_CACHE)
  val mTaskCache :RemoteCache[String, OffloadableTask] = cacheContainer.getCache[String, OffloadableTask](CacheKeys.TASK_CACHE)
  val mResultCache :RemoteCache[String, OffloadableData] = cacheContainer.getCache[String, OffloadableData](CacheKeys.RESULT_CACHE)


  def startApp: Unit ={

    //APP regist
    val APP_ID = mGateway.registerApplication(APP_NAME)

    println("REGISTER APP")
    println(APP_ID)

    //register task for cache
    val TASK_ID = Util.taskPathBuilder(APP_ID, TASK_NAME)
    mTaskCache.put(TASK_ID, new SIFTTask())

    println("REGISTER TASK")
    println(TASK_ID)

    //init task params
    val bundle: Bundle = new Bundle()
    bundle.putData(BundleKeys.APP_ID, APP_ID)
    bundle.putData(BundleKeys.TASK_ID, TASK_ID)

    val request: GhostRequest = new GhostRequest(GhostRequestTypes.REGISTERTASK, bundle)
    val fTask: Future[Any] = gateway.registerTask(request)

    implicit val timeout = Timeout(10 seconds)
    //waiting for register task....
    val result = Await.result(fTask, timeout.duration).asInstanceOf[GhostResponse]
    println("Register Task DONE")

    while (true) {
      println("Waiting....")
      try {
        val image = mQueue.take().asInstanceOf[BufferedImage]
        var pixels: Array[Int] = SIFTUtil.getPixelsTab(image)
        var fa = SIFT.ArrayToFloatArray2D(image.getWidth(), image.getHeight(), pixels)
        getFeatures(fa, APP_ID, TASK_ID)
      }catch{
        case e :InterruptedException => println("Thread error....")
        case e :ClassCastException => println("cast ERROR")
        case e :Exception => println("UNKNOWN ERROR")
      }
    }
  }

  def getFeatures(_fa: FloatArray2D, APP_ID :String, TASK_ID :String): Unit = {
    var fa = _fa

    val sift: FloatArray2DSIFT = new FloatArray2DSIFT(fdsize, fdbins)
    Filter.enhance(fa, 1.0f)

    fa = Filter.computeGaussianFastMirror(fa, (Math.sqrt(initial_sigma * initial_sigma - 0.25)).asInstanceOf[Float])
    sift.init(fa, steps, initial_sigma, min_size, max_size)

    for(o <- 0 until sift.getOctaves.length){

      val seq :String = o.toString

      val data :OffloadableData = new OffloadableData(TASK_ID, seq )
      data.putData("OCTAVE", sift.getOctave(o))
      data.putData("SEQ", seq)
      mDataCache.put(Util.dataPathBuilder(TASK_ID, seq), data)

      val eBundle: Bundle = new Bundle()
      eBundle.putData(BundleKeys.APP_ID, APP_ID)
      eBundle.putData(BundleKeys.TASK_ID, TASK_ID)
      eBundle.putData(BundleKeys.DATA_SEQ, seq)

      val eRequest: GhostRequest = new GhostRequest(GhostRequestTypes.EXECUTE, eBundle)

      val res :Future[Any] = gateway.executeTask(eRequest)

    }

    //val fs1: util.Vector[Feature] = sift.run(max_size)

    //Collections.sort(fs1)

    //fs1
  }
}
