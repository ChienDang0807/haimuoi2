package vn.chiendt.cartservice.service;

public interface CartService {
    void createCart (Long userId);
    void getCart (Long userId);
    void clearCart (Long userId);

}
