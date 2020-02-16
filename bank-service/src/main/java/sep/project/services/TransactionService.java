package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.Transaction;
import sep.project.repository.TransactionRepository;

@Service
public class TransactionService {
	
	@Autowired
	private TransactionRepository transactionRepository;

	public Transaction findMerchantTransactionBasedOnId(Long id, String email) {
		return transactionRepository.findSellerTransactionBasedOnId(id, email);
	}
	
}
