package vn.chiendt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.cache.redisson.service.RedissonCacheService;
import vn.chiendt.dto.response.ApiResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/cache")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cache Management", description = "APIs for managing Redis cache using Redisson")
public class CacheController {

    private final RedissonCacheService redissonCacheService;

    @PostMapping("/put")
    @Operation(summary = "Store a key-value pair in cache")
    public ResponseEntity<ApiResponse> putCache(
            @Parameter(description = "Cache key") @RequestParam String key,
            @Parameter(description = "Cache value") @RequestParam String value,
            @Parameter(description = "TTL in seconds (optional)") @RequestParam(required = false) Long ttl) {
        
        try {
            if (ttl != null && ttl > 0) {
                redissonCacheService.put(key, value, ttl, TimeUnit.SECONDS);
                log.info("Stored key: {} with value: {} and TTL: {} seconds", key, value, ttl);
            } else {
                redissonCacheService.put(key, value);
                log.info("Stored key: {} with value: {}", key, value);
            }
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(200)
                    .message("Cache entry stored successfully")
                    .data("Cache entry stored successfully")
                    .build());
        } catch (Exception e) {
            log.error("Failed to store cache entry", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message("Failed to store cache entry: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/get")
    @Operation(summary = "Get value by key")
    public ResponseEntity<ApiResponse> getCache(
            @Parameter(description = "Cache key") @RequestParam String key) {
        
        try {
            Object value = redissonCacheService.get(key);
            if (value != null) {
                log.info("Retrieved key: {} with value: {}", key, value);
                return ResponseEntity.ok(ApiResponse.builder()
                        .status(200)
                        .message("Cache entry retrieved successfully")
                        .data(value)
                        .build());
            } else {
                log.info("Key: {} not found in cache", key);
                return ResponseEntity.ok(ApiResponse.builder()
                        .status(200)
                        .message("Key not found")
                        .data(null)
                        .build());
            }
        } catch (Exception e) {
            log.error("Failed to retrieve cache entry", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message("Failed to retrieve cache entry: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/exists")
    @Operation(summary = "Check if key exists in cache")
    public ResponseEntity<ApiResponse> existsCache(
            @Parameter(description = "Cache key") @RequestParam String key) {
        
        try {
            boolean exists = redissonCacheService.exists(key);
            log.info("Key: {} exists: {}", key, exists);
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(200)
                    .message("Key existence checked successfully")
                    .data(exists)
                    .build());
        } catch (Exception e) {
            log.error("Failed to check cache key existence", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message("Failed to check cache key existence: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete key from cache")
    public ResponseEntity<ApiResponse> deleteCache(
            @Parameter(description = "Cache key") @RequestParam String key) {
        
        try {
            boolean deleted = redissonCacheService.delete(key);
            log.info("Key: {} deleted: {}", key, deleted);
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(200)
                    .message("Cache entry deletion completed")
                    .data(deleted)
                    .build());
        } catch (Exception e) {
            log.error("Failed to delete cache entry", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message("Failed to delete cache entry: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/keys")
    @Operation(summary = "Get keys matching pattern")
    public ResponseEntity<ApiResponse> getKeys(
            @Parameter(description = "Key pattern (e.g., 'test:*')") @RequestParam String pattern) {
        
        try {
            Set<String> keys = redissonCacheService.getKeys(pattern);
            log.info("Found {} keys matching pattern: {}", keys.size(), pattern);
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(200)
                    .message("Keys retrieved successfully")
                    .data(keys)
                    .build());
        } catch (Exception e) {
            log.error("Failed to get keys with pattern", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message("Failed to get keys: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/ttl")
    @Operation(summary = "Get TTL for a key")
    public ResponseEntity<ApiResponse> getTTL(
            @Parameter(description = "Cache key") @RequestParam String key) {
        
        try {
            long ttl = redissonCacheService.getTTL(key);
            log.info("Key: {} TTL: {} ms", key, ttl);
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(200)
                    .message("TTL retrieved successfully")
                    .data(ttl)
                    .build());
        } catch (Exception e) {
            log.error("Failed to get TTL for key", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message("Failed to get TTL: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping("/ttl")
    @Operation(summary = "Set TTL for a key")
    public ResponseEntity<ApiResponse> setTTL(
            @Parameter(description = "Cache key") @RequestParam String key,
            @Parameter(description = "TTL in seconds") @RequestParam Long ttl) {
        
        try {
            boolean success = redissonCacheService.setTTL(key, ttl, TimeUnit.SECONDS);
            log.info("Set TTL for key: {} to {} seconds - success: {}", key, ttl, success);
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(200)
                    .message("TTL set successfully")
                    .data(success)
                    .build());
        } catch (Exception e) {
            log.error("Failed to set TTL for key", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message("Failed to set TTL: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/size")
    @Operation(summary = "Get cache size")
    public ResponseEntity<ApiResponse> getCacheSize() {
        try {
            long size = redissonCacheService.size();
            log.info("Cache size: {}", size);
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(200)
                    .message("Cache size retrieved successfully")
                    .data(size)
                    .build());
        } catch (Exception e) {
            log.error("Failed to get cache size", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message("Failed to get cache size: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear all cache entries")
    public ResponseEntity<ApiResponse> clearCache(@RequestParam String key) {
        try {
            redissonCacheService.clear(key);
            log.info("Cache cleared successfully");
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(200)
                    .message("Cache cleared successfully")
                    .data("Cache cleared successfully")
                    .build());
        } catch (Exception e) {
            log.error("Failed to clear cache", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message("Failed to clear cache: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping("/demo")
    @Operation(summary = "Demo cache operations")
    public ResponseEntity<ApiResponse> demoCacheOperations() {
        try {
            Map<String, Object> demoData = new HashMap<>();
            demoData.put("demo:key1", "value1");
            demoData.put("demo:key2", "value2");
            demoData.put("demo:key3", "value3");

            // Store multiple values
            redissonCacheService.putMultiple(demoData, 60, TimeUnit.SECONDS);

            // Retrieve multiple values
            Map<String, Object> retrieved = redissonCacheService.getMultiple(demoData.keySet());

            Map<String, Object> result = new HashMap<>();
            result.put("stored", demoData);
            result.put("retrieved", retrieved);
            result.put("cache_size", redissonCacheService.size());

            log.info("Demo cache operations completed");
            return ResponseEntity.ok(ApiResponse.builder()
                    .status(200)
                    .message("Demo cache operations completed successfully")
                    .data(result)
                    .build());
        } catch (Exception e) {
            log.error("Failed to perform demo cache operations", e);
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .status(400)
                    .message("Failed to perform demo operations: " + e.getMessage())
                    .build());
        }
    }
}