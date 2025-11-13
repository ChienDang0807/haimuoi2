package vn.chiendt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import vn.chiendt.avro.ProductEvent;
import vn.chiendt.avro.ProductEventType;
import vn.chiendt.common.Currency;
import vn.chiendt.common.OrderStatus;
import vn.chiendt.common.PaymentMethod;
import vn.chiendt.model.InventoryItem;
import vn.chiendt.model.SaleOrder;
import vn.chiendt.model.SaleOrderItem;
import vn.chiendt.repository.SaleOrderItemRepository;
import vn.chiendt.repository.SaleOrderRepository;
import vn.chiendt.service.impl.InventoryTransactionServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "TRANSACTION-LISTENER")
public class SubscribleKafkaMessage {

    private final SaleOrderRepository saleOrderRepository;
    private final SaleOrderItemRepository saleOrderItemRepository;
    private final InventoryTransactionServiceImpl inventoryTransactionService;
    private final InventoryItemService inventoryItemService;
    private final DltMessageService dltMessageService;

    @KafkaListener(topics = "update-inventory-topic", groupId = "update-inventory-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleEventUpdateInventory(String message) throws IOException {
        log.info("handleEventUpdateInventory called, message: {}", message);

        // Convert Json to Object
        OrderMessage orderMessage = new ObjectMapper().readValue(message, OrderMessage.class);

        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setId(orderMessage.getId());
        saleOrder.setCustomerId(orderMessage.getCustomerId());
        saleOrder.setTotalAmount(orderMessage.getAmount());
        saleOrder.setCurrency(orderMessage.getCurrency());
        saleOrder.setPaymentMethod(orderMessage.getPaymentMethod());
        saleOrder.setStatus(OrderStatus.valueOf(orderMessage.getStatusName()));

        SaleOrder result = saleOrderRepository.save(saleOrder);
        log.info("SaleOder saved");

        if (result.getId() != null) {
            List<SaleOrderItem> saleOrderItems = new ArrayList<>();
            for (OrderItem item : orderMessage.getOrderItems()) {
                SaleOrderItem orderItem = new SaleOrderItem();
                orderItem.setSalesId(result.getId());
                orderItem.setProductId(item.getProductId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice());

                saleOrderItems.add(orderItem);
            }

            saleOrderItemRepository.saveAll(saleOrderItems);
            log.info("saleOrderItems saved");
        }

        log.info("saleOrder created id: {}", result.getId());
    }

    @RetryableTopic(attempts = "4", backoff = @Backoff(delay = 3000, multiplier = 1.5 , maxDelay = 15000))
    @KafkaListener(topics = "product-sync-events", groupId = "inventory-product-sync-group", containerFactory = "avroKafkaListenerContainerFactory")
    public void handleEventCreateProduct(ConsumerRecord<String, ProductEvent> productEvent) throws IOException {
        log.info("handleEventCreateProduct called, message: {}", productEvent);

        ProductEvent event = productEvent.value();
        
        if (ProductEventType.CREATED == event.getEventType()) {
            log.info("Processing product creation event for product ID: {}", event.getId());
            
            try {
                Integer initialQuantity = 0; // Default initial stock

                InventoryItem initInventoryItem = inventoryItemService.initializeInventory(event.getId(),initialQuantity);
                log.info("Inventory item init for product {}:", initInventoryItem.getId());

                Long transactionId = inventoryTransactionService.initializeProductInventory(
                    event.getId(), 
                    initialQuantity,
                    initInventoryItem.getId()
                );
                
                log.info("Inventory transaction initialized for product ID: {} with transaction ID: {}",
                    event.getId(), transactionId);
                    
            } catch (Exception e) {
                log.error("Failed to initialize inventory for product ID: {}", event.getId(), e);
            }
        } else if ( ProductEventType.DELETED == event.getEventType()) {
            log.info("Processing product deletion event for product ID: {}", event.getId());
            // Handle product deletion - you might want to mark inventory as inactive
            // or archive the inventory records
        } else if ( ProductEventType.UPDATED == event.getEventType()) {
            log.info("Processing product update event for product ID: {}", event.getId());
            // Handle product updates if needed
        }
    }

    @DltHandler
    public void listenDLT(ConsumerRecord<String, ProductEvent> productEvent,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, 
                         @Header(KafkaHeaders.OFFSET) long offset,
                         @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage,
                         @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String exceptionStacktrace) {
        
        log.error("=== DEAD LETTER TOPIC HANDLER ===");
        log.error("Message failed to process and sent to DLT: {}", topic);
        
        // Sử dụng DltMessageService để xử lý message
        dltMessageService.processDltMessage(productEvent, exceptionMessage, exceptionStacktrace);
        
        // Phân loại lỗi để xử lý phù hợp
        DltMessageService.DltErrorType errorType = dltMessageService.categorizeError(exceptionMessage);
        log.error("Error categorized as: {} - {}", errorType.name(), errorType.getDescription());
        
        // Xử lý theo từng loại lỗi
        switch (errorType) {
            case SERIALIZATION_ERROR:
                log.error("Serialization error detected - message format may be corrupted");
                break;
            case DATABASE_ERROR:
                log.error("Database error detected - check database connectivity and constraints");
                break;
            case TIMEOUT_ERROR:
                log.error("Timeout error detected - consider increasing timeout values");
                break;
            case VALIDATION_ERROR:
                log.error("Validation error detected - check data format and business rules");
                break;
            case BUSINESS_LOGIC_ERROR:
                log.error("Business logic error detected - review application logic");
                break;
            default:
                log.error("Unknown error type - manual investigation required");
        }
        
        log.error("=== END DLT HANDLER ===");
    }

    @Getter
    @Setter
    private static class OrderMessage {
        private String id;
        private Long customerId;
        private Long amount;
        private Currency currency;
        private PaymentMethod paymentMethod;
        private int status;
        private String statusName;
        private Date createdAt;
        private Date updatedAt;
        // Nested object
        private List<OrderItem> orderItems;
    }

    @Getter
    @Setter
    private static class OrderItem {
        private String id;
        private String orderId;
        private Long productId;
        private String productName;
        private Integer quantity;
        private Long price;
        private String unit;
    }
}
