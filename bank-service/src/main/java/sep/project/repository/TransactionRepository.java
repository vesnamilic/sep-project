package sep.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sep.project.enums.Status;
import sep.project.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	Transaction findByPaymentID(String paymentId);

	List<Transaction> findAllByStatus(Status status);

	@Query("SELECT distinct trans FROM Transaction trans inner join trans.seller as s WHERE trans.merchantOrderId = ?1 and s.email = ?2 ")
	public Transaction findSellerTransactionBasedOnId(Long id, String email);
}
