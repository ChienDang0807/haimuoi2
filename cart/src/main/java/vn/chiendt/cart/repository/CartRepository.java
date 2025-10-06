package vn.chiendt.cart.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import vn.chiendt.cart.common.CartState;
import vn.chiendt.cart.model.Cart;

import java.util.Optional;


public interface CartRepository extends MongoRepository<Cart,String> {

    Optional<Cart> findByUserIdAndCartState(Long id, CartState state);

    @Query(value = "{ 'userId': ?0 }")
    @Update("{ '$pull': { 'cartItems': { 'productId': ?1 } } }")
    void removeCartItem(Long userId, Long itemId);


    void deleteByUserId(Long userId);
}
