package sep.project.services;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.paypal.api.payments.Agreement;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Currency;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.MerchantPreferences;
import com.paypal.api.payments.Patch;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentDefinition;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.Plan;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import sep.project.dto.PaymentDTO;
import sep.project.dto.SubscriptionDTO;
import sep.project.model.Client;
import sep.project.model.Subscription;
import sep.project.model.SubscriptionFrequency;
import sep.project.model.SubscriptionStatus;
import sep.project.model.TransactionStatus;


@Service
public class PayPalService {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private TransactionService transactionService;
		
	@Autowired
	private SubscriptionService subscriptionService;
		
	@Value("${success_url_payment}")
	private String successPaymentURL;
	
	@Value("${cancel_url_payment}")
	private String cancelPaymentURL;
	
	@Value("${success_url_agreement}")
	private String successAgreementURL;
	
	@Value("${cancel_url_agreement}")
	private String cancelAgreementURL;
	
	private String executionMode = "sandbox";
	
	public String createPayment(PaymentDTO paymentDTO, Client client) throws PayPalRESTException {
		
	    //create and save transaction with status INITIATED
	    sep.project.model.Transaction paypalTransaction = new sep.project.model.Transaction(paymentDTO, client);
	    sep.project.model.Transaction savedTransaction = transactionService.save(paypalTransaction);
		
	    Payer payer = new Payer();
	    payer.setPaymentMethod("paypal");
	    
	    RedirectUrls redirectUrls = new RedirectUrls();
	    redirectUrls.setCancelUrl(cancelPaymentURL+paypalTransaction.getId());
	    redirectUrls.setReturnUrl(successPaymentURL);

		Amount amount = new Amount();
		amount.setCurrency(paymentDTO.getPaymentCurrency());
		amount.setTotal(paymentDTO.getPaymentAmount().toString());
	    
	    Transaction transaction = new Transaction();
	    transaction.setAmount(amount);
	    transaction.setDescription("Payment for client with the email: " + client.getEmail());
	    
	    List<Transaction> transactions = new ArrayList<Transaction>();
	    transactions.add(transaction);
	    
	    //create the payment object
	    Payment payment = new Payment("sale", payer);
	    payment.setTransactions(transactions);
	    payment.setRedirectUrls(redirectUrls);
	    
	    APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);

    	String redirectUrl = "";
    	
	    try {	    	
	    	//create the payment
	    	Payment newPayment = payment.create(context);
	    		    
			//get the approval url from the response 
			Iterator links = newPayment.getLinks().iterator();		
			
			while(links.hasNext()) {
				Links link = (Links) links.next();
				
				if(link.getRel().equalsIgnoreCase("approval_url")) {
					redirectUrl = link.getHref();                  
    				break;
				}
			}
							
			//save payment id 
			savedTransaction.setPaymentId(newPayment.getId());
		} 
	    catch (PayPalRESTException e) {						
			//set transaction status to CANCELED
			savedTransaction.setStatus(TransactionStatus.CANCELED);
			transactionService.save(savedTransaction);
			
    	    throw e;    
		}
	    
        //set transaction status to CREATED
		savedTransaction.setStatus(TransactionStatus.CREATED);
		transactionService.save(savedTransaction);
	 
