package sample;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * Created by aqram on 9/2/14.
 */

public class RemoteCacheContainer {

    private static RemoteCacheManager CACHE_MANAGER;
    private static RemoteCacheContainer instance = null;

    private RemoteCacheContainer(){
        try {
            CACHE_MANAGER = new RemoteCacheManager(new ConfigurationBuilder().addServers("localhost").build());
            //CACHE_MANAGER = new RemoteCacheManager(new ConfigurationBuilder().addServers("133.27.171.58").build());
        } catch (Exception e) {
            throw new RuntimeException("Unable to configure Infinispan", e);
        }
    }

    public static RemoteCacheContainer getInstance(){

        if(instance==null){
            instance = new RemoteCacheContainer();
        }
        return instance;
    }


    /**
     * Retrieves the default cache.
     * @param <K> type used as keys in this cache
     * @param <V> type used as values in this cache
     * @return a cache
     */
    public <K, V> RemoteCache<K, V> getCache() {
        return CACHE_MANAGER.getCache();
    }

    /**
     * Retrieves a named cache.
     * @param cacheName name of cache to retrieve
     * @param <K> type used as keys in this cache
     * @param <V> type used as values in this cache
     * @return a cache
     */
    public <K, V> RemoteCache<K, V> getCache(String cacheName) {
        if (cacheName == null) throw new NullPointerException("Cache name cannot be null!");
        return CACHE_MANAGER.getCache(cacheName);
    }

    /**
     * Retrieves the embedded cache manager.
     * @return a cache manager
     */

}
