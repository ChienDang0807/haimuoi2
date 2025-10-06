package vn.chiendt.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.avro.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import vn.chiendt.model.ProductDocument;
import vn.chiendt.repository.ProductSearchRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static vn.chiendt.common.AvroConvert.fromByteBuffer;

@Slf4j
@RequiredArgsConstructor
public class SubscribleKafkaMessage {

    private final ProductSearchRepository productSearchRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${kafka.topic}", groupId = "product-event-group",containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, ProductEvent> record) {
        log.info("Received event: {}");
        ProductEvent event = record.value();

        // Tùy vào loại event mà xử lý khác nhau
        switch (event.getEventType()) {
            case CREATED:
                handleProductCreated(event);
                break;
            case UPDATED:
                handleProductUpdated(event);
                break;
            case DELETED:
                handleProductDeleted(event);
                break;
            default:
                log.error("⚠️ Unknown event type: {}" , event.getEventType());
        }
    }

    private void handleProductDeleted(ProductEvent event) {
        log.info("Product deleted with id {}", event.getId());
    }

    private void handleProductUpdated(ProductEvent event) {
        log.info("Product updated with id {}", event.getId());

        mapProductEvent(event);
    }

    private void mapProductEvent(ProductEvent event) {
        ProductDocument productUpdated = ProductDocument.builder()
                .id(event.getId())
                .slug(event.getSlug().toString())
                .name(event.getName().toString())
                .price(fromByteBuffer(event.getPrice(),10,2))
                .userId(event.getUserId())
                .status(event.getStatus().name())
                .attributes(parseAttributes(event.getAttributes().toString()))
                .createdAt(Instant.ofEpochMilli(event.getCreatedAt()))
                .updatedAt(Instant.ofEpochMilli(event.getUpdatedAt()))
                .build();
        productSearchRepository.save(productUpdated);
    }

    private void handleProductCreated(ProductEvent event) {
        log.info("Product created with id {}", event.getId());

        mapProductEvent(event);

    }

    private Map<String, Object> parseAttributes(String attributes) {
        if (attributes == null || attributes.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(attributes, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            // Log lỗi và trả về map rỗng để tránh crash consumer
            log.error("Failed to parse attributes: {}" ,e.getMessage());
            return Collections.emptyMap();
        }
    }
}
