# Redisson Integration trong Products Service

## Tổng quan

Service Products đã được tích hợp Redisson để cung cấp các tính năng:
- **Distributed Caching**: Cache dữ liệu với TTL và quản lý tự động
- **Distributed Locking**: Đảm bảo thread-safe operations trong môi trường distributed
- **Redis Operations**: Các thao tác Redis cơ bản với high-level API

## Cấu trúc thư mục

```
products/src/main/java/vn/chiendt/cache/redisson/
├── annotation/
│   └── DistributedLock.java          # Annotation cho distributed locking
├── aspect/
│   └── DistributedLockAspect.java    # AOP aspect xử lý distributed lock
├── config/
│   └── RedissonConfig.java           # Cấu hình Redisson client
└── service/
    ├── RedisDistributedService.java  # Interface cho distributed locking
    ├── RedisDistributedLocker.java   # Interface cho lock operations
    ├── RedissonCacheService.java     # Interface cho cache operations
    └── impl/
        ├── RedisDistributedServiceImpl.java  # Implementation distributed locking
        └── RedissonCacheServiceImpl.java     # Implementation cache operations
```

## Cấu hình

### 1. Dependencies (pom.xml)

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.37.0</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### 2. Application Configuration (application-dev.yml)

```yaml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6319
      password: ""
      database: 0

# Redisson configuration
redisson:
  connection-pool-size: 50
  connection-minimum-idle-size: 10
  idle-connection-timeout: 10000
  connect-timeout: 10000
  timeout: 3000
  retry-attempts: 3
  retry-interval: 1500
```

## Sử dụng

### 1. Distributed Locking

Sử dụng annotation `@DistributedLock` trên methods:

```java
@DistributedLock(key = "'product:add:' + #request.name", waitTime = 5000, leaseTime = 10000)
public long addProduct(ProductCreationRequest request) {
    // Business logic here
}
```

**Tham số:**
- `key`: Lock key (hỗ trợ SpEL expressions)
- `waitTime`: Thời gian chờ lock (ms)
- `leaseTime`: Thời gian giữ lock (ms)
- `timeUnit`: Đơn vị thời gian
- `throwOnFailure`: Có throw exception khi không lấy được lock không

### 2. Cache Operations

Inject `RedissonCacheService` và sử dụng:

```java
@Autowired
private RedissonCacheService redissonCacheService;

// Store với TTL
redissonCacheService.put("key", value, 30, TimeUnit.MINUTES);

// Get value
String value = redissonCacheService.get("key");

// Check existence
boolean exists = redissonCacheService.exists("key");

// Delete
boolean deleted = redissonCacheService.delete("key");
```

### 3. Tích hợp trong ProductService

Service đã được tích hợp với:
- **Cache cho product lookup**: Cache kết quả tìm kiếm và get by ID
- **Distributed locking**: Đảm bảo thread-safe cho add/update/delete operations
- **Cache invalidation**: Tự động clear cache khi có thay đổi

## API Endpoints

### Cache Management APIs

- `POST /api/v1/cache/put` - Store key-value pair
- `GET /api/v1/cache/get` - Get value by key
- `GET /api/v1/cache/exists` - Check key existence
- `DELETE /api/v1/cache/delete` - Delete key
- `GET /api/v1/cache/keys` - Get keys by pattern
- `GET /api/v1/cache/ttl` - Get TTL for key
- `POST /api/v1/cache/ttl` - Set TTL for key
- `GET /api/v1/cache/size` - Get cache size
- `DELETE /api/v1/cache/clear` - Clear all cache
- `POST /api/v1/cache/demo` - Demo cache operations

## Testing

### Unit Tests

Chạy tests:
```bash
mvn test -Dtest=RedissonCacheServiceTest
```

### Manual Testing

1. **Start Redis server**:
```bash
redis-server --port 6319
```

2. **Start application**:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

3. **Test cache operations**:
```bash
# Store value
curl -X POST "http://localhost:8084/api/v1/cache/put?key=test&value=hello"

# Get value
curl "http://localhost:8084/api/v1/cache/get?key=test"

# Check existence
curl "http://localhost:8084/api/v1/cache/exists?key=test"
```

## Best Practices

### 1. Lock Key Design
- Sử dụng prefix để tránh conflict: `"product:add:" + productName`
- Key phải unique và có ý nghĩa
- Tránh key quá dài

### 2. Cache Strategy
- Set TTL phù hợp (30 phút cho product data)
- Cache invalidation khi có update
- Sử dụng pattern matching để clear related cache

### 3. Error Handling
- Luôn có fallback khi cache fail
- Log đầy đủ cho debugging
- Handle timeout và connection issues

### 4. Performance
- Sử dụng async operations khi có thể
- Monitor cache hit ratio
- Tối ưu connection pool size

## Monitoring

### Logs
- Cache operations được log với level DEBUG
- Lock operations được log với level INFO
- Errors được log với level ERROR

### Metrics
- Cache hit/miss ratio
- Lock acquisition time
- Connection pool usage

## Troubleshooting

### Common Issues

1. **Connection timeout**:
   - Kiểm tra Redis server đang chạy
   - Verify host/port configuration
   - Check network connectivity

2. **Lock timeout**:
   - Tăng `waitTime` nếu cần
   - Kiểm tra deadlock scenarios
   - Monitor lock contention

3. **Cache inconsistency**:
   - Verify cache invalidation logic
   - Check TTL settings
   - Monitor cache size

### Debug Commands

```bash
# Check Redis connection
redis-cli -p 6319 ping

# List all keys
redis-cli -p 6319 keys "*"

# Monitor Redis commands
redis-cli -p 6319 monitor
```
