package vn.chiendt.cache.redisson.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RedissonConfig {
    
    @Value("${spring.data.redis.host:127.0.0.1}")
    private String redisHost;
    
    @Value("${spring.data.redis.port:6319}")
    private int redisPort;
    
    @Value("${spring.data.redis.password:}")
    private String redisPassword;
    
    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;
    
    @Value("${redisson.connection-pool-size:50}")
    private int connectionPoolSize;
    
    @Value("${redisson.connection-minimum-idle-size:10}")
    private int connectionMinimumIdleSize;
    
    @Value("${redisson.idle-connection-timeout:10000}")
    private int idleConnectionTimeout;
    
    @Value("${redisson.connect-timeout:10000}")
    private int connectTimeout;
    
    @Value("${redisson.timeout:3000}")
    private int timeout;
    
    @Value("${redisson.retry-attempts:3}")
    private int retryAttempts;
    
    @Value("${redisson.retry-interval:1500}")
    private int retryInterval;

    @Bean
    public RedissonClient redissonClient() {
        try {
            Config config = new Config();
            
            String redisUrl = String.format("redis://%s:%d", redisHost, redisPort);
            log.info("Connecting to Redis at: {}", redisUrl);
            
            config.useSingleServer()
                    .setAddress(redisUrl)
                    .setDatabase(redisDatabase)
                    .setConnectionPoolSize(connectionPoolSize)
                    .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                    .setIdleConnectionTimeout(idleConnectionTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setTimeout(timeout)
                    .setRetryAttempts(retryAttempts)
                    .setRetryInterval(retryInterval);
            
            // Set password if provided
            if (redisPassword != null && !redisPassword.trim().isEmpty()) {
                config.useSingleServer().setPassword(redisPassword);
            }
            
            RedissonClient redissonClient = Redisson.create(config);
            log.info("Redisson client created successfully");
            
            return redissonClient;
        } catch (Exception e) {
            log.error("Failed to create Redisson client", e);
            throw new RuntimeException("Failed to create Redisson client", e);
        }
    }
}
