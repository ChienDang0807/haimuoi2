package vn.chiendt.cart.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.chiendt.cart.dto.response.ApiResponse;

@FeignClient(name = "product-service", url = "${service.productUrl}")
public interface ProductServiceClient {

    @GetMapping("/product/{productId}")
    ApiResponse<?> getProductDetail(@PathVariable(required = false) Long productId);
}
