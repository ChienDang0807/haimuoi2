package vn.chiendt.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.dto.request.CheckPermissionRequest;
import vn.chiendt.dto.response.CheckPermissionResponse;
import vn.chiendt.dto.response.PermissionResponse;
import vn.chiendt.service.AuthorizationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "AUTHORIZATION-CONTROLLER")
public class AuthorizationController {
    private final AuthorizationService authorizationService;

    @GetMapping("/roles")
    public List<Long> getRoleIdsByUsername(@RequestParam String username) {
        log.info("getRoleIdsByUsername called");
        return authorizationService.getRoleIdsByUsername(username);
    }

    @GetMapping("/{roleId}/permissions")
    public List<PermissionResponse> getPermissionsByRoleId(@PathVariable Integer roleId) {
        log.info("getPermissionsByRoleId called");
        return authorizationService.getPermissionsByRoleId(roleId);
    }

    @PostMapping("/check-permissions")
    public CheckPermissionResponse countPermissionByRequestPathAndUser(@Valid @RequestBody CheckPermissionRequest request) {
        log.info("countPermissionByRequestPathAndUser called");
        return authorizationService.countPermissionByRequestPathAndUser(request);
    }
}
