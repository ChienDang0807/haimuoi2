package vn.chiendt.cart.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.chiendt.cart.helpers.CartIdentifierResolver;

/**
 * Utility class for cart token operations
 * Provides convenient methods for working with cart tokens
 */
@Component
@RequiredArgsConstructor
@Slf4j(topic = "CART-TOKEN-UTILS")
public class CartTokenUtils {

    private final CartIdentifierResolver cartIdentifierResolver;

    /**
     * Get cart token from request, create new one if not exists
     * @param request HTTP request
     * @param response HTTP response
     * @return cart token
     */
    public String getCartToken(HttpServletRequest request, HttpServletResponse response) {
        return cartIdentifierResolver.getOrCreateCartToken(request, response);
    }

    /**
     * Get cart token from request only (don't create new one)
     * @param request HTTP request
     * @return cart token or null if not found
     */
    public String getExistingCartToken(HttpServletRequest request) {
        return cartIdentifierResolver.getCartTokenFromCookie(request);
    }

    /**
     * Check if cart token exists in request
     * @param request HTTP request
     * @return true if cart token exists
     */
    public boolean hasCartToken(HttpServletRequest request) {
        return cartIdentifierResolver.hasCartToken(request);
    }

    /**
     * Set cart token in response
     * @param response HTTP response
     * @param cartToken cart token to set
     */
    public void setCartToken(HttpServletResponse response, String cartToken) {
        cartIdentifierResolver.setCartTokenCookie(response, cartToken);
    }

    /**
     * Clear cart token from response
     * @param response HTTP response
     */
    public void clearCartToken(HttpServletResponse response) {
        cartIdentifierResolver.clearCartTokenCookie(response);
    }

    /**
     * Get or create cart token for anonymous user
     * This is the most commonly used method
     * @param request HTTP request
     * @param response HTTP response
     * @return cart token
     */
    public String getOrCreateCartToken(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Getting or creating cart token for request");
        return cartIdentifierResolver.getOrCreateCartToken(request, response);
    }

    /**
     * Validate cart token format
     * @param cartToken cart token to validate
     * @return true if valid format
     */
    public boolean isValidCartToken(String cartToken) {
        if (cartToken == null || cartToken.isBlank()) {
            return false;
        }
        
        // Basic UUID format validation
        return cartToken.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    }

    /**
     * Extract cart token from request and validate
     * @param request HTTP request
     * @return valid cart token or null
     */
    public String extractValidCartToken(HttpServletRequest request) {
        String cartToken = getExistingCartToken(request);
        if (cartToken != null && isValidCartToken(cartToken)) {
            return cartToken;
        }
        return null;
    }
}
