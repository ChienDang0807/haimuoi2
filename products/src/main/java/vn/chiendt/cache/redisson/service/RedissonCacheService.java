package vn.chiendt.cache.redisson.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Service interface for Redisson-based caching operations
 */
public interface RedissonCacheService {
    
    /**
     * Store a key-value pair in cache
     * @param key the cache key
     * @param value the cache value
     */
    void put(String key, Object value);
    
    /**
     * Store a key-value pair in cache with TTL
     * @param key the cache key
     * @param value the cache value
     * @param ttl time to live
     * @param timeUnit time unit for TTL
     */
    void put(String key, Object value, long ttl, TimeUnit timeUnit);
    
    /**
     * Get value by key
     * @param key the cache key
     * @param <T> the type of the value
     * @return the cached value or null if not found
     */
    <T> T get(String key);
    
    /**
     * Get value by key with default value
     * @param key the cache key
     * @param defaultValue default value if key not found
     * @param <T> the type of the value
     * @return the cached value or default value
     */
    <T> T get(String key, T defaultValue);
    
    /**
     * Check if key exists in cache
     * @param key the cache key
     * @return true if key exists, false otherwise
     */
    boolean exists(String key);
    
    /**
     * Delete key from cache
     * @param key the cache key
     * @return true if key was deleted, false if key didn't exist
     */
    boolean delete(String key);
    
    /**
     * Delete multiple keys from cache
     * @param keys collection of keys to delete
     * @return number of keys deleted
     */
    long delete(Collection<String> keys);
    
    /**
     * Get all keys matching pattern
     * @param pattern the key pattern
     * @return set of matching keys
     */
    Set<String> getKeys(String pattern);
    
    /**
     * Get multiple values by keys
     * @param keys collection of keys
     * @param <T> the type of the values
     * @return map of key-value pairs
     */
    <T> Map<String, T> getMultiple(Collection<String> keys);
    
    /**
     * Store multiple key-value pairs
     * @param map map of key-value pairs to store
     */
    void putMultiple(Map<String, Object> map);
    
    /**
     * Store multiple key-value pairs with TTL
     * @param map map of key-value pairs to store
     * @param ttl time to live
     * @param timeUnit time unit for TTL
     */
    void putMultiple(Map<String, Object> map, long ttl, TimeUnit timeUnit);
    
    /**
     * Get TTL for a key
     * @param key the cache key
     * @return TTL in seconds, -1 if key doesn't exist, -2 if key exists but has no TTL
     */
    long getTTL(String key);
    
    /**
     * Set TTL for a key
     * @param key the cache key
     * @param ttl time to live
     * @param timeUnit time unit for TTL
     * @return true if TTL was set, false if key doesn't exist
     */
    boolean setTTL(String key, long ttl, TimeUnit timeUnit);
    
    /**
     * Clear all cache entries
     */
    void clear(String key);
    
    /**
     * Get cache size
     * @return number of entries in cache
     */
    long size();
}
