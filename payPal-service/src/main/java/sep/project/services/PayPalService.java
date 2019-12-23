package sep.project.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import sep.project.dto.ConfirmPaymentDTO;
import sep.project.dto.CreatePaymentDTO;
import sep.project.model.Client;
import sep.project.model.TransactionStatus;


@Service
public class PayPalService {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private TransactionService transactionService;
	
	private static final Logger logger = LoggerFactory.getLogger(PayPalService.class);
	
	private String executionMode = "sandbox";
	
	public boolean createPayment(CreatePaymentDTO paymentDTO){
		
		logger.info("INITIATED | PayPal Transaction | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());
		
		Client client = clientService.getClient(paymentDTO.getEmail());
		
		if(client == null) {
			logger.error("CANCELED | PayPal Transaction | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());
			
			return false;
		}
		
	    Payer payer = new Payer();
	    payer.setPaymentMethod("paypal");
	    
	    //TODO prepraviti URL-ove
	    RedirectUrls redirectUrls = new RedirectUrls();
	    redirectUrls.setCancelUrl("https://localhost:4200/cancel");
	    redirectUrls.setReturnUrl("https://localhost:4200/return");

		Amount amount = new Amount();
		amount.setCurrency(paymentDTO.getPaymentCurrency());
		amount.setTotal(paymentDTO.getPaymentAmount().toString());
	    
	    Transaction transaction = new Transaction();
	    transaction.setAmount(amount);
	    
	    List<Transaction> transactions = new ArrayList<Transaction>();
	    transactions.add(transaction);
	    
	    Payment payment = new Payment("sale", payer);
	    payment.setTransactions(transactions);
	    payment.setRedirectUrls(redirectUrls);
	    	    	    
	    //saving transaction with transaction status created
	    sep.project.model.Transaction paypalTransaction = new sep.project.model.Transaction(client, new Date(), TransactionStatus.INITIATED, paymentDTO.getPaymentAmount(), paymentDTO.getPaymentCurrency());
	    sep.project.model.Transaction savedTransaction = transactionService.save(paypalTransaction);
	    
	    APIContext apiContext = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);

	    try {
	    	String redirectUrl = "";
	    	
	    	Payment newPayment = payment.create(apiContext);
	    	
	    	//System.out.println("created payment object details:" + newPayment.toString());
	    
	    	if(newPayment != null) {
	    		
				logger.error("EXECUTED | PayPal Transaction | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());
	    		
	    		for(Links link : newPayment.getLinks()) {
	    			if(link.getRel().equals("approval_url")) {
	    				redirectUrl = link.getHref();
	    				
	    				System.out.println(redirectUrl);
	                    
	    				break;
	    			}
	    		}
	    	}
	    		    	
		} catch (PayPalRESTException e) {
			//System.err.println(e.getDetails());
			
			logger.error("CANCELED | PayPal Transaction | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());
			
			//edit transaction status to canceled
			savedTransaction.setStatus(TransactionStatus.CANCELED);
			transactionService.save(savedTransaction);
			
			return false;
		}
	    
	    return true;    	    
 	}
	
	public boolean completePayment(ConfirmPaymentDTO paymentDTO){
		
		Client client = clientService.getClient(paymentDTO.getEmail());
		
		if(client == null) {
			return false;
		}
		
		Payment payment = new Payment();
		payment.setId(paymentDTO.getPaymentId());

	    PaymentExecution paymentExecution = new PaymentExecution();
	    paymentExecution.setPayerId(paymentDTO.getPayerId());
	    
	    try {
	        APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);
	        Payment createdPayment = payment.execute(context, paymentExecution);
	        if(createdPayment!=null){
				logger.error("COMPLETED | PayPal Transaction Completion");

	        }
	    } catch (PayPalRESTException e) {
	        System.err.println(e.getDetails());
	        
			logger.error("CANCELED | PayPal Transaction Completion");
	        
	        return false;
	    }
	    
	    return true;
	}
}
