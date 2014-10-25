package sample;

/**
 * Created by aqram on 10/2/14.
 */

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.io.IOException;

/**
 * Created by aqram on 8/26/14.
 */
public class CacheContainer {

    private static final String INFINISPAN_CONFIGURATION = "infinispan-local.xml";

    private static EmbeddedCacheManager CACHE_MANAGER;

    private static CacheContainer instance = null;

    private CacheContainer(){
        try {
            CACHE_MANAGER = new DefaultCacheManager(INFINISPAN_CONFIGURATION);
        } catch (IOException e) {
            throw new RuntimeException("Unable to configure Infinispan", e);
        }
    }

    public static CacheContainer getInstance(){

        if(instance == null){
            instance = new CacheContainer();
        }
        return instance;
    }

    /*
    static {
        try {
            CACHE_MANAGER = new DefaultCacheManager(INFINISPAN_CONFIGURATION);
        } catch (IOException e) {
            throw new RuntimeException("Unable to configure Infinispan", e);
        }
    }
    */

    /**
     * Retrieves the default cache.
     * @param <K> type used as keys in this cache
     * @param <V> type used as values in this cache
     * @return a cache
     */
    public synchronized  <K, V> Cache<K, V> getCache() {
        return CACHE_MANAGER.getCache();
    }

    /**
     * Retrieves a named cache.
     * @param cacheName name of cache to retrieve
     * @param <K> type used as keys in this cache
     * @param <V> type used as values in this cache
     * @return a cache
     */
    public synchronized  <K, V> Cache<K, V> getCache(String cacheName) {
        if (cacheName == null) throw new NullPointerException("Cache name cannot be null!");
        return CACHE_MANAGER.getCache(cacheName);
    }

    /**
     * Retrieves the embedded cache manager.
     * @return a cache manager
     */
    public EmbeddedCacheManager getCacheContainer() {
        return CACHE_MANAGER;
    }

}
