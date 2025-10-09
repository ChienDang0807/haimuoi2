package vn.chiendt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.chiendt.model.InventoryItem;

import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    Optional<InventoryItem> findByProductId(Long productId);
    
    boolean existsByProductId(Long productId);
}
