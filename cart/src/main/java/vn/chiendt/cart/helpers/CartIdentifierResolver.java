package vn.chiendt.cart.helpers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * Helper class for managing cart tokens via cookies
 * Handles cart token creation, retrieval, and cookie management
 */
@Component
@Slf4j(topic = "CART-IDENTIFIER-RESOLVER")
public class CartIdentifierResolver {

    private static final String CART_TOKEN_COOKIE = "cartToken";
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30 days
    private static final String COOKIE_PATH = "/";

    /**
     * Resolve existing cart token from cookie or create new one
     * @param request HTTP request
     * @param response HTTP response
     * @return cart token (existing or newly created)
     */
    public String resolveOrCreateCartToken(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Resolving cart token from request");

        // Try to get existing cart token from cookie
        String existingToken = getCartTokenFromCookie(request);

        if (existingToken != null && !existingToken.isBlank()) {
            log.debug("Found existing cart token: {}", existingToken);
            return existingToken;
        }

        // Create new cart token
        String newCartToken = generateCartToken();
        setCartTokenCookie(response, newCartToken);

        log.info("Created new cart token: {}", newCartToken);
        return newCartToken;
    }

    /**
     * Get cart token from cookie if exists
     * @param request HTTP request
     * @return cart token or null if not found
     */
    public String getCartTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            log.debug("No cookies found in request");
            return null;
        }

        Optional<String> cartToken = Arrays.stream(cookies)
                .filter(cookie -> CART_TOKEN_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();

        return cartToken.orElse(null);
    }

    /**
     * Set cart token cookie in response
     * @param response HTTP response
     * @param cartToken cart token to set
     */
    public void setCartTokenCookie(HttpServletResponse response, String cartToken) {
        Cookie cookie = new Cookie(CART_TOKEN_COOKIE, cartToken);
        cookie.setPath(COOKIE_PATH);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);

        log.debug("Set cart token cookie: {}", cartToken);
    }

    /**
     * Clear cart token cookie
     * @param response HTTP response
     */
    public void clearCartTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(CART_TOKEN_COOKIE, "");
        cookie.setPath(COOKIE_PATH);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Expire immediately
        response.addCookie(cookie);

        log.debug("Cleared cart token cookie");
    }

    /**
     * Generate a new unique cart token
     * @return new cart token
     */
    private String generateCartToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Check if cart token exists in request
     * @param request HTTP request
     * @return true if cart token exists
     */
    public boolean hasCartToken(HttpServletRequest request) {
        String cartToken = getCartTokenFromCookie(request);
        return cartToken != null && !cartToken.isBlank();
    }

    /**
     * Get cart token or create new one if not exists
     * This is a convenience method that combines resolveOrCreateCartToken
     * @param request HTTP request
     * @param response HTTP response
     * @return cart token
     */
    public String getOrCreateCartToken(HttpServletRequest request, HttpServletResponse response) {
        return resolveOrCreateCartToken(request, response);
    }
}
