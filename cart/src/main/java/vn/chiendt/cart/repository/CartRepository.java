package vn.chiendt.cart.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import vn.chiendt.cartservice.model.Cart;

public interface CartRepository extends MongoRepository<Cart,String> {
}
