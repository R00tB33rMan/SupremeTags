package net.noscape.project.supremetags.storage;

import lombok.Getter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataCache {

    @Getter
    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static String getCachedData(String key) {
        return cache.get(key);
    }

    public static void cacheData(String key, String value) {
        cache.put(key, value);
    }

    public static void removeFromCache(String key) {
        cache.remove(key);
    }

    public static void clearCache() {
        cache.clear();
    }
}
