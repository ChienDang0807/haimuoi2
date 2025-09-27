package vn.chiendt.cache.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class RedisServiceImpl implements RedisService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // SET key value(string)
    @Override
    public void setString(String key, String value) {
        if (!StringUtils.hasLength(key)) { // key null hoặc rỗng
            log.warn("Skip setString: key is null or empty");
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    // GET key
    @Override
    public String getString(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .map(String::valueOf)
                .orElse(null);
    }

    @Override
    public void setObject(String key, Object value) {
        if (!StringUtils.hasLength(key)) {
            log.warn("Skip setObject: key is null or empty");
            return;
        }
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Error while setting object to Redis with key {}: {}", key, e.getMessage());
        }
    }

    @Override
    public <T> T  getObject(String key, Class<T> targetClass) {
        Object result = redisTemplate.opsForValue().get(key);
        log.debug("Get cache for key {}: {}", key, result);

        if (result == null) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        // Nếu Redis lưu dạng Map (vd: do Jackson lưu trực tiếp object)
        if (result instanceof Map) {
            try {
                return objectMapper.convertValue(result, targetClass);
            } catch (IllegalArgumentException e) {
                log.error("Failed to convert LinkedHashMap to {}: {}", targetClass.getSimpleName(), e.getMessage());
                return null;
            }
        }

        // Nếu Redis lưu dạng String (JSON)
        if (result instanceof String stringResult) {
            try {
                return objectMapper.readValue(stringResult, targetClass);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize JSON to {}: {}", targetClass.getSimpleName(), e.getMessage());
                return null;
            }
        }

        log.warn("Unsupported Redis value type: {}", result.getClass());
        return null;
    }

}

