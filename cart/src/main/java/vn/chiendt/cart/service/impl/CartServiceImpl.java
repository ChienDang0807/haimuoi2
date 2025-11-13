package vn.chiendt.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chiendt.cart.client.InventoryServiceClient;
import vn.chiendt.cart.client.ProductServiceClient;
import vn.chiendt.cart.common.CartState;
import vn.chiendt.cart.dto.request.AddCartItemRequest;
import vn.chiendt.cart.dto.request.CartCreationRequest;
import vn.chiendt.cart.dto.request.UpdateCartItemRequest;
import vn.chiendt.cart.dto.response.CartItemResponse;
import vn.chiendt.cart.dto.response.CartResponse;
import vn.chiendt.cart.exception.CartNotFoundException;
import vn.chiendt.cart.exception.OutOfStockException;
import vn.chiendt.cart.mapper.CartItemMapper;
import vn.chiendt.cart.mapper.CartMapper;
import vn.chiendt.cart.model.Cart;
import vn.chiendt.cart.model.CartItem;
import vn.chiendt.cart.repository.CartItemRepository;
import vn.chiendt.cart.repository.CartRepository;
import vn.chiendt.cart.service.CartService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CART-SERVICE")
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    @Override
    @Transactional
    public CartResponse createCart(CartCreationRequest request) {
        log.info("Creating cart for user: {}", request.getUserId());
        
        // Check if user already has a cart
        Optional<Cart> existingCart = cartRepository.findByUserId(request.getUserId());
        if (existingCart.isPresent()) {
            log.info("Cart already exists for user: {}", request.getUserId());
            return getCartByUserId(request.getUserId());
        }
        
        Cart cart = Cart.builder()
                .id(UUID.randomUUID().toString())
                .userId(request.getUserId())
                .cartState(request.getCartState() != null ? request.getCartState() : CartState.ACTIVE)
                .shippingFee(BigDecimal.valueOf(30000))
                .discount(BigDecimal.ZERO)
                .build();
        
        cart = cartRepository.save(cart);
        log.info("Cart created successfully with ID: {}", cart.getId());
        
        return cartMapper.toResponse(cart);
    }

    @Override
    public CartResponse getCartByUserId(Long userId) {
        log.info("Getting cart for user: {}", userId);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        
        List<CartItem> cartItems = cartItemRepository.findAllByCartIdentifier(cart.getId());
        cart.setCartItems(cartItems);
        
        return cartMapper.toResponse(cart);
    }

    @Override
    public CartResponse getCartByToken(String cartToken) {
        log.info("Getting cart by token: {}", cartToken);
        
        Cart cart = cartRepository.findById(cartToken)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for token: " + cartToken));
        
        List<CartItem> cartItems = cartItemRepository.findAllByCartIdentifier(cart.getId());
        cart.setCartItems(cartItems);
        
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(Long userId, AddCartItemRequest request) {
        log.info("Adding item to cart for user: {}, product: {}", userId, request.getProductId());
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        
        return addItemToCartInternal(cart.getId(), request);
    }

    @Override
    @Transactional
    public CartResponse addItemToCartByToken(String cartToken, AddCartItemRequest request) {
        log.info("Adding item to cart by token: {}, product: {}", cartToken, request.getProductId());
        
        Cart cart = cartRepository.findById(cartToken)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for token: " + cartToken));
        
        return addItemToCartInternal(cart.getId(), request);
    }

    private CartResponse addItemToCartInternal(String cartId, AddCartItemRequest request) {
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cartId, request.getProductId());
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
            log.info("Updated quantity for existing item: {}", item.getProductId());
        } else {
            // Create new cart item
            CartItem cartItem = CartItem.builder()
                    .productId(request.getProductId())
                    .cartIdentifier(cartId)
                    .productName(request.getProductName())
                    .price(request.getPrice())
                    .quantity(request.getQuantity())
                    .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                    .shopId(request.getShopId())
                    .attributes(request.getAttributes() != null ? request.getAttributes() : new java.util.HashMap<>())
                    .build();
            
            cartItemRepository.save(cartItem);
            log.info("Added new item to cart: {}", cartItem.getProductId());
        }
        
        return getCartByUserId(cartRepository.findById(cartId).get().getUserId());
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request) {
        log.info("Updating cart item for user: {}, product: {}", userId, productId);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        
        return updateCartItemInternal(cart.getId(), productId, request);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemByToken(String cartToken, Long productId, UpdateCartItemRequest request) {
        log.info("Updating cart item by token: {}, product: {}", cartToken, productId);
        
        Cart cart = cartRepository.findById(cartToken)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for token: " + cartToken));
        
        return updateCartItemInternal(cart.getId(), productId, request);
    }

    private CartResponse updateCartItemInternal(String cartId, Long productId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new CartNotFoundException("Cart item not found for product: " + productId));
        
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        
        log.info("Updated cart item quantity: {}", productId);
        return getCartByUserId(cartRepository.findById(cartId).get().getUserId());
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long productId) {
        log.info("Removing item from cart for user: {}, product: {}", userId, productId);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        
        return removeItemFromCartInternal(cart.getId(), productId);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCartByToken(String cartToken, Long productId) {
        log.info("Removing item from cart by token: {}, product: {}", cartToken, productId);
        
        Cart cart = cartRepository.findById(cartToken)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for token: " + cartToken));
        
        return removeItemFromCartInternal(cart.getId(), productId);
    }

    private CartResponse removeItemFromCartInternal(String cartId, Long productId) {
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new CartNotFoundException("Cart item not found for product: " + productId));
        
        cartItemRepository.delete(cartItem);
        log.info("Removed item from cart: {}", productId);
        
        return getCartByUserId(cartRepository.findById(cartId).get().getUserId());
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("Clearing cart for user: {}", userId);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        
        cartItemRepository.deleteCartItemByUserId(userId);
        log.info("Cleared cart for user: {}", userId);
    }

    @Override
    @Transactional
    public void clearCartByToken(String cartToken) {
        log.info("Clearing cart by token: {}", cartToken);
        
        Cart cart = cartRepository.findById(cartToken)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for token: " + cartToken));
        
        cartItemRepository.deleteCartItemByCartToken(cartToken);
        log.info("Cleared cart by token: {}", cartToken);
    }

    @Override
    public Integer getCartItemsCount(Long userId) {
        log.info("Getting cart items count for user: {}", userId);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
        
        return (int) cartItemRepository.countCartItemsByCartId(cart.getId());
    }

    @Override
    public Integer getCartItemsCountByToken(String cartToken) {
        log.info("Getting cart items count by token: {}", cartToken);
        
        Cart cart = cartRepository.findById(cartToken)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for token: " + cartToken));
        
        return (int) cartItemRepository.countCartItemsByCartId(cart.getId());
    }

    @Override
    public CartResponse calculateCartTotal(Long userId) {
        log.info("Calculating cart total for user: {}", userId);
        return getCartByUserId(userId);
    }

    @Override
    public CartResponse calculateCartTotalByToken(String cartToken) {
        log.info("Calculating cart total by token: {}", cartToken);
        return getCartByToken(cartToken);
    }
}
