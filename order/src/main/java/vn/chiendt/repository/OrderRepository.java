package vn.chiendt.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.chiendt.model.Order;

@Repository
public interface OrderRepository  extends MongoRepository<Order,String> {
}
