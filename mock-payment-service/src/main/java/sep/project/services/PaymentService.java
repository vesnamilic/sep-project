package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.dto.PaymentDTO;
import sep.project.model.Client;
import sep.project.model.Transaction;
import sep.project.model.TransactionStatus;

@Service
public class PaymentService {
	
	@Autowired 
	private TransactionService transactionService;

	public Transaction createPayment(PaymentDTO paymentDTO, Client client) {
		
		Transaction transaction = new Transaction(paymentDTO, client);
		transaction.setStatus(TransactionStatus.COMPLETED);
		Transaction savedTransaction = transactionService.save(transaction);
		
		return savedTransaction;
	}

}
