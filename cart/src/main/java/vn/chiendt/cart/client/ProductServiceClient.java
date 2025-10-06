package vn.chiendt.cart.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "product-service", url = "${service.productUrl}")
public class ProductServiceClient {
}
