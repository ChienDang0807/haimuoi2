package vn.chiendt.cache.redisson.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RObject;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import vn.chiendt.cache.redisson.service.RedissonCacheService;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RedissonCacheServiceImpl implements RedissonCacheService {
    
    @Resource
    private RedissonClient redissonClient;
    
    @Override
    public void put(String key, Object value) {
        try {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            bucket.set(value);
            log.debug("Stored key: {} with value: {}", key, value);
        } catch (Exception e) {
            log.error("Failed to store key: {} with value: {}", key, value, e);
            throw new RuntimeException("Failed to store cache entry", e);
        }
    }
    
    @Override
    public void put(String key, Object value, long ttl, TimeUnit timeUnit) {
        try {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            bucket.set(value, ttl, timeUnit);
            log.debug("Stored key: {} with value: {} and TTL: {} {}", key, value, ttl, timeUnit);
        } catch (Exception e) {
            log.error("Failed to store key: {} with value: {} and TTL: {} {}", key, value, ttl, timeUnit, e);
            throw new RuntimeException("Failed to store cache entry with TTL", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        try {
            RBucket<T> bucket = redissonClient.getBucket(key);
            T value = bucket.get();
            log.debug("Retrieved key: {} with value: {}", key, value);
            return value;
        } catch (Exception e) {
            log.error("Failed to retrieve key: {}", key, e);
            throw new RuntimeException("Failed to retrieve cache entry", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        try {
            RBucket<T> bucket = redissonClient.getBucket(key);
            T value = bucket.get();
            if (value == null) {
                log.debug("Key: {} not found, returning default value: {}", key, defaultValue);
                return defaultValue;
            }
            log.debug("Retrieved key: {} with value: {}", key, value);
            return value;
        } catch (Exception e) {
            log.error("Failed to retrieve key: {} with default value: {}", key, defaultValue, e);
            return defaultValue;
        }
    }
    
    @Override
    public boolean exists(String key) {
        try {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            boolean exists = bucket.isExists();
            log.debug("Key: {} exists: {}", key, exists);
            return exists;
        } catch (Exception e) {
            log.error("Failed to check existence of key: {}", key, e);
            return false;
        }
    }
    
    @Override
    public boolean delete(String key) {
        try {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            boolean deleted = bucket.delete();
            log.debug("Key: {} deleted: {}", key, deleted);
            return deleted;
        } catch (Exception e) {
            log.error("Failed to delete key: {}", key, e);
            return false;
        }
    }
    
    @Override
    public long delete(Collection<String> keys) {
        try {
            long deletedCount = 0;
            for (String key : keys) {
                if (delete(key)) {
                    deletedCount++;
                }
            }
            log.debug("Deleted {} out of {} keys", deletedCount, keys.size());
            return deletedCount;
        } catch (Exception e) {
            log.error("Failed to delete keys: {}", keys, e);
            return 0;
        }
    }
    
    @Override
    public Set<String> getKeys(String pattern) {
        try {
            RKeys keys = redissonClient.getKeys();
            Iterable<String> keysIterable = keys.getKeysByPattern(pattern);
            Set<String> keySet = new HashSet<>();
            for (String key : keysIterable) {
                keySet.add(key);
            }
            log.debug("Found {} keys matching pattern: {}", keySet.size(), pattern);
            return keySet;
        } catch (Exception e) {
            log.error("Failed to get keys with pattern: {}", pattern, e);
            return Collections.emptySet();
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getMultiple(Collection<String> keys) {
        try {
            Map<String, T> result = new HashMap<>();
            for (String key : keys) {
                RBucket<T> bucket = redissonClient.getBucket(key);
                T value = bucket.get();
                if (value != null) {
                    result.put(key, value);
                }
            }
            log.debug("Retrieved {} values out of {} keys", result.size(), keys.size());
            return result;
        } catch (Exception e) {
            log.error("Failed to get multiple keys: {}", keys, e);
            return Collections.emptyMap();
        }
    }
    
    @Override
    public void putMultiple(Map<String, Object> map) {
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
            log.debug("Stored {} key-value pairs", map.size());
        } catch (Exception e) {
            log.error("Failed to store multiple key-value pairs: {}", map.keySet(), e);
            throw new RuntimeException("Failed to store multiple cache entries", e);
        }
    }
    
    @Override
    public void putMultiple(Map<String, Object> map, long ttl, TimeUnit timeUnit) {
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue(), ttl, timeUnit);
            }
            log.debug("Stored {} key-value pairs with TTL: {} {}", map.size(), ttl, timeUnit);
        } catch (Exception e) {
            log.error("Failed to store multiple key-value pairs with TTL: {}", map.keySet(), e);
            throw new RuntimeException("Failed to store multiple cache entries with TTL", e);
        }
    }
    
    @Override
    public long getTTL(String key) {
        try {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            long ttl = bucket.remainTimeToLive();
            log.debug("Key: {} TTL: {} ms", key, ttl);
            return ttl;
        } catch (Exception e) {
            log.error("Failed to get TTL for key: {}", key, e);
            return -1;
        }
    }
    
    @Override
    public boolean setTTL(String key, long ttl, TimeUnit timeUnit) {
        try {
            RBucket<Object> bucket = redissonClient.getBucket(key);
            boolean success = bucket.expire(ttl, timeUnit);
            log.debug("Set TTL for key: {} to {} {} - success: {}", key, ttl, timeUnit, success);
            return success;
        } catch (Exception e) {
            log.error("Failed to set TTL for key: {} to {} {}", key, ttl, timeUnit, e);
            return false;
        }
    }
    
    @Override
    public void clear(String string) {
        try {
            RKeys keys = redissonClient.getKeys();
            long deletedCount = keys.delete(string);
            log.info("Cleared cache, deleted {} keys", deletedCount);
        } catch (Exception e) {
            log.error("Failed to clear cache", e);
            throw new RuntimeException("Failed to clear cache", e);
        }
    }
    
    @Override
    public long size() {
        try {
            RKeys keys = redissonClient.getKeys();
            long count = keys.count();
            log.debug("Cache size: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Failed to get cache size", e);
            return 0;
        }
    }
}
