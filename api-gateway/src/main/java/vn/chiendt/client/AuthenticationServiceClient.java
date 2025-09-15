package vn.chiendt.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.request.CheckPermissionRequest;
import vn.chiendt.response.CheckPermissionResponse;
import vn.chiendt.response.PermissionResponse;
import vn.chiendt.response.VerifyTokenResponse;

import java.util.List;

@FeignClient(name = "authentication-service", url = "${service.authUrl}")
public interface AuthenticationServiceClient {

    @GetMapping("/author/roles")
    List<Long> getRoleIdsByUsername(@RequestParam String username);

    @GetMapping("/author/{username}/permissions")
    public List<PermissionResponse> getPermissionsByUsername(@PathVariable String username);

    @PostMapping("/verify-token")
    VerifyTokenResponse verifyAccessToken(@RequestBody String token);


    @PostMapping("/author/check-permissions")
    CheckPermissionResponse countPermissionByRequestPathAndUser(@Valid @RequestBody CheckPermissionRequest request);
}
