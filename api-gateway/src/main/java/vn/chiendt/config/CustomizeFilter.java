package vn.chiendt.config;


import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import reactor.core.publisher.Mono;
import vn.chiendt.client.AuthenticationServiceClient;

import vn.chiendt.grpc.VerifyTokenGrpcResponse;
import vn.chiendt.model.PermissionHash;
import vn.chiendt.repository.PermissionRepository;
import vn.chiendt.request.CheckPermissionRequest;
import vn.chiendt.response.CheckPermissionResponse;
import vn.chiendt.response.ErrorResponse;
import vn.chiendt.response.PermissionResponse;
import vn.chiendt.response.VerifyTokenResponse;
import vn.chiendt.service.VerifyTokenService;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@Slf4j
public class CustomizeFilter extends AbstractGatewayFilterFactory<CustomizeFilter.Config> {

    private final PermissionRepository permissionRepository;
    private final VerifyTokenService verifyTokenService;

    @Value("${service.authUrl}")
    private String authUrl;
    @Value("${service.authorUrl}")
    private String authorUrl;

    private AuthenticationServiceClient authenticationServiceClient;

    public CustomizeFilter( PermissionRepository permissionRepository, VerifyTokenService verifyTokenService) {
        super(Config.class);
        this.permissionRepository = permissionRepository;
        this.verifyTokenService = verifyTokenService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String url = request.getPath().toString();
            log.info("-------------[ {} ]", url);

            if (isWhiteListURL(url) || url.contains("/v3/api-docs/")) {
                return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                }));
            }

            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders requestHeaders = request.getHeaders();

            if (requestHeaders.containsKey(AUTHORIZATION) && Objects.requireNonNull(requestHeaders.getFirst(AUTHORIZATION)).startsWith("Bearer ")) {
                // Get access token from header
                final String token = request.getHeaders().getOrEmpty(AUTHORIZATION).get(0).substring(7);

                // verify access token
                VerifyTokenGrpcResponse grpcResponse = verifyTokenService.verifyAccessToken(token);
                if (!grpcResponse.getIsValid()){
                    return printErrorMessage(exchange.getResponse(), FORBIDDEN, url, grpcResponse.getMessage());
                }

                boolean isGranted = checkRoleInCache(grpcResponse.getUsername(), request.getMethod().name() + " " + url);
                if (!isGranted) {
                    return printErrorMessage(exchange.getResponse(), FORBIDDEN, url, "Access denied");
                }

                log.info("Request valid");
                return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                }));
            } else {
                log.info("Request not valid, URL={}", url);
                return printErrorMessage(exchange.getResponse(), UNAUTHORIZED, url, "Request invalid, Please try again!");
            }
        };
    }

    /**
     * Check permission in Redis
     *
     * @param username
     * @return
     */
    private boolean checkRoleInCache(String username, String requestPath) {
        Optional<PermissionHash> data = permissionRepository.findById(username);
        if (data.isPresent()) {
            PermissionHash permissionHash = data.get();
            return permissionHash
                    .getPermissions().stream().anyMatch(p -> Objects.equals(p, requestPath));
        }
        return false;
    }

    /**
     * Verify Access Token
     *
     * @param token
     * @return
     */
    VerifyTokenResponse verifyAccessToken(String token) {
        log.info("Verify access token");

        try {
            return authenticationServiceClient.verifyAccessToken(token);
        } catch (RestClientException e) {
            return VerifyTokenResponse.builder()
                    .status(UNAUTHORIZED.value())
                    .message(e.getMessage())
                    .isValid(false)
                    .build();
        }
    }

    /**
     * Check role by username
     *
     * @param username
     * @return
     */
    List<Long> getRoleByUsername(String username) {
        log.info("checkRoleByUsername called");

        try {
            List<Long> roles = authenticationServiceClient.getRoleIdsByUsername(username);
            if (roles == null || roles.isEmpty()) {
                return List.of();
            }
            return roles;
        } catch (RestClientException e) {
            return List.of();
        }
    }

    /**
     * Check permission by username
     *
     * @param username
     * @return
     */
    List<PermissionResponse> getPermissionByUsername(String username) {
        log.info("checkPermissionByUsername called");

        try {
            List<PermissionResponse> permissions = authenticationServiceClient.getPermissionsByUsername(username);
            if (permissions == null || permissions.isEmpty()) {
                return List.of();
            }

            return permissions;
        } catch (RestClientException e) {
            log.error(e.getMessage());
            return List.of();
        }
    }

    /**
     * Check permission by request Method + URL
     *
     * @param username
     * @param method
     * @param path
     * @return
     */
    private CheckPermissionResponse checkPermissionByUsernameAndRequestPath(String username, String method, String path) {
        log.info("checkPermissionByUsernameAndRequestPath");

        CheckPermissionRequest request = new CheckPermissionRequest();
        request.setUsername(username);
        request.setPath(method + " " + path);

        try {
            return authenticationServiceClient.countPermissionByRequestPathAndUser(request);
        } catch (RestClientException e) {
            log.error("Can not connect to author-service: ", e);
            return CheckPermissionResponse.builder()
                    .status(UNAUTHORIZED.value())
                    .message(e.getMessage())
                    .path(request.getPath())
                    .build();
        }
    }

    /**
     * White list for access app without token
     *
     * @return
     */
    private boolean isWhiteListURL(String url) {
        List<String> permitUrls = new LinkedList<>();
        permitUrls.add("/auth/access-token");
        permitUrls.add("/auth/refresh-token");
        permitUrls.add("/auth/verify-token");

        return permitUrls.contains(url);
    }

    /**
     * @param response
     * @param url
     * @param message
     * @return
     */
    private Mono<Void> printErrorMessage(ServerHttpResponse response, HttpStatus status, String url, String message) {
        log.info("Request valid, URL={}", url);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(url);
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage(message);

        byte[] bytes = new Gson().toJson(errorResponse).getBytes(StandardCharsets.UTF_8);

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);

        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {

    }
}