# Dead Letter Topics (DLT) Configuration - Inventory Service

## Tổng quan

Dead Letter Topics (DLT) được cấu hình trong Inventory Service để xử lý các message không thể xử lý thành công sau khi retry. Khi một message thất bại sau số lần retry được cấu hình, nó sẽ được gửi đến DLT để phân tích và xử lý thủ công.

## Cấu hình

### 1. Kafka Consumer Configuration

File: `src/main/java/vn/chiendt/config/KafkaConsumerConfig.java`

- **Retry Policy**: 3 lần retry với delay 3000ms, multiplier 1.5, max delay 15000ms
- **DLT Topic Naming**: `{original-topic}.dlt`
- **Error Handler**: `DefaultErrorHandler` với `DeadLetterPublishingRecoverer`

### 2. Kafka Producer Configuration

File: `src/main/java/vn/chiendt/config/KafkaProducerConfig.java`

- **Reliability**: `acks=all`, `enable.idempotence=true`
- **Retry**: 3 lần retry
- **Serialization**: Hỗ trợ cả String và Avro

### 3. Application Configuration

File: `src/main/resources/application-dev.yml`

```yaml
spring:
  kafka:
    listener:
      missing-topics-fatal: false
      ack-mode: manual_immediate
    consumer:
      enable-auto-commit: false
      auto-offset-reset: earliest
      max-poll-records: 1
    producer:
      acks: all
      retries: 3
      enable-idempotence: true
```

## DLT Topics

### Topics được monitor:

1. **product-sync-events.dlt** - DLT cho product sync events
2. **update-inventory-topic.dlt** - DLT cho inventory update events

## DLT Handler

### File: `src/main/java/vn/chiendt/service/SubscribleKafkaMessage.java`

```java
@DltHandler
public void listenDLT(ConsumerRecord<String, ProductEvent> productEvent,
                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, 
                     @Header(KafkaHeaders.OFFSET) long offset,
                     @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage,
                     @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String exceptionStacktrace)
```

### Chức năng:
- Log chi tiết thông tin message thất bại
- Phân loại lỗi theo loại (Serialization, Database, Timeout, Validation, Business Logic)
- Cung cấp thông tin để debug và fix lỗi

## DLT Message Service

### File: `src/main/java/vn/chiendt/service/DltMessageService.java`

#### Chức năng chính:
1. **processDltMessage()**: Xử lý và log thông tin message DLT
2. **categorizeError()**: Phân loại lỗi để xử lý phù hợp

#### Các loại lỗi được phân loại:
- `SERIALIZATION_ERROR`: Lỗi serialize/deserialize
- `DATABASE_ERROR`: Lỗi kết nối database
- `TIMEOUT_ERROR`: Lỗi timeout
- `VALIDATION_ERROR`: Lỗi validation dữ liệu
- `BUSINESS_LOGIC_ERROR`: Lỗi logic nghiệp vụ
- `UNKNOWN`: Lỗi không xác định

## Monitoring và Debugging

### 1. Logs
- Tất cả DLT messages được log với level ERROR
- Bao gồm: topic, partition, offset, key, value, exception details
- Timestamp và error categorization

### 2. Kafka Topics
```bash
# List DLT topics
kafka-topics --bootstrap-server localhost:29092 --list | grep dlt

# Consume DLT messages
kafka-console-consumer --bootstrap-server localhost:29092 \
  --topic product-sync-events.dlt \
  --from-beginning
```

### 3. Metrics
- Monitor số lượng messages trong DLT topics
- Track error rates và retry attempts
- Alert khi DLT topics có messages mới

## Xử lý DLT Messages

### 1. Phân tích lỗi
- Xem logs để hiểu nguyên nhân lỗi
- Kiểm tra message format và data
- Verify database connectivity và constraints

### 2. Fix và Retry
- Fix lỗi trong application code
- Fix data issues nếu có
- Manually republish messages từ DLT về topic gốc

### 3. Manual Republish
```bash
# Consume từ DLT và republish
kafka-console-consumer --bootstrap-server localhost:29092 \
  --topic product-sync-events.dlt \
  --from-beginning | \
kafka-console-producer --bootstrap-server localhost:29092 \
  --topic product-sync-events
```

## Best Practices

### 1. Monitoring
- Set up alerts cho DLT topics
- Monitor DLT message count
- Track error patterns

### 2. Error Handling
- Implement proper validation
- Handle database connection issues
- Use circuit breakers cho external services

### 3. Data Quality
- Validate message format trước khi process
- Implement schema evolution properly
- Handle backward compatibility

### 4. Operations
- Regular cleanup của DLT topics
- Archive old DLT messages
- Document error patterns và solutions

## Troubleshooting

### Common Issues:

1. **Serialization Errors**
   - Check Avro schema compatibility
   - Verify schema registry connectivity
   - Update consumer deserializer

2. **Database Errors**
   - Check database connectivity
   - Verify table constraints
   - Check transaction isolation levels

3. **Timeout Errors**
   - Increase timeout values
   - Optimize database queries
   - Check network connectivity

4. **Validation Errors**
   - Review business rules
   - Check data format requirements
   - Update validation logic

## Configuration Tuning

### Retry Configuration:
```java
@RetryableTopic(
    attempts = "4", 
    backoff = @Backoff(delay = 3000, multiplier = 1.5, maxDelay = 15000)
)
```

### Error Handler Configuration:
```java
new FixedBackOff(3000L, 3L) // 3 retries với 3s delay
```

## Security Considerations

- DLT topics có thể chứa sensitive data
- Implement proper access controls
- Encrypt DLT topics nếu cần
- Regular cleanup để tránh data exposure

