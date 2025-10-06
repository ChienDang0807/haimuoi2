package vn.chiendt.cart.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "inventory-service", url = "${service.inventoryUrl}")
public class InventoryServiceClient {
}
