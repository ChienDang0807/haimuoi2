package vn.chiendt.cart.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.chiendt.cart.helpers.CartIdentifierResolver;

import java.io.IOException;

/**
 * Middleware to automatically handle cart token for anonymous users
 * This filter ensures that every request has a cart token available
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j(topic = "CART-TOKEN-MIDDLEWARE")
public class CartTokenMiddleware extends OncePerRequestFilter {

    private final CartIdentifierResolver cartIdentifierResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Only apply to cart endpoints that don't require user authentication
        if (shouldApplyCartToken(requestPath)) {
            log.debug("Applying cart token middleware for path: {}", requestPath);
            
            // Ensure cart token exists in request
            if (!cartIdentifierResolver.hasCartToken(request)) {
                String cartToken = cartIdentifierResolver.getOrCreateCartToken(request, response);
                log.debug("Created cart token for anonymous user: {}", cartToken);
            } else {
                log.debug("Found existing cart token for request");
            }
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Determine if cart token middleware should be applied to this request
     * @param requestPath the request path
     * @return true if middleware should be applied
     */
    private boolean shouldApplyCartToken(String requestPath) {
        // Apply to cart endpoints that don't require user authentication
        return requestPath.startsWith("/api/v1/cart/token") && 
               !requestPath.contains("/user/") &&
               !requestPath.contains("/admin/");
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip middleware for user-specific endpoints and admin endpoints
        return path.contains("/user/") || 
               path.contains("/admin/") || 
               path.contains("/swagger") ||
               path.contains("/health");
    }
}
