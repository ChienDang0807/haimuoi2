package vn.chiendt.repository;

import jdk.jfr.Registered;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.chiendt.model.Transaction;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "select t from Transaction t where lower(t.description) like :keyword ")
    Page<Transaction> searchTransactionByKeyword(String keyword, Pageable pageable);

    Optional<Transaction> getTransactionByPaymentId(String paymentId);
}
