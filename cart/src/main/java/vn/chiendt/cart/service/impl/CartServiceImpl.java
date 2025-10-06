package vn.chiendt.cart.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.chiendt.cart.repository.CartRepository;
import vn.chiendt.cart.service.CartService;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public void createCart(Long userId) {

    }

    @Override
    public void getCart(Long userId) {

    }

    @Override
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }
}
