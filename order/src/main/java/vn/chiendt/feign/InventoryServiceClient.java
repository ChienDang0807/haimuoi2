package vn.chiendt.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.chiendt.dto.request.SaleOrderCreationRequest;
import vn.chiendt.dto.response.ApiResponse;

@FeignClient(name = "inventory-service", url = "${api.internal.saleOrderUrl}")
public interface InventoryServiceClient {

    @PostMapping("/create")
    public ApiResponse createSaleOrder(@Valid @RequestBody SaleOrderCreationRequest request) ;

    @PatchMapping("/cancel/{id}")
    public ApiResponse cancelSaleOrder(@PathVariable String id) ;
}
