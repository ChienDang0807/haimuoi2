package vn.chiendt.service;

import vn.chiendt.common.TokenType;

import java.util.List;

public interface JwtService {
    String generateAccessToken(String username, Long userId, List<String> authorities);

    String generateRefreshToken(String username, Long userId, List<String> authorities);

    String extractUsername(String token, TokenType type);

    Long extractUserId(String token, TokenType type);
}
