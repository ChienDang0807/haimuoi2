package vn.chiendt.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.cart.dto.request.AddCartItemRequest;
import vn.chiendt.cart.dto.request.CartCreationRequest;
import vn.chiendt.cart.dto.request.UpdateCartItemRequest;
import vn.chiendt.cart.dto.response.ApiResponse;
import vn.chiendt.cart.dto.response.CartResponse;
import vn.chiendt.cart.service.CartService;
import vn.chiendt.cart.utils.CartTokenUtils;

@RestController
@RequestMapping("/api/v1/cart")
@Slf4j(topic = "CART-CONTROLLER")
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "APIs for managing shopping cart")
public class CartController {

    private final CartService cartService;
    private final CartTokenUtils cartTokenUtils;

    @PostMapping
    @Operation(summary = "Create a new cart", description = "Create a new shopping cart for a user")
    public ResponseEntity<ApiResponse<CartResponse>> createCart(@Valid @RequestBody CartCreationRequest request) {
        log.info("Creating cart for user: {}", request.getUserId());
        CartResponse cartResponse = cartService.createCart(request);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Cart created successfully"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get cart by user ID", description = "Retrieve cart information for a specific user")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Getting cart for user: {}", userId);
        CartResponse cartResponse = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Cart retrieved successfully"));
    }

    @GetMapping("/token")
    @Operation(summary = "Get cart by token from cookie", description = "Retrieve cart information using cart token from cookie")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByTokenFromCookie(
            HttpServletRequest request, HttpServletResponse response) {
        String cartToken = cartTokenUtils.getOrCreateCartToken(request, response);
        log.info("Getting cart by token from cookie: {}", cartToken);
        CartResponse cartResponse = cartService.getCartByToken(cartToken);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Cart retrieved successfully"));
    }

    @GetMapping("/token/{cartToken}")
    @Operation(summary = "Get cart by token", description = "Retrieve cart information using cart token")
    public ResponseEntity<ApiResponse<CartResponse>> getCartByToken(
            @Parameter(description = "Cart token") @PathVariable String cartToken) {
        log.info("Getting cart by token: {}", cartToken);
        CartResponse cartResponse = cartService.getCartByToken(cartToken);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Cart retrieved successfully"));
    }

    @PostMapping("/user/{userId}/items")
    @Operation(summary = "Add item to cart", description = "Add a new item to user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Valid @RequestBody AddCartItemRequest request) {
        log.info("Adding item to cart for user: {}, product: {}", userId, request.getProductId());
        CartResponse cartResponse = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Item added to cart successfully"));
    }

    @PostMapping("/token/items")
    @Operation(summary = "Add item to cart by token from cookie", description = "Add a new item to cart using cart token from cookie")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCartByTokenFromCookie(
            HttpServletRequest request, HttpServletResponse response,
            @Valid @RequestBody AddCartItemRequest addRequest) {
        String cartToken = cartTokenUtils.getOrCreateCartToken(request, response);
        log.info("Adding item to cart by token from cookie: {}, product: {}", cartToken, addRequest.getProductId());
        CartResponse cartResponse = cartService.addItemToCartByToken(cartToken, addRequest);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Item added to cart successfully"));
    }

    @PostMapping("/token/{cartToken}/items")
    @Operation(summary = "Add item to cart by token", description = "Add a new item to cart using cart token")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCartByToken(
            @Parameter(description = "Cart token") @PathVariable String cartToken,
            @Valid @RequestBody AddCartItemRequest request) {
        log.info("Adding item to cart by token: {}, product: {}", cartToken, request.getProductId());
        CartResponse cartResponse = cartService.addItemToCartByToken(cartToken, request);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Item added to cart successfully"));
    }

    @PutMapping("/user/{userId}/items/{productId}")
    @Operation(summary = "Update cart item", description = "Update quantity of a specific item in user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Product ID") @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        log.info("Updating cart item for user: {}, product: {}", userId, productId);
        CartResponse cartResponse = cartService.updateCartItem(userId, productId, request);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Cart item updated successfully"));
    }

    @PutMapping("/token/items/{productId}")
    @Operation(summary = "Update cart item by token from cookie", description = "Update quantity of a specific item in cart using token from cookie")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItemByTokenFromCookie(
            HttpServletRequest request, HttpServletResponse response,
            @Parameter(description = "Product ID") @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest updateRequest) {
        String cartToken = cartTokenUtils.getOrCreateCartToken(request, response);
        log.info("Updating cart item by token from cookie: {}, product: {}", cartToken, productId);
        CartResponse cartResponse = cartService.updateCartItemByToken(cartToken, productId, updateRequest);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Cart item updated successfully"));
    }

    @PutMapping("/token/{cartToken}/items/{productId}")
    @Operation(summary = "Update cart item by token", description = "Update quantity of a specific item in cart using token")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItemByToken(
            @Parameter(description = "Cart token") @PathVariable String cartToken,
            @Parameter(description = "Product ID") @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        log.info("Updating cart item by token: {}, product: {}", cartToken, productId);
        CartResponse cartResponse = cartService.updateCartItemByToken(cartToken, productId, request);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Cart item updated successfully"));
    }

    @DeleteMapping("/user/{userId}/items/{productId}")
    @Operation(summary = "Remove item from cart", description = "Remove a specific item from user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCart(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        log.info("Removing item from cart for user: {}, product: {}", userId, productId);
        CartResponse cartResponse = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Item removed from cart successfully"));
    }

    @DeleteMapping("/token/items/{productId}")
    @Operation(summary = "Remove item from cart by token from cookie", description = "Remove a specific item from cart using token from cookie")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCartByTokenFromCookie(
            HttpServletRequest request, HttpServletResponse response,
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        String cartToken = cartTokenUtils.getOrCreateCartToken(request, response);
        log.info("Removing item from cart by token from cookie: {}, product: {}", cartToken, productId);
        CartResponse cartResponse = cartService.removeItemFromCartByToken(cartToken, productId);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Item removed from cart successfully"));
    }

    @DeleteMapping("/token/{cartToken}/items/{productId}")
    @Operation(summary = "Remove item from cart by token", description = "Remove a specific item from cart using token")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCartByToken(
            @Parameter(description = "Cart token") @PathVariable String cartToken,
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        log.info("Removing item from cart by token: {}, product: {}", cartToken, productId);
        CartResponse cartResponse = cartService.removeItemFromCartByToken(cartToken, productId);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Item removed from cart successfully"));
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Clear cart", description = "Remove all items from user's cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Clearing cart for user: {}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared successfully"));
    }

    @DeleteMapping("/token")
    @Operation(summary = "Clear cart by token from cookie", description = "Remove all items from cart using token from cookie")
    public ResponseEntity<ApiResponse<Void>> clearCartByTokenFromCookie(
            HttpServletRequest request, HttpServletResponse response) {
        String cartToken = cartTokenUtils.getOrCreateCartToken(request, response);
        log.info("Clearing cart by token from cookie: {}", cartToken);
        cartService.clearCartByToken(cartToken);
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared successfully"));
    }

    @DeleteMapping("/token/{cartToken}")
    @Operation(summary = "Clear cart by token", description = "Remove all items from cart using token")
    public ResponseEntity<ApiResponse<Void>> clearCartByToken(
            @Parameter(description = "Cart token") @PathVariable String cartToken) {
        log.info("Clearing cart by token: {}", cartToken);
        cartService.clearCartByToken(cartToken);
        return ResponseEntity.ok(ApiResponse.success(null, "Cart cleared successfully"));
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Get cart items count", description = "Get the number of items in user's cart")
    public ResponseEntity<ApiResponse<Integer>> getCartItemsCount(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Getting cart items count for user: {}", userId);
        Integer count = cartService.getCartItemsCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count, "Cart items count retrieved successfully"));
    }

    @GetMapping("/token/count")
    @Operation(summary = "Get cart items count by token from cookie", description = "Get the number of items in cart using token from cookie")
    public ResponseEntity<ApiResponse<Integer>> getCartItemsCountByTokenFromCookie(
            HttpServletRequest request, HttpServletResponse response) {
        String cartToken = cartTokenUtils.getOrCreateCartToken(request, response);
        log.info("Getting cart items count by token from cookie: {}", cartToken);
        Integer count = cartService.getCartItemsCountByToken(cartToken);
        return ResponseEntity.ok(ApiResponse.success(count, "Cart items count retrieved successfully"));
    }

    @GetMapping("/token/{cartToken}/count")
    @Operation(summary = "Get cart items count by token", description = "Get the number of items in cart using token")
    public ResponseEntity<ApiResponse<Integer>> getCartItemsCountByToken(
            @Parameter(description = "Cart token") @PathVariable String cartToken) {
        log.info("Getting cart items count by token: {}", cartToken);
        Integer count = cartService.getCartItemsCountByToken(cartToken);
        return ResponseEntity.ok(ApiResponse.success(count, "Cart items count retrieved successfully"));
    }

    @GetMapping("/user/{userId}/total")
    @Operation(summary = "Calculate cart total", description = "Calculate total amount for user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> calculateCartTotal(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Calculating cart total for user: {}", userId);
        CartResponse cartResponse = cartService.calculateCartTotal(userId);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Cart total calculated successfully"));
    }

    @GetMapping("/token/total")
    @Operation(summary = "Calculate cart total by token from cookie", description = "Calculate total amount for cart using token from cookie")
    public ResponseEntity<ApiResponse<CartResponse>> calculateCartTotalByTokenFromCookie(
            HttpServletRequest request, HttpServletResponse response) {
        String cartToken = cartTokenUtils.getOrCreateCartToken(request, response);
        log.info("Calculating cart total by token from cookie: {}", cartToken);
        CartResponse cartResponse = cartService.calculateCartTotalByToken(cartToken);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Cart total calculated successfully"));
    }

    @GetMapping("/token/{cartToken}/total")
    @Operation(summary = "Calculate cart total by token", description = "Calculate total amount for cart using token")
    public ResponseEntity<ApiResponse<CartResponse>> calculateCartTotalByToken(
            @Parameter(description = "Cart token") @PathVariable String cartToken) {
        log.info("Calculating cart total by token: {}", cartToken);
        CartResponse cartResponse = cartService.calculateCartTotalByToken(cartToken);
        return ResponseEntity.ok(ApiResponse.success(cartResponse, "Cart total calculated successfully"));
    }
}
