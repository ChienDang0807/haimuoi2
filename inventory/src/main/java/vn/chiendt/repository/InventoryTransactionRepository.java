package vn.chiendt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.chiendt.model.InventoryTransaction;

import java.util.Optional;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction,Long> {
    Optional<InventoryTransaction> findByProductId(Long productId);

    // pessimistic locking
    @Query("SELECT i FROM InventoryTransaction i WHERE i.id= :inventoryTransactionId")
    Optional<InventoryTransaction> findByIdAndLock(Long inventoryTransactionId);
}
