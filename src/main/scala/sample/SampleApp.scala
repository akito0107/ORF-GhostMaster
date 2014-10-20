package sample

import jp.ac.keio.sfc.ht.memsys.ghost.actor.GatewayActor
import old.lib.commonlib.data.OffloadableData
import old.lib.commonlib.requests.CacheKeys
import old.lib.commonlib.tasks.OffloadableTask
import old.lib.commonlib.util.CacheContainer
import org.infinispan.Cache
import org.infinispan.manager.EmbeddedCacheManager

/**
 * Created by aqram on 10/17/14.
 */
class SampleApp(_gateway :GatewayActor) {

  val gateway = _gateway

  val mCacheManager :EmbeddedCacheManager = CacheContainer.getCacheContainer()
  val mDataCache :Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.DATA_CACHE)
  val mTaskCache :Cache[String, OffloadableTask] = mCacheManager.getCache[String, OffloadableTask](CacheKeys.TASK_CACHE)
  val mResultCache :Cache[String, OffloadableData] = mCacheManager.getCache[String, OffloadableData](CacheKeys.RESULT_CACHE)

  val APP_NAME = "sample_app"
  val TASK_Name = "sample_task"


}
