package vn.chiendt.cart.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.chiendt.cart.common.CartState;
import vn.chiendt.cart.dto.response.CartItemResponse;
import vn.chiendt.cart.exception.CartNotFoundException;
import vn.chiendt.cart.model.Cart;
import vn.chiendt.cart.repository.CartRepository;
import vn.chiendt.cart.service.CartItemService;


import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartRepository cartRepository;

    @Override
    public void addCartItem(Long userId, Long productId, Integer quantity) {

    }

    @Override
    public void updateCartItem(Long userId, Long productId, Integer quantity) {

    }

    @Override
    public void removeItem(Long userId, Long productId) {
        cartRepository.removeCartItem(userId,productId);
    }

    @Override
    public List<CartItemResponse> listItems(Long userId) {
        log.info("Get all cart items for userId:{}", userId);

        // get cart from db
        //Cart cart = cartRepository.getCartByUserIdAndCartState(userId, CartState.ACTIVE).orElseThrow(() -> new CartNotFoundException("Active cart not found for user: " + userId));
        return List.of();
    }
}
