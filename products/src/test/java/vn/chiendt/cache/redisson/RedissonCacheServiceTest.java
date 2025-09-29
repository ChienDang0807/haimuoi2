package vn.chiendt.cache.redisson;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import vn.chiendt.cache.redisson.service.RedissonCacheService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RedissonCacheServiceTest {

    @Autowired
    private RedissonCacheService redissonCacheService;

    @Test
    public void testBasicCacheOperations() {
        String key = "test:key";
        String value = "test:value";

        // Test put and get
        redissonCacheService.put(key, value);
        String retrievedValue = redissonCacheService.get(key);
        assertEquals(value, retrievedValue);

        // Test exists
        assertTrue(redissonCacheService.exists(key));

        // Test delete
        boolean deleted = redissonCacheService.delete(key);
        assertTrue(deleted);
        assertFalse(redissonCacheService.exists(key));
    }

    @Test
    public void testCacheWithTTL() throws InterruptedException {
        String key = "test:ttl:key";
        String value = "test:ttl:value";
        long ttl = 2; // 2 seconds

        // Put with TTL
        redissonCacheService.put(key, value, ttl, TimeUnit.SECONDS);
        assertTrue(redissonCacheService.exists(key));

        // Wait for TTL to expire
        Thread.sleep(3000);

        // Should not exist after TTL
        assertFalse(redissonCacheService.exists(key));
    }

    @Test
    public void testGetWithDefaultValue() {
        String key = "test:default:key";
        String defaultValue = "default:value";

        // Get non-existent key with default
        String result = redissonCacheService.get(key, defaultValue);
        assertEquals(defaultValue, result);

        // Put value and get again
        String actualValue = "actual:value";
        redissonCacheService.put(key, actualValue);
        result = redissonCacheService.get(key, defaultValue);
        assertEquals(actualValue, result);
    }

    @Test
    public void testMultipleOperations() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("test:multi:key1", "value1");
        testData.put("test:multi:key2", "value2");
        testData.put("test:multi:key3", "value3");

        // Put multiple values
        redissonCacheService.putMultiple(testData);

        // Get multiple values
        Map<String, String> retrieved = redissonCacheService.getMultiple(testData.keySet());
        assertEquals(testData.size(), retrieved.size());
        assertEquals("value1", retrieved.get("test:multi:key1"));
        assertEquals("value2", retrieved.get("test:multi:key2"));
        assertEquals("value3", retrieved.get("test:multi:key3"));

        // Delete multiple keys
        long deletedCount = redissonCacheService.delete(testData.keySet());
        assertEquals(testData.size(), deletedCount);
    }

    @Test
    public void testGetKeysWithPattern() {
        // Put some test keys
        redissonCacheService.put("test:pattern:key1", "value1");
        redissonCacheService.put("test:pattern:key2", "value2");
        redissonCacheService.put("test:other:key3", "value3");

        // Get keys with pattern
        Set<String> patternKeys = redissonCacheService.getKeys("test:pattern:*");
        assertEquals(2, patternKeys.size());
        assertTrue(patternKeys.contains("test:pattern:key1"));
        assertTrue(patternKeys.contains("test:pattern:key2"));

        // Clean up
        redissonCacheService.delete(Set.of("test:pattern:key1", "test:pattern:key2", "test:other:key3"));
    }

    @Test
    public void testTTLOperations() {
        String key = "test:ttl:operations";
        String value = "test:value";

        // Put value
        redissonCacheService.put(key, value);
        assertTrue(redissonCacheService.exists(key));

        // Set TTL
        boolean ttlSet = redissonCacheService.setTTL(key, 5, TimeUnit.SECONDS);
        assertTrue(ttlSet);

        // Get TTL
        long ttl = redissonCacheService.getTTL(key);
        assertTrue(ttl > 0);
        assertTrue(ttl <= 5000); // Should be around 5 seconds in milliseconds

        // Clean up
        redissonCacheService.delete(key);
    }
}
