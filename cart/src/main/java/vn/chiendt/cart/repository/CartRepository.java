package vn.chiendt.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chiendt.cart.model.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,String> {

    @Query("""
    SELECT c
    FROM Cart c
    WHERE c.userId = :userId
    """)
    Optional<Cart> findByUserId(@Param("userId") Long userId);

    @Query("""
    SELECT c
    FROM Cart c
    WHERE c.cartToken = :cartToken
    """)
    Optional<Cart> findByCartToken(@Param("cartToken") String cartToken);

}
