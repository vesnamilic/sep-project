package sep.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import sep.project.model.Client;
import sep.project.model.Transaction;
import sep.project.model.TransactionStatus;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
	
	Transaction findByPaymentId(String paymentId);
	
	List<Transaction> findByClientAndStatus(Client client, TransactionStatus status);
}
