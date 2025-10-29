package vn.chiendt.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "account-service", url = "${api.internal.accountUrl}")
public interface AccountServiceClient {

    @GetMapping("/list/{userId}")
    ResponseEntity<List<Address>> getListAddress(@PathVariable Long userId);

    @GetMapping("/{addressId}")
    ResponseEntity<Address> getAddressDetail(@PathVariable Long addressId);
}
