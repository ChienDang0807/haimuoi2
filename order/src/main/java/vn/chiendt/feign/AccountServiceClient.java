package vn.chiendt.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "account-service", url = "${api.internal.accountUrl}")
public interface AccountServiceClient {

}
