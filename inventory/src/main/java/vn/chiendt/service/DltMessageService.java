package vn.chiendt.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import vn.chiendt.avro.ProductEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service để xử lý các message trong Dead Letter Topics
 */
@Service
@Slf4j(topic = "DLT-SERVICE")
public class DltMessageService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Xử lý message từ DLT với thông tin chi tiết
     */
    public void processDltMessage(ConsumerRecord<String, ProductEvent> dltMessageRecord ,
                                String exceptionMessage, 
                                String exceptionStacktrace) {
        
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        
        log.error("=== DLT MESSAGE PROCESSING - {} ===", timestamp);
        log.error("Topic: {}", dltMessageRecord.topic());
        log.error("Partition: {}, Offset: {}", dltMessageRecord.partition(), dltMessageRecord.offset());
        log.error("Key: {}", dltMessageRecord.key());
        log.error("Timestamp: {}", dltMessageRecord.timestamp());
        
        if (dltMessageRecord.value() != null) {
            ProductEvent event = dltMessageRecord.value();
            log.error("Product Event - ID: {}, Type: {}, Timestamp: {}", 
                event.getId(), event.getEventType(), event.getTimestamp());
        }
        
        log.error("Exception: {}", exceptionMessage);
        log.error("Stacktrace: {}", exceptionStacktrace);

        
        log.error("=== END DLT PROCESSING ===");
    }

    /**
     * Phân loại lỗi để xử lý phù hợp
     */
    public DltErrorType categorizeError(String exceptionMessage) {
        if (exceptionMessage == null) {
            return DltErrorType.UNKNOWN;
        }
        
        String lowerMessage = exceptionMessage.toLowerCase();
        
        if (lowerMessage.contains("deserialization") || lowerMessage.contains("serialization")) {
            return DltErrorType.SERIALIZATION_ERROR;
        } else if (lowerMessage.contains("database") || lowerMessage.contains("connection")) {
            return DltErrorType.DATABASE_ERROR;
        } else if (lowerMessage.contains("timeout")) {
            return DltErrorType.TIMEOUT_ERROR;
        } else if (lowerMessage.contains("validation") || lowerMessage.contains("constraint")) {
            return DltErrorType.VALIDATION_ERROR;
        } else {
            return DltErrorType.BUSINESS_LOGIC_ERROR;
        }
    }

    /**
     * Enum để phân loại các loại lỗi
     */
    @Getter
    public enum DltErrorType {
        SERIALIZATION_ERROR("Lỗi serialize/deserialize message"),
        DATABASE_ERROR("Lỗi kết nối hoặc thao tác database"),
        TIMEOUT_ERROR("Lỗi timeout"),
        VALIDATION_ERROR("Lỗi validation dữ liệu"),
        BUSINESS_LOGIC_ERROR("Lỗi logic nghiệp vụ"),
        UNKNOWN("Lỗi không xác định");

        private final String description;

        DltErrorType(String description) {
            this.description = description;
        }

    }
}
