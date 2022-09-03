package com.spartan.dc.core.util.common;

import com.google.common.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wxq
 * @create 2022/8/20 17:52
 * @description google cache
 */
public class CacheManager {
    private static Logger logger = LoggerFactory.getLogger("CacheManager");
    private static final long GUAVA_CACHE_SIZE = 10;
    private static final long GUAVA_CACHE_TIME = 20;
    public static final String PASSWORD_CACHE_KEY = "keystore";
    static LoadingCache<String, String> loadingCache;

    static {
        if (GUAVA_CACHE_TIME <= 0) {
            loadingCache = CacheBuilder.newBuilder()
                    .maximumSize(GUAVA_CACHE_SIZE)
                    .build(new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) {
                            return "";
                        }
                    });
        } else {
            loadingCache = CacheBuilder.newBuilder()
                    .maximumSize(GUAVA_CACHE_SIZE)
                    .expireAfterAccess(GUAVA_CACHE_TIME, TimeUnit.MINUTES)
                    .expireAfterWrite(GUAVA_CACHE_TIME, TimeUnit.MINUTES)
                    .removalListener(new RemovalListener<String, String>() {
                        @Override
                        public void onRemoval(RemovalNotification<String, String> removalNotification) {
                            logger.info("remove cache data:" + removalNotification.getValue());
                        }
                    })
                    .recordStats()
                    .build(new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) {
                            return "";
                        }
                    });
        }
    }


    public static void put(String key, String value) {
        try {
            loadingCache.put(key, value);
        } catch (Exception e) {
            logger.error("Failed to set the cache value", e);
        }
    }

    /**
     * @param map
     */
    public static void putAll(Map<? extends String, ? extends String> map) {
        try {
            loadingCache.putAll(map);
        } catch (Exception e) {
            logger.error("Failed to set cache values in batches. Procedure", e);
        }
    }


    public static String get(String key) {
        String value = "";
        try {
            value = loadingCache.get(key);
        } catch (Exception e) {
            logger.error("Error getting cache value", e);
        }
        return value;
    }

    /**
     * @param key
     */
    public static void remove(String key) {
        try {
            loadingCache.invalidate(key);
        } catch (Exception e) {
            logger.error("Error removing cache", e);
        }
    }


    public static void removeAll(Iterable<String> keys) {
        try {
            loadingCache.invalidateAll(keys);
        } catch (Exception e) {
            logger.error("Failed to batch remove the cache. Procedure", e);
        }
    }


    public static void removeAll() {
        try {
            loadingCache.invalidateAll();
        } catch (Exception e) {
            logger.error("Error clearing all caches", e);
        }
    }


    public static long size() {
        long size = 0;
        try {
            size = loadingCache.size();
        } catch (Exception e) {
            logger.error("Failed to get the number of cache entries", e);
        }
        return size;
    }
}
