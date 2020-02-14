package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.Transaction;
import sep.project.repositories.TransactionRepository;

@Service
public class TransactionService {
	
	@Autowired
	TransactionRepository transactionRepository;
	
	public Transaction save(Transaction transaction) {
		
		return transactionRepository.save(transaction);						
	}
	
	public Transaction findByPaymentId(String paymentId) {
		
		return transactionRepository.findByPaymentId(paymentId);
	}
	
}
