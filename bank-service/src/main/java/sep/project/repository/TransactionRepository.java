package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	Transaction findByMerchantOrderId(Long merchantOrderId);

}
