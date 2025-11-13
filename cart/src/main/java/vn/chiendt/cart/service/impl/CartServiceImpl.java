package vn.chiendt.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chiendt.cart.common.CartState;
import vn.chiendt.cart.dto.request.AddCartItemRequest;
import vn.chiendt.cart.dto.request.CartCreationRequest;
import vn.chiendt.cart.dto.request.UpdateCartItemRequest;
import vn.chiendt.cart.dto.response.CartResponse;
import vn.chiendt.cart.exception.CartNotFoundException;
import vn.chiendt.cart.mapper.CartMapper;
import vn.chiendt.cart.model.Cart;
import vn.chiendt.cart.model.CartItem;
import vn.chiendt.cart.repository.CartItemRepository;
import vn.chiendt.cart.repository.CartRepository;
import vn.chiendt.cart.service.CartService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
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
        Cart cart = loadCartByUserId(userId);
        return buildCartResponse(cart);
    }

    @Override
    public CartResponse getCartByToken(String cartToken) {
        log.info("Getting cart by token: {}", cartToken);
        Cart cart = loadCartByToken(cartToken);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(Long userId, AddCartItemRequest request) {
        log.info("Adding item to cart for user: {}, product: {}", userId, request.getProductId());
        Cart cart = loadCartByUserId(userId);
        return addItemToCartInternal(cart, request);
    }

    @Override
    @Transactional
    public CartResponse addItemToCartByToken(String cartToken, AddCartItemRequest request) {
        log.info("Adding item to cart by token: {}, product: {}", cartToken, request.getProductId());
        Cart cart = loadCartByToken(cartToken);
        return addItemToCartInternal(cart, request);
    }

    private CartResponse addItemToCartInternal(Cart cart, AddCartItemRequest request) {
        validateQuantity(request.getQuantity());

        cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .ifPresentOrElse(item -> {
                    item.setQuantity(item.getQuantity() + request.getQuantity());
                    cartItemRepository.save(item);
                    log.info("Updated quantity for existing item: {}", item.getProductId());
                }, () -> {
                    CartItem cartItem = CartItem.builder()
                            .productId(request.getProductId())
                            .cartIdentifier(cart.getId())
                            .productName(request.getProductName())
                            .price(request.getPrice())
                            .quantity(request.getQuantity())
                            .discount(Optional.ofNullable(request.getDiscount()).orElse(BigDecimal.ZERO))
                            .shopId(request.getShopId())
                            .attributes(new HashMap<>(Optional.ofNullable(request.getAttributes()).orElseGet(Collections::emptyMap)))
                            .build();

                    cartItemRepository.save(cartItem);
                    log.info("Added new item to cart: {}", cartItem.getProductId());
                });

        return buildCartResponse(loadCartById(cart.getId()));
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request) {
        log.info("Updating cart item for user: {}, product: {}", userId, productId);
        Cart cart = loadCartByUserId(userId);
        return updateCartItemInternal(cart, productId, request);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemByToken(String cartToken, Long productId, UpdateCartItemRequest request) {
        log.info("Updating cart item by token: {}, product: {}", cartToken, productId);
        Cart cart = loadCartByToken(cartToken);
        return updateCartItemInternal(cart, productId, request);
    }

    private CartResponse updateCartItemInternal(Cart cart, Long productId, UpdateCartItemRequest request) {
        validateQuantity(request.getQuantity());

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new CartNotFoundException("Cart item not found for product: " + productId));
        
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        
        log.info("Updated cart item quantity: {}", productId);
        return buildCartResponse(loadCartById(cart.getId()));
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long productId) {
        log.info("Removing item from cart for user: {}, product: {}", userId, productId);
        Cart cart = loadCartByUserId(userId);
        return removeItemFromCartInternal(cart, productId);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCartByToken(String cartToken, Long productId) {
        log.info("Removing item from cart by token: {}, product: {}", cartToken, productId);
        Cart cart = loadCartByToken(cartToken);
        return removeItemFromCartInternal(cart, productId);
    }

    private CartResponse removeItemFromCartInternal(Cart cart, Long productId) {
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new CartNotFoundException("Cart item not found for product: " + productId));
        
        cartItemRepository.delete(cartItem);
        log.info("Removed item from cart: {}", productId);
        
        return buildCartResponse(loadCartById(cart.getId()));
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("Clearing cart for user: {}", userId);
        loadCartByUserId(userId);
        cartItemRepository.deleteCartItemByUserId(userId);
        log.info("Cleared cart for user: {}", userId);
    }

    @Override
    @Transactional
    public void clearCartByToken(String cartToken) {
        log.info("Clearing cart by token: {}", cartToken);
        loadCartByToken(cartToken);
        cartItemRepository.deleteCartItemByCartToken(cartToken);
        log.info("Cleared cart by token: {}", cartToken);
    }

    @Override
    public Integer getCartItemsCount(Long userId) {
        log.info("Getting cart items count for user: {}", userId);
        Cart cart = loadCartByUserId(userId);
        return Math.toIntExact(cartItemRepository.countCartItemsByCartId(cart.getId()));
    }

    @Override
    public Integer getCartItemsCountByToken(String cartToken) {
        log.info("Getting cart items count by token: {}", cartToken);
        Cart cart = loadCartByToken(cartToken);
        return Math.toIntExact(cartItemRepository.countCartItemsByCartId(cart.getId()));
    }

    @Override
    public CartResponse calculateCartTotal(Long userId) {
        log.info("Calculating cart total for user: {}", userId);
        return buildCartResponse(loadCartByUserId(userId));
    }

    @Override
    public CartResponse calculateCartTotalByToken(String cartToken) {
        log.info("Calculating cart total by token: {}", cartToken);
        return buildCartResponse(loadCartByToken(cartToken));
    }

    private Cart loadCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
    }

    private Cart loadCartByToken(String cartToken) {
        return cartRepository.findById(cartToken)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for token: " + cartToken));
    }

    private Cart loadCartById(String cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for id: " + cartId));
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findAllByCartIdentifier(cart.getId());
        cart.setCartItems(cartItems);
        return cartMapper.toResponse(cart);
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
}
