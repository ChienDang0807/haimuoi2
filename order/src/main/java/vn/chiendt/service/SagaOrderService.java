package vn.chiendt.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import vn.chiendt.common.Currency;
import vn.chiendt.common.OrderStatus;
import vn.chiendt.common.PaymentMethod;
import vn.chiendt.dto.request.PaymentIntentRequest;
import vn.chiendt.dto.request.PlaceOrderRequest;
import vn.chiendt.dto.request.SaleOrderCreationRequest;
import vn.chiendt.dto.request.SaleOrderItemCreationRequest;
import vn.chiendt.dto.response.ApiResponse;
import vn.chiendt.dto.response.PaymentIntentResponse;
import vn.chiendt.feign.InventoryServiceClient;
import vn.chiendt.feign.PaymentServiceClient;
import vn.chiendt.model.Order;
import vn.chiendt.model.OrderItem;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SAGA-ORDER-SERVICE")
public class SagaOrderService {

    private final OrderService orderService;
    private InventoryServiceClient inventoryServiceClient;
    private PaymentServiceClient paymentServiceClient;

    @Value("${api.internal.paymentUrl}")
    private String paymentUrl;
    @Value("${api.internal.saleOrderUrl}")
    private String saleOrderUrl;

    public String createSagaOrder(PlaceOrderRequest request) {
        log.info("createSagaOrder called");

        String orderId = "";
        String saleOrderId = "";
        String paymentId = "";
        try {
            // Create order
            Order order = orderService.createOrder(request);
            orderId = order.getId();

            // Call process init payment intent
            PaymentIntentResponse response = payOrder(order);
            paymentId = response.getPaymentId();

            // Synchronize data with inventory service
            saleOrderId = createSaleOrder(order);

            // return clientSecret for frontend continue process confirm payment with Stripe
            return response.getClientSecret();
        } catch (Exception e) {
            log.error("createSagaOrder error", e);

            if (StringUtils.hasLength(orderId)) {
                orderService.cancelOrder(orderId);
            }
            if (StringUtils.hasLength(paymentId)) {
                refund(paymentId);
            }
            if (StringUtils.hasLength(saleOrderId)) {
                cancelSaleOrder(saleOrderId);
            }
            throw e;
        }
    }

    /**
     * Process payment
     *
     * @param order
     * @return
     */
    private PaymentIntentResponse payOrder(Order order) {
        PaymentIntentRequest request = new PaymentIntentRequest();
        request.setCustomerId(order.getCustomerId());
        request.setOrderId(order.getId());
        request.setPaymentMethod(PaymentMethod.CARD);
        request.setAmount(50000L);
        request.setCurrency(Currency.USD);
        request.setDescription("saga_" + order.getCustomerId() + "_" + order.getId());

        try {
            Map<String, String> responseBody =  paymentServiceClient.createPaymentIntent(request).getBody();
            if(responseBody != null) {
                return PaymentIntentResponse.builder()
                        .paymentId(responseBody.get("paymentId"))
                        .clientSecret(responseBody.get("clientSecret"))
                        .build();
            } else {
                throw new ResourceAccessException("Response body is null");
            }
        } catch (Exception e) {
            throw new ResourceAccessException("Error creating payment intent");
        }
    }

    /**
     * Refund for order was canceled
     *
     * @param paymentId
     */
    private void refund(String paymentId) {
        log.info("refund called");

        paymentServiceClient.createRefund(paymentId);

    }

    /**
     * Update inventory
     *
     * @param order
     */
    private String createSaleOrder(Order order) {
        log.info("createSaleOrder called");

        SaleOrderCreationRequest orderRequest = new SaleOrderCreationRequest();
        orderRequest.setId(order.getId());
        orderRequest.setCustomerId(order.getCustomerId());
        orderRequest.setStatus(OrderStatus.PENDING);
        orderRequest.setTotalAmount(order.getAmount());
        orderRequest.setCurrency(order.getCurrency());
        orderRequest.setPaymentMethod(order.getPaymentMethod());

        List<SaleOrderItemCreationRequest> items = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            SaleOrderItemCreationRequest orderItemRequest = new SaleOrderItemCreationRequest();
            orderItemRequest.setSalesId(order.getId());
            orderItemRequest.setProductId(item.getProductId());
            orderItemRequest.setQuantity(item.getQuantity());
            orderItemRequest.setPrice(item.getPrice());
            items.add(orderItemRequest);

            orderRequest.setItems(items);
        }

            ApiResponse response = inventoryServiceClient.createSaleOrder(orderRequest);
            log.info("Created saleOrder: {}", response);

            assert response != null;
            return response.getData() == null ? "" : response.getData().toString();


    }

    /**
     * Cancel sale order from inventory
     *
     * @param orderId
     */
    private void cancelSaleOrder(String orderId) {
        log.info("cancelSaleOrder called");

        ApiResponse response = inventoryServiceClient.cancelSaleOrder(orderId);
        log.info("cancelSaleOrder response: {}", response);
    }
}