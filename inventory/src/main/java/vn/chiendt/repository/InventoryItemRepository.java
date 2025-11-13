package vn.chiendt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.chiendt.model.InventoryItem;

import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    Optional<InventoryItem> findByProductId(Long productId);
    
    boolean existsByProductId(Long productId);

    @Query("SELECT it.availableQuantity FROM InventoryItem it WHERE it.productId = :productId")
    Integer findAvailableQuantityByProductId(@Param("productId") Long productId);
}
