package vn.chiendt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.chiendt.model.SaleOrderItem;

@Repository
public interface SaleOrderItemRepository extends JpaRepository<SaleOrderItem,String> {
}
