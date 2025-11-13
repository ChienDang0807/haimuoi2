package vn.chiendt.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chiendt.cart.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    long countCartItemsByCartId(String cartId);

    Optional<CartItem> findByCartIdAndProductId(String cartId, Long productId);

    @Query(value = """
        SELECT ci
        FROM CartItem ci
        JOIN Cart c ON c.id = ci.cartIdentifier
        WHERE c.userId = :userId
        """)
    List<CartItem> findAllByUserId(@Param("userId") Long userId);

    @Query(value = """
        SELECT ci
        FROM CartItem ci
        WHERE ci.cartIdentifier = :cartIdentifier
    """)
    List<CartItem> findAllByCartIdentifier(@Param("cartIdentifier") String cartIdentifier);
    @Query(value = """
        SELECT ci
        FROM CartItem ci
        JOIN Cart c ON c.id = ci.cartIdentifier
        WHERE c.userId = :userId
        AND ci.productId = :productId
        """)
    Optional<CartItem> findByCartIdentifierAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("""
        SELECT ci
        FROM CartItem ci
        WHERE ci.cartToken = :cartToken
        AND ci.productId = :productId
    """)
    Optional<CartItem> findByCartTokenAndProductId(@Param("cartToken") String cartToken, @Param("productId") Long productId);

    @Modifying
    @Query("""
        DELETE FROM CartItem ci
        WHERE ci.cartIdentifier IN ( SELECT c.id FROM Cart c WHERE c.userId = :userId)
    """)
    void deleteCartItemByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("""
        DELETE FROM CartItem ci
        WHERE ci.cartToken = :cartToken
    """)
    void deleteCartItemByCartToken (@Param("cartToken") String cartToken);
}
