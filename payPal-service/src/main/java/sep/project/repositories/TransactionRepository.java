package sep.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import sep.project.model.Client;
import sep.project.model.Transaction;
import sep.project.model.TransactionStatus;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
	
	Transaction findByPaymentId(String paymentId);
	
	List<Transaction> findByClientAndStatusIn(Client client, List<TransactionStatus> statuses);
	
	@Query("SELECT distinct trans FROM Transaction trans inner join trans.client as m WHERE trans.orderId = ?1 and m.email = ?2 ")
	public Transaction findMerchantTransactionBasedOnId(Long id, String email);
}
