package vn.chiendt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.dto.request.SaleOrderCreationRequest;
import vn.chiendt.dto.response.ApiResponse;
import vn.chiendt.service.SaleOrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sale-order")
@Tag(name = "Sale Order Controller")
@Slf4j(topic = "SALE-ORDER-CONTROLLER")
public class SaleOrderController {

    private final SaleOrderService saleOrderService;

    @Operation(summary = "Create sale order", description = "API create sale order by saga")
    @PostMapping("/create")
    public ApiResponse createSaleOrder(@Valid @RequestBody SaleOrderCreationRequest request) {
        log.info("createSaleOrder called");

        String saleOrderId = saleOrderService.createSaleOrder(request);

        return ApiResponse.builder()
                .status(200)
                .message("Create sale order successfully")
                .data(saleOrderId)
                .build();
    }

    @Operation(summary = "Cancel sale order by id", description = "API cancel sale order by saga")
    @PatchMapping("/cancel/{id}")
    public ApiResponse cancelSaleOrder(@PathVariable String id) {
        log.info("Cancel sale order by id: {}", id);

        saleOrderService.cancelSaleOrder(id);

        return ApiResponse.builder()
                .status(200)
                .message("Sale order canceled successfully")
                .build();
    }
}