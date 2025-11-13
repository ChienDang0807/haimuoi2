package vn.chiendt.cart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import vn.chiendt.cart.dto.response.ApiResponse;

@FeignClient(name = "inventory-service", url = "${service.inventoryUrl}")
public interface InventoryServiceClient {

    @GetMapping("/inventory-item/{id}")
     ApiResponse<?> getInventoryItemById(@PathVariable Long id);


    @GetMapping("/inventory-item/check")
     ApiResponse<?> isInventoryItemAvailable(@RequestParam Long productId, @RequestParam Integer quantity );
}
