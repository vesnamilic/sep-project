package sep.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.enums.Status;
import sep.project.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	
	List<Transaction> findAllByStatus(Status s);
	
	Transaction findByPaymentId(String paymentId);

}
