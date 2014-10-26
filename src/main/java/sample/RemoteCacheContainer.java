package sample;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * Created by aqram on 9/2/14.
 */

public class RemoteCacheContainer {

    private static final RemoteCacheManager CACHE_MANAGER;

    static {
        try {
            CACHE_MANAGER = new RemoteCacheManager(new ConfigurationBuilder().addServers("localhost").build());
        } catch (Exception e) {
            throw new RuntimeException("Unable to configure Infinispan", e);
        }
    }

    /**
     * Retrieves the default cache.
     * @param <K> type used as keys in this cache
     * @param <V> type used as values in this cache
     * @return a cache
     */
    public static <K, V> RemoteCache<K, V> getCache() {
        return CACHE_MANAGER.getCache();
    }

    /**
     * Retrieves a named cache.
     * @param cacheName name of cache to retrieve
     * @param <K> type used as keys in this cache
     * @param <V> type used as values in this cache
     * @return a cache
     */
    public static <K, V> RemoteCache<K, V> getCache(String cacheName) {
        if (cacheName == null) throw new NullPointerException("Cache name cannot be null!");
        return CACHE_MANAGER.getCache(cacheName);
    }

    /**
     * Retrieves the embedded cache manager.
     * @return a cache manager
     */

}
