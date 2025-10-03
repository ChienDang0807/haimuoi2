package vn.chiendt.cart.service;

import vn.chiendt.cartservice.dto.response.CartItemResponse;

import java.util.List;

public interface CartItemService {

    void addCartItem (Long userId, Long productId, Integer quantity);

    void updateCartItem (Long userId, Long productId, Integer quantity);

    void removeItem(Long userId, Long productId);

    void removeCart(Long userId);

    List<CartItemResponse> listItems(Long userId);
}
