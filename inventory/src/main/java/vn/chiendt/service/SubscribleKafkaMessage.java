package vn.chiendt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.chiendt.common.Currency;
import vn.chiendt.common.OrderStatus;
import vn.chiendt.common.PaymentMethod;
import vn.chiendt.model.SaleOrder;
import vn.chiendt.model.SaleOrderItem;
import vn.chiendt.repository.SaleOrderItemRepository;
import vn.chiendt.repository.SaleOrderRepository;

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

    @KafkaListener(topics = "update-inventory-topic", groupId = "update-inventory-group")
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
