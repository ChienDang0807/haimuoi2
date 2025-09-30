package vn.chiendt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.dto.request.InventoryTransactionCreationRequest;
import vn.chiendt.dto.request.InventoryTransactionUpdateRequest;
import vn.chiendt.dto.response.ApiResponse;
import vn.chiendt.service.InventoryTransactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
@Slf4j(topic = "INVENTORY-TRANSACTION-CONTROLLER")
@Tag(name = "Inventory Transaction Controller")
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    @Operation(summary = "Get all inventory transactions", description = "API get all inventory transactions")
    @GetMapping("/list")
    public ApiResponse getInventoryTransactionList(@RequestParam(required = false) String sort,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        log.info("Get all inventory transactions");

        return ApiResponse.builder()
                .status(200)
                .message("inventoryTransactions")
                .data(inventoryTransactionService.getAllInventoryTransaction(sort, page, size))
                .build();
    }

    @Operation(summary = "Get inventory transaction detail", description = "API Get inventory transaction detail")
    @GetMapping("/{id}")
    public ApiResponse getInventoryTransactionDetail(@PathVariable Long id) {
        log.info("Get inventory detail by id: {}", id);

        return ApiResponse.builder()
                .status(200)
                .message("inventory")
                .data(inventoryTransactionService.getInventoryTransactionDetail(id))
                .build();
    }

    @Operation(summary = "Get inventory transaction detail", description = "API Get inventory transaction detail")
    @PostMapping("/add")
    public ApiResponse createInventoryTransaction(@Valid @RequestBody InventoryTransactionCreationRequest request) {
        log.info("Add inventory: {}", request);

        return ApiResponse.builder()
                .status(200)
                .message("Create inventory transaction successfully")
                .data(inventoryTransactionService.addInventoryTransaction(request))
                .build();
    }

    @Operation(summary = "Update inventory transaction", description = "API update inventory transaction")
    @PutMapping("/upd")
    public ApiResponse updateInventoryTransaction(@Valid @RequestBody InventoryTransactionUpdateRequest request) {
        log.info("Update inventory by id: {}", request.getId());

        inventoryTransactionService.updateInventoryTransaction(request);

        return ApiResponse.builder()
                .status(200)
                .message("Update inventory transaction successfully")
                .build();
    }

    @Operation(summary = "Delete inventory transaction by id", description = "API delete inventory transaction")
    @DeleteMapping("/del/{id}")
    public ApiResponse deleteInventoryTransaction(@PathVariable Long id) {
        log.info("Delete inventory by id: {}", id);

        inventoryTransactionService.deleteInventoryTransaction(id);

        return ApiResponse.builder()
                .status(200)
                .message("Delete inventory transaction successfully")
                .build();
    }
}
