package vn.chiendt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.chiendt.dto.request.SignInRequest;
import vn.chiendt.dto.response.TokenResponse;
import vn.chiendt.dto.response.VerifyTokenResponse;
import vn.chiendt.exception.ValidationException;
import vn.chiendt.model.User;
import vn.chiendt.repository.UserRepository;
import vn.chiendt.service.AuthenticationService;
import vn.chiendt.service.JwtService;

import java.util.ArrayList;
import java.util.List;

import static vn.chiendt.common.TokenType.ACCESS_TOKEN;
import static vn.chiendt.common.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Get access token");

        List<String> authorities = new ArrayList<>();
        try {
            // Thực hiện xác thực với username và password
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            log.info("isAuthenticated = {}", authenticate.isAuthenticated());
            log.info("Authorities: {}", authenticate.getAuthorities().toString());
            authorities.add(authenticate.getAuthorities().toString());

            // Nếu xác thực thành công, lưu thông tin vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (BadCredentialsException | DisabledException e) {
            log.error("errorMessage: {}", e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }

        User user = userRepository.findByUsername(request.getUsername());
        Long userId = user != null ? user.getId() : null;

        String accessToken = jwtService.generateAccessToken(request.getUsername(), userId, authorities);
        String refreshToken = jwtService.generateRefreshToken(request.getUsername(), userId, authorities);

        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        log.info("Get refresh token");

        if (!StringUtils.hasLength(refreshToken)) {
            throw new ValidationException("Token must be not blank");
        }

        try {
            // Verify token
            String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);

            // check user is active or inactivated
            User user = userRepository.findByUsername(userName);

            List<String> authorities = new ArrayList<>();
            user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

            // generate new access token
            String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getId(), authorities);

            return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        } catch (Exception e) {
            log.error("Access denied! errorMessage: {}", e.getMessage());
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    public VerifyTokenResponse getVerifyToken(String request) {
        log.info("Get verify token");

        try {
            String userName = jwtService.extractUsername(request, ACCESS_TOKEN);
            Long userId = jwtService.extractUserId(request, ACCESS_TOKEN);
            log.info("extractUsername = {}", userName);
            return VerifyTokenResponse.builder()
                    .status(200)
                    .isValid(true)
                    .message("Token authenticated")
                    .username(userName)
                    .userId(userId)
                    .build();
        } catch (Exception e) {
            log.error("Invalid access token: {}", e.getMessage());
            return VerifyTokenResponse.builder()
                    .status(401)
                    .isValid(false)
                    .message("Token not authenticated")
                    .build();
        }
    }

}
