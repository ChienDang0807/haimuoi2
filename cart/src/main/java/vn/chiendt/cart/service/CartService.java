package vn.chiendt.cart.service;

import vn.chiendt.cart.dto.request.AddCartItemRequest;
import vn.chiendt.cart.dto.request.CartCreationRequest;
import vn.chiendt.cart.dto.request.UpdateCartItemRequest;
import vn.chiendt.cart.dto.response.CartResponse;

public interface CartService {

    /**
     * Create a new cart for a user
     */
    CartResponse createCart(CartCreationRequest request);

    /**
     * Get cart by user ID
     */
    CartResponse getCartByUserId(Long userId);

    /**
     * Get cart by cart token (for anonymous users)
     */
    CartResponse getCartByToken(String cartToken);

    /**
     * Add item to cart
     */
    CartResponse addItemToCart(Long userId, AddCartItemRequest request);

    /**
     * Add item to cart by token (for anonymous users)
     */
    CartResponse addItemToCartByToken(String cartToken, AddCartItemRequest request);

    /**
     * Update cart item quantity
     */
    CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request);

    /**
     * Update cart item quantity by token
     */
    CartResponse updateCartItemByToken(String cartToken, Long productId, UpdateCartItemRequest request);

    /**
     * Remove item from cart
     */
    CartResponse removeItemFromCart(Long userId, Long productId);

    /**
     * Remove item from cart by token
     */
    CartResponse removeItemFromCartByToken(String cartToken, Long productId);

    /**
     * Clear entire cart
     */
    void clearCart(Long userId);

    /**
     * Clear entire cart by token
     */
    void clearCartByToken(String cartToken);

    /**
     * Get cart items count
     */
    Integer getCartItemsCount(Long userId);

    /**
     * Get cart items count by token
     */
    Integer getCartItemsCountByToken(String cartToken);

    /**
     * Calculate cart total
     */
    CartResponse calculateCartTotal(Long userId);

    /**
     * Calculate cart total by token
     */
    CartResponse calculateCartTotalByToken(String cartToken);
}
