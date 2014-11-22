package sample

import java.awt.image.BufferedImage
import java.util.concurrent.LinkedBlockingQueue

import akka.util.Timeout
import jp.ac.keio.sfc.ht.memsys.ghost.actor.Gateway
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.datatypes.GhostRequestTypes
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.requests._
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.util.Util
import memsys.{BackgroundSubtractionTask, Constants}
import org.infinispan.client.hotrod.RemoteCache

import scala.collection.parallel.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.control.Breaks;

/**
 * Created by aqram on 11/18/14.
 */
class DemoApp2(inqueue: LinkedBlockingQueue[BufferedImage], outqueue: LinkedBlockingQueue[BufferedImage], gateway: Gateway) {

  val mInputQueue = inqueue
  val mOutputQueue = outqueue
  val mGateway = gateway

  val APP_NAME = "WEBCAM_APP"
  val TASK_NAME = "WEBCAM_TASK"

  var counter: Int = 0

  val NUM_OF_PARA = Constants.NUMOFACTORS

  /**
  constants
    */
  private val steps: Int = 10
  private val THRESHOLD: Int = 20000
  private var count: Int = 0

  private val initial_sigma: Float = 1.6f
  private val fdsize: Int = 4
  private val fdbins: Int = 8
  private val min_size: Int = 64
  private val max_size: Int = 1024

  private var originalImg: Array[Int] = new Array[Int](Constants.HEIGHT * Constants.WIDTH)

  val cacheContainer = RemoteCacheContainer.getInstance()
  val mDataCache: RemoteCache[String, OffloadableData] = cacheContainer.getCache[String, OffloadableData](CacheKeys.DATA_CACHE)
  val mTaskCache: RemoteCache[String, OffloadableTask] = cacheContainer.getCache[String, OffloadableTask](CacheKeys.TASK_CACHE)
  val mResultCache: RemoteCache[String, OffloadableData] = cacheContainer.getCache[String, OffloadableData](CacheKeys.RESULT_CACHE)
  val mPermanentDataCache: RemoteCache[String, OffloadableData] = cacheContainer.getCache[String, OffloadableData](CacheKeys.PDATA_CACHE)

  def startApp: Unit = {

    for (i <- 0 until originalImg.length) {
      originalImg(0) = 0
    }

    //APP regist
    val APP_ID = mGateway.registerApplication(APP_NAME)

    println("REGISTER APP")
    println(APP_ID)

    //register task for cache
    val TASK_ID = Util.taskPathBuilder(APP_ID, TASK_NAME)
    mTaskCache.put(TASK_ID, new BackgroundSubtractionTask())

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

    backgroundSubtraction(APP_ID, TASK_ID)

  }

  val unit = Constants.WIDTH * Constants.HEIGHT / NUM_OF_PARA
  var frame_counter = 0

  //var list: mutable.Seq[Int] = Seq()
  var queue = new scala.collection.mutable.Queue[Int]

  def backgroundSubtraction(appId: String, taskId: String): Unit = {

    val mybreaks = new Breaks;
    import mybreaks.{break, breakable};

    while (true) {
      breakable {

        val img: BufferedImage = mInputQueue.take();

        println(img.getWidth)
        println(img.getHeight)

        if (img.getWidth() != Constants.WIDTH) {
          break
        }


        val pics: Array[Int] = img.getRGB(0, 0, Constants.WIDTH, Constants.HEIGHT, null, 0, Constants.WIDTH)

        if (count < steps) {

          for (i <- 0 until originalImg.length) {
            originalImg(i) = Math.round(originalImg(i) * 0.2 + pics(i) * 0.8).asInstanceOf[Int]
          }
          count = count + 1

          if (count == steps) {

            for (i <- 0 until NUM_OF_PARA) {
              val pdata = new OffloadableData(taskId, i.toString)
              pdata.putData("ORIGINAL", originalImg.slice(i * unit, (i + 1) * unit))
              mPermanentDataCache.put(Util.permanentDataPathBuilder(taskId, "ORIGINAL_IMAGE" + i.toString), pdata)
            }

          }

        } else {

          for (i <- 0 until NUM_OF_PARA) {
            val data = new OffloadableData(taskId, i.toString)
            data.putData("PICS", pics.slice(i * unit, (i + 1) * unit))
            data.putData("SEQ", i.toString)
            mDataCache.put(Util.dataPathBuilder(taskId, (frame_counter + i).toString), data)

            val eBundle: Bundle = new Bundle()
            eBundle.putData(BundleKeys.APP_ID, appId)
            eBundle.putData(BundleKeys.TASK_ID, taskId)
            eBundle.putData(BundleKeys.DATA_SEQ, (frame_counter + i).toString)

            val eRequest: GhostRequest = new GhostRequest(GhostRequestTypes.EXECUTE, eBundle)

            val res: Future[Any] = gateway.executeTask(eRequest)

          }

          //Thread.sleep(100)

          val out = Constants.deepCopy(img)

          for (i <- 0 until NUM_OF_PARA) {
            //  queue.enqueue(i)
            //}

            //while (queue.size > 0) {

            // val i = queue.dequeue

            val result = mResultCache.get(Util.dataPathBuilder(taskId, (frame_counter + i).toString))
            if (result != null) {
              result match {
                case value: OffloadableData => {
                  val rgb = value.getData("PICS").asInstanceOf[Array[Int]]
                  var m = 0
                  for (j <- i * (Constants.HEIGHT / NUM_OF_PARA) until (i + 1) * (Constants.HEIGHT / NUM_OF_PARA)) {
                    for (k <- 0 until Constants.WIDTH) {
                      out.setRGB(k, j, rgb(m))
                      m = m + 1
                    }
                  }
                }
              }
            } else if (result == null) {
              //  queue.enqueue(i)
            }
          }

          mOutputQueue.put(out)

          frame_counter = frame_counter + 1

          if (frame_counter > 100000) {
            frame_counter = 0
          }
        }
      }
    }

  }

}
