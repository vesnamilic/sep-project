package sep.project.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.Client;
import sep.project.model.Transaction;
import sep.project.model.TransactionStatus;
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
	
	public List<Transaction> findAllCreatedTransactions(Client client){
		
		List<TransactionStatus> list = new ArrayList<TransactionStatus>();
		list.add(TransactionStatus.CREATED);
		
		return transactionRepository.findByClientAndStatusIn(client, list);
	}
}
