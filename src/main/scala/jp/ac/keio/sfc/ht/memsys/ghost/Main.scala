package jp.ac.keio.sfc.ht.memsys.main.Main

import akka.actor._
import jp.ac.keio.sfc.ht.memsys.ghost.actor.{GatewayActor, Gateway}
import old.lib.commonlib.data.OffloadableData
import old.lib.commonlib.requests.CacheKeys
import old.lib.commonlib.tasks.OffloadableTask
import org.infinispan.Cache
import org.infinispan.manager.EmbeddedCacheManager
import sample.CacheContainer

/**
 * Created by aqram on 9/24/14.
 */
object Main extends App{



  val ID:Int = 0
  val system = ActorSystem("Main")

  val gateway = TypedActor(system).typedActorOf(TypedProps(classOf[Gateway], new GatewayActor(ID)))

  //Sample Task Impl




}