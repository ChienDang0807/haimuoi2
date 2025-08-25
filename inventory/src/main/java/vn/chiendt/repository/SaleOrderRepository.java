package vn.chiendt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.chiendt.model.SaleOrder;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, String> {
}
