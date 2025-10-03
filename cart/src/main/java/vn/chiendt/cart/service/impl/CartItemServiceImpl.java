package vn.chiendt.cart.service.impl;

import org.springframework.stereotype.Service;
import vn.chiendt.cartservice.dto.response.CartItemResponse;
import vn.chiendt.cartservice.service.CartItemService;

import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Override
    public void addCartItem(Long userId, Long productId, Integer quantity) {

    }

    @Override
    public void updateCartItem(Long userId, Long productId, Integer quantity) {

    }

    @Override
    public void removeItem(Long userId, Long productId) {

    }

    @Override
    public void removeCart(Long userId) {

    }

    @Override
    public List<CartItemResponse> listItems(Long userId) {
        return List.of();
    }
}
