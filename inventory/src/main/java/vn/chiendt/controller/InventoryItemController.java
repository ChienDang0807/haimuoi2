package vn.chiendt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.dto.response.ApiResponse;
import vn.chiendt.service.InventoryItemService;

@RestController
@RequestMapping("/inventory-item")
@RequiredArgsConstructor
@Slf4j(topic = "INVENTORY-ITEM-CONTROLLER")
@Tag(name = "Inventory Item Controller")
public class InventoryItemController {
    private final InventoryItemService inventoryItemService;

    @Operation(summary = "Get inventory item", description = "API get inventory item")
    @GetMapping("/{id}")
    public ApiResponse getInventoryItemById(@PathVariable Long id){
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Get Inventory Item Success")
                .data(inventoryItemService.getInventoryByProductId(id))
                .build();
    }

    @Operation(summary = "")
    @GetMapping("/check")
    public ApiResponse isInventoryItemAvailable(@RequestParam Long productId, @RequestParam Integer quantity ){
        return ApiResponse.builder()
                .status(200)
                .message("Check successfully")
                .data(inventoryItemService.isInventoryItemAvailable(productId,quantity)).build();
    }

}
