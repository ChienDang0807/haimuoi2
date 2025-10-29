package vn.chiendt.service.impl;

import com.google.gson.Gson;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.chiendt.common.OrderStatus;
import vn.chiendt.dto.request.PaymentMessage;
import vn.chiendt.dto.request.PlaceOrderRequest;
import vn.chiendt.dto.request.UpdateOrderRequest;
import vn.chiendt.dto.response.OrderListResponse;
import vn.chiendt.exception.InvalidDataException;
import vn.chiendt.exception.ResourceNotFoundException;
import vn.chiendt.model.Order;
import vn.chiendt.model.OrderItem;
import vn.chiendt.repository.OrderRepository;
import vn.chiendt.service.OrderService;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static vn.chiendt.common.OrderStatus.CANCELED;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ORDER-SERVICE")
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String checkoutOrderTopic;

    @Override
    public OrderListResponse getAllOrders(OrderStatus status, String sort, int page, int size) {
        return null;
    }

    @Override
    public Order getOrderDetail(String orderId) {
        log.info("getOrderDetail called");

        return getOrderById(orderId);
    }

    // createOrder thi chua can thong tin thanh toan
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public Order createOrder(PlaceOrderRequest request) {
        log.info("Create order");

        Order order = new Order();
        String orderId = String.valueOf(UUID.randomUUID());

        order.setId(orderId);
        order.setCustomerId(request.getCustomerId());
        order.setAmount(request.getAmount());
        order.setCurrency(request.getCurrency());
        order.setStatus(OrderStatus.NEW.getValue());
        order.setStatusName(OrderStatus.NEW.name());

        order.setCreatedAt(new Date());

        List<OrderItem> orderItems = request.getOrderItems().stream().map(
                item -> OrderItem.builder()
                        .id(UUID.randomUUID().toString())
                        .orderId(orderId)
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .unit(item.getUnit())

                        .build()
        ).toList();
        order.setOrderItems(orderItems);

        Order result = orderRepository.save(order);
        log.info("Order created successfully");

        // push kafka (optional)

        return result;
    }

    @Override
    public void updateOrder(UpdateOrderRequest req) {
        log.info("updateOrder called");

        Order order = getOrderById(req.getId());

        order.setAmount(req.getAmount());
        order.setCurrency(req.getCurrency());
        order.setPaymentMethod(req.getPaymentMethod());
        order.setUpdatedAt(new Date());

        // update status
        if (req.getStatus() != null) {
            order.setStatus(req.getStatus().getValue());
            order.setStatusName(req.getStatus().name());
        }

        // update order items
        List<OrderItem> orderItems = req.getOrderItems().stream().map(
                item -> OrderItem.builder()
                        .orderId(order.getId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unit(item.getUnit())
                        .price(item.getPrice())
                        .build()
        ).toList();
        order.setOrderItems(orderItems);

        orderRepository.save(order);
        log.info("Order updated");
    }

    @Override
    public void checkoutOrder(String orderId) {
        Order order = getOrderById(orderId);

        // Push message sang payment
        PaymentMessage paymentMessage = new PaymentMessage();
        paymentMessage.setOrderId(orderId);
        paymentMessage.setCustomerId(order.getCustomerId());
        paymentMessage.setAmount(order.getAmount());
        paymentMessage.setCurrency(order.getCurrency());
        paymentMessage.setPaymentMethod(order.getPaymentMethod());

        // convert message to json
        try {
            String jsonMessage = new Gson().toJson(paymentMessage);
            kafkaTemplate.send(checkoutOrderTopic, jsonMessage);
            log.info("checkoutOrder sent message to Payment service message: {}", jsonMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InvalidDataException("Push message failed");
        }

        // update status
        order.setStatus(OrderStatus.PAID.getValue());
        order.setStatusName(OrderStatus.PAID.name());
        order.setUpdatedAt(new Date());
        orderRepository.save(order);
        log.info("Order checked out");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderId) {
        log.info("cancelOrder called");

        Order order = getOrderById(orderId);
        order.setStatus(CANCELED.getValue());
        order.setStatusName(CANCELED.name());
        order.setUpdatedAt(new Date());
        orderRepository.save(order);
        log.info("Order cancelled");
    }

    @Override
    public void changeOrderStatus(String orderId, OrderStatus status) {
        log.info("changeOrderStatus called, status: {}", status.name());

        Order order = getOrderById(orderId);
        order.setStatus(status.getValue());
        order.setStatusName(status.name());
        order.setUpdatedAt(new Date());

        orderRepository.save(order);
        log.info("Order changed status");
    }

    @Override
    public BufferedImage generateQRCodeImage(String text) throws WriterException {
        return null;
    }

    @Override
    public BufferedImage generateBarCodeImage(String barcode) throws WriterException {
        return null;
    }

    /**
     * Get order by id
     *
     * @param orderId
     * @return
     */
    private Order getOrderById(String orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }
}