		//to redirect the customer to the paypal site	    
	    return redirectUrl;    
 	}
	
	public void executePayment(sep.project.model.Transaction transaction, String paymentId, String PayerID) throws PayPalRESTException {
		
		Payment payment = new Payment();
		payment.setId(paymentId);
		
	    PaymentExecution paymentExecution = new PaymentExecution();
	    paymentExecution.setPayerId(PayerID);
	    
	    Client client = transaction.getClient();
	    
        APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);

	    try {
	        //execute the payment
	        Payment createdPayment = payment.execute(context, paymentExecution);  
	        
	    } 
	    catch (PayPalRESTException e) {    
	    	//set transaction status to CANCELED
			transaction.setStatus(TransactionStatus.CANCELED);
			transactionService.save(transaction);
			
			throw e;
	    }  
	    
        //set transaction status to COMPLETED
		transaction.setStatus(TransactionStatus.COMPLETED);
		transactionService.save(transaction);
	}
	
	public Long createBillingPlan(SubscriptionDTO subscriptionDTO, Client client) throws PayPalRESTException {
		
		//create and save a new subscription with status INITIATED
		Subscription subscription = new Subscription(subscriptionDTO, client);
		Subscription savedSubscription = subscriptionService.save(subscription);
						
		//set currency and value
		Currency currency = new Currency();
		currency.setCurrency(subscriptionDTO.getPaymentCurrency());
		
		Double price = subscriptionDTO.getFrequency() == SubscriptionFrequency.YEAR ? 12*subscriptionDTO.getPaymentAmount() : subscriptionDTO.getPaymentAmount();				
		price = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP).doubleValue();
		
		System.out.println(price);
		currency.setValue(price.toString());
						
		PaymentDefinition paymentDefinition = new PaymentDefinition();
		paymentDefinition.setName(client.getEmail() + " subscription");
		paymentDefinition.setType("REGULAR");
		paymentDefinition.setFrequency(subscriptionDTO.getFrequency().toString());
		paymentDefinition.setFrequencyInterval("1");
		paymentDefinition.setCycles(subscriptionDTO.getCyclesNumber().toString());
		
		paymentDefinition.setAmount(currency);
		
		List<PaymentDefinition> paymentDefinitionList = new ArrayList<PaymentDefinition>();
		paymentDefinitionList.add(paymentDefinition);
				
		MerchantPreferences merchantPreferences = new MerchantPreferences(cancelAgreementURL+savedSubscription.getId(), successAgreementURL+savedSubscription.getId());
		merchantPreferences.setAutoBillAmount("YES");
		merchantPreferences.setInitialFailAmountAction("CONTINUE");
				
		//create a plan
		Plan plan = new Plan();
		plan.setType(subscriptionDTO.getType().toString());
		plan.setName(client.getEmail() + " subscription");
		plan.setDescription(subscriptionDTO.getPaymentAmount() + " " + subscriptionDTO.getPaymentCurrency() + " a " + subscriptionDTO.getFrequency().toString().toLowerCase());
		
		plan.setPaymentDefinitions(paymentDefinitionList);
		plan.setMerchantPreferences(merchantPreferences);
		
		APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);
		
		try {
			  //create the plan
			  Plan createdPlan = plan.create(context);
			  
			  //update plan state to ACTIVE
			  List<Patch> patchRequestList = new ArrayList<Patch>();
			  Map<String, String> value = new HashMap<String, String>();
			  value.put("state", "ACTIVE");

			  Patch patch = new Patch();
			  patch.setPath("/");
			  patch.setValue(value);
			  patch.setOp("replace");
			  patchRequestList.add(patch);

			  //activate the plan
			  createdPlan.update(context, patchRequestList);
			  			  
			  //save billing plan id
		   	  savedSubscription.setBillingPlanId(createdPlan.getId());		  
		} 
		catch (PayPalRESTException e) {			
			//set subscription status to CANCELED
			savedSubscription.setStatus(SubscriptionStatus.CANCELED);
			subscriptionService.save(savedSubscription);
		}	
		
		subscriptionService.save(savedSubscription);  
		
		return savedSubscription.getId();
	}
	
	
	public String createBillingAgreement(SubscriptionDTO subscriptionDTO, Client client, Long subscriptionId) throws PayPalRESTException, MalformedURLException, UnsupportedEncodingException {
		//get date for the agreement				
		Date date = new Date();		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, 1);
		
		//format defined in ISO8601
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String formattedDate = sdf.format(c.getTime());
		
		Subscription subscription = subscriptionService.getOne(subscriptionId);
		
		//set billing plan id
		Plan plan = new Plan();
		plan.setId(subscription.getBillingPlanId());
		
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");
				
		//create the agreement object
		Agreement agreement = new Agreement();
		agreement.setName(client.getEmail() + " subscription");
		agreement.setDescription(client.getEmail() + " subscription");
		agreement.setStartDate(formattedDate);
		
		agreement.setPlan(plan);	
		agreement.setPayer(payer);
						
		//save subscription with status BILLING_AGREEMENT_INITIATED
		Subscription savedSubscription = subscriptionService.save(subscription);
		
		APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);
		
		String redirectUrl = "";
		
		try {
			//create the agreement
			Agreement newAgreement = agreement.create(context);
						
			if(newAgreement != null) {
				//get the approval url from the response 
				Iterator links = newAgreement.getLinks().iterator();		
				
				while(links.hasNext()) {
					Links link = (Links) links.next();
					
					if(link.getRel().equalsIgnoreCase("approval_url")) {
						redirectUrl = link.getHref();                  
	    				break;
					}
				}
			}
		} 
		catch (PayPalRESTException e) {
			throw e;
		} 
		catch (MalformedURLException e) {
			throw e;
		} 
		catch (UnsupportedEncodingException e) {
			throw e;
		}
		
		//set subscription status to BILLING_AGREEMENT_CREATED
		savedSubscription.setStatus(SubscriptionStatus.CREATED);
		subscriptionService.save(savedSubscription);  
		
		//to redirect the customer to the paypal site	    
	    return redirectUrl;  
	}
	
	public void executeBillingAgreement(Subscription subscription, String token) throws PayPalRESTException {
		
		Agreement agreement =  new Agreement();
		agreement.setToken(token);
		
		Client client = subscription.getClient();
		
		APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);

		try {
			//execute the agreement and sign up the user for the subscription
			Agreement createdAgreement = agreement.execute(context, agreement.getToken());
			
			subscription.setBillingAgreementId(createdAgreement.getId());
			
		} 
		catch (PayPalRESTException e) {
			throw e;
		}
		
		//set status to COMPLETED
		subscription.setStatus(SubscriptionStatus.COMPLETED);
		subscriptionService.save(subscription);
	}
	
	/**
	 * Compare transaction statues with PayPal every hour
	 */
	//@Scheduled(initialDelay = 10000, fixedRate = 3600000)
	@Scheduled(initialDelay = 10000, fixedRate = 180000)
	public void synchronizeTransactions() {
		//find all PayPal clients
		List<Client> clientsList = clientService.findAll();
		
		for(Client client : clientsList) {
			//find all transaction with status INITIATED
			List<sep.project.model.Transaction> transactions = transactionService.findAllCreatedTransactions(client);
			
			APIContext context = new APIContext(client.getClientId(), client.getClientSecret(), executionMode);
			
			if(transactions.size() > 0) {				
				
				for(sep.project.model.Transaction transaction : transactions) {
					try {
						//get payment details
						Payment payment = Payment.get(context, transaction.getPaymentId());	
						
						System.out.println("KP: " + transaction.getStatus() + " | PP: " + payment.getState());
						
						if(payment.getState().equalsIgnoreCase("APPROVED")){ 
							//update transaction status to COMPLETED
							transaction.setStatus(TransactionStatus.COMPLETED);
							transactionService.save(transaction);
						}
						else if(payment.getState().equalsIgnoreCase("FAILED")){
							//update transaction status to CANCELED
							transaction.setStatus(TransactionStatus.CANCELED);
							transactionService.save(transaction);
						}
						
					}
					catch(PayPalRESTException e) {
						//if transaction doesn't exist
						if(e.getResponsecode() == 404) {
							transaction.setStatus(TransactionStatus.CANCELED);
							transactionService.save(transaction);
						}
					}
				}
			}
			
			//find all subscriptions with status ACTIVE
			List<sep.project.model.Subscription> subscriptions = subscriptionService.getSubscriptionForSynchronizing(client);
			
			if(subscriptions.size() > 0) {
				for(Subscription subscription : subscriptions) {
					try {
						Agreement agreement = Agreement.get(context, subscription.getBillingAgreementId());
						
						System.out.println("KP: " + subscription.getStatus() + " | PP: " + agreement.getState());
						
						if(agreement.getState().equalsIgnoreCase("ACTIVE")){ 
							//update subscription status to COMPLETED
							subscription.setStatus(SubscriptionStatus.COMPLETED);
							subscriptionService.save(subscription);
						}
						else if(agreement.getState().equalsIgnoreCase("CANCELLED")){
							//update subscruption status to CANCELED
							subscription.setStatus(SubscriptionStatus.CANCELED);
							subscriptionService.save(subscription);
						}
						else if(agreement.getState().equalsIgnoreCase("EXPIRED")){
							//update subscruption status to EXPIRED
							subscription.setStatus(SubscriptionStatus.EXPIRED);
							subscriptionService.save(subscription);
						}
					}
					catch(PayPalRESTException e) {
						//if subscription doesn't exist
						if(e.getResponsecode() == 404) {
							subscription.setStatus(SubscriptionStatus.CANCELED);
							subscriptionService.save(subscription);
						}
					}
				}
			}
		}
	}
	
}
		
