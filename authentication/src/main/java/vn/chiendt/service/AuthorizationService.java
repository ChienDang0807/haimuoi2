package vn.chiendt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.chiendt.dto.request.CheckPermissionRequest;
import vn.chiendt.dto.response.CheckPermissionResponse;
import vn.chiendt.dto.response.PermissionResponse;
import vn.chiendt.repository.RoleAndPermissionRepository;

import java.util.List;

@Service
@Slf4j(topic = "AUTHORIZATION-SERVICE")
public class AuthorizationService {
    private final RoleAndPermissionRepository roleAndPermissionRepository;

    public AuthorizationService(RoleAndPermissionRepository roleAndPermissionRepository) {
        this.roleAndPermissionRepository = roleAndPermissionRepository;
    }

    public List<Long> getRoleIdsByUsername(String username) {
        log.info("getRoleIdsByUsername called");
        return roleAndPermissionRepository.findRolesByUsername(username);
    }

    public List<PermissionResponse> getPermissionsByRoleId(Integer roleId) {
        log.info("getPermissionsByRoleId called");
        return roleAndPermissionRepository.findPermissionsByRoleId(roleId);
    }

    public List<PermissionResponse> getPermissionsByUsername(String username) {
        log.info("getPermissionsByUsername called");
        return roleAndPermissionRepository.findPermissionsByUsername(username);
    }

    public CheckPermissionResponse countPermissionByRequestPathAndUser(CheckPermissionRequest request) {
        log.info("countPermissionByRequestPathAndUser called");

        Long countPermission = roleAndPermissionRepository.countPermissionByRequestPathAndUser(request.getPath(), request.getUsername());
        if (countPermission == 0) {
            return CheckPermissionResponse.builder()
                    .status(401)
                    .path(request.getPath())
                    .message("Access Denied")
                    .build();
        }

        return CheckPermissionResponse.builder()
                .status(200)
                .path(request.getPath())
                .message("Granted")
                .build();
    }
}
