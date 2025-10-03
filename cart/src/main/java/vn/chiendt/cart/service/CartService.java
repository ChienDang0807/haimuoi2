package vn.chiendt.cart.service;

public interface CartService {
    void createCart (Long userId);
    void getCart (Long userId);
    void clearCart (Long userId);

}
