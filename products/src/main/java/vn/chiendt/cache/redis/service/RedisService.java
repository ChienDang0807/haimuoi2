package vn.chiendt.cache.redis.service;

public interface RedisService {
    void setString(String key, String value);
    String getString(String key);

    void setObject(String key, Object value);
    <T> T getObject(String key, Class<T> targetClass);
}
