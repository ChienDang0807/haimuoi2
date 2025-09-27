package vn.chiendt.cache.redisson.service;

public interface RedisDistributedService {
    RedisDistributedLocker getDistributedLock(String lockKey);
}
