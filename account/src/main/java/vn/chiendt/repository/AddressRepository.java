package vn.chiendt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.chiendt.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByUserIdAndAddressType(Long userId, Integer addressType);
}
