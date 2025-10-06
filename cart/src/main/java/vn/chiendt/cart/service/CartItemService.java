package vn.chiendt.cart.service;



import vn.chiendt.cart.dto.response.CartItemResponse;

import java.util.List;

public interface CartItemService {

    void addCartItem (Long userId, Long productId, Integer quantity);

    void updateCartItem (Long userId, Long productId, Integer quantity);

    void removeItem(Long userId, Long productId);

    List<CartItemResponse> listItems(Long userId);
}
