package sep.project.services;

import java.util.ArrayList;
import java.util.List;

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


@Service
public class PayPalService {
	
	@Autowired
	private ClientService clientService;
	
	private String executionMode = "sandbox";
	
	public boolean createPayment(CreatePaymentDTO paymentDTO){
		
		Client client = clientService.getClient(paymentDTO.getEmail());
		
		if(client == null) {
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
	    	    
	    APIContext apiContext = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);
	    
	    try {
	    	String redirectUrl = "";
	    	
	    	Payment newPayment = payment.create(apiContext);
	    	
	    	//System.out.println("created payment object details:" + newPayment.toString());
	    
	    	if(newPayment != null) {
	    		for(Links link : newPayment.getLinks()) {
	    			if(link.getRel().equals("approval_url")) {
	    				redirectUrl = link.getHref();
	    				
	    				System.out.println(redirectUrl);
	                    break;
	    			}
	    		}
	    	}
	    		    	
		} catch (PayPalRESTException e) {
			System.err.println(e.getDetails());
			
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
		    	//System.out.println("confirmed payment object details:" + createdPayment.toString());

	        }
	    } catch (PayPalRESTException e) {
	        System.err.println(e.getDetails());
	        
	        return false;
	    }
	    
	    return true;
	}
}
