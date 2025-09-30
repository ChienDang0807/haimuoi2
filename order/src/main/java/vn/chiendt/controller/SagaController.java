package vn.chiendt.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.chiendt.dto.request.PlaceOrderRequest;
import vn.chiendt.dto.response.ApiResponse;
import vn.chiendt.service.SagaOrderService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Saga Controller")
@Slf4j(topic = "SAGA-CONTROLLER")
public class SagaController {

    private final SagaOrderService sagaOrderService;

    @PostMapping("/create-saga-order")
    public ApiResponse createSagaOrder(@RequestBody PlaceOrderRequest orderRequest) {
        log.info("createSagaOrder request: {}", orderRequest);

        return ApiResponse.builder()
                .status(200)
                .message("Order canceled successfully")
                .data(sagaOrderService.createSagaOrder(orderRequest))
                .build();
    }
}