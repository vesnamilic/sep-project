package sep.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.base.rest.PayPalRESTException;

import sep.project.dto.BillingAgreementDTO;
import sep.project.dto.BillingPlanDTO;
import sep.project.dto.PaymentDTO;
import sep.project.model.BillingPlan;
import sep.project.model.Client;
import sep.project.model.Subscription;
import sep.project.model.Transaction;
import sep.project.services.BillingPlanService;
import sep.project.services.ClientService;
import sep.project.services.PayPalService;
import sep.project.services.SubscriptionService;
import sep.project.services.TransactionService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class PayPalController {

	@Autowired
	private ClientService clientService;

	@Autowired
	private PayPalService payPalService;
	
	@Autowired 
	private TransactionService transactionService;
	
	@Autowired 
	private SubscriptionService subscriptionService;
	
	@Autowired
	private BillingPlanService billingPlanService;

	private static final Logger logger = LoggerFactory.getLogger(PayPalController.class);

	@Value("${error_url}")
	private String errorURL;

	@Value("${success_url_redirect}")
	private String succesURLRedirect;

	
	/**
	 * Create a new PayPal transaction
	 */
	@PostMapping(value = "/create")
	public ResponseEntity<?> createPayment(@RequestBody PaymentDTO paymentDTO) {

		logger.info("INITIATED | PayPal Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());

		//check if paypal client exists
		Client client = clientService.findByEmail(paymentDTO.getEmail());
		if (client == null) {
			logger.error("CANCELED | PayPal Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());
			return ResponseEntity.ok(errorURL);
		}

		String redirectUrl = "";
		try {
			
			redirectUrl = payPalService.createPayment(paymentDTO, client);
		} 
		catch (PayPalRESTException e) {	
			logger.error("CANCELED | PayPal Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());
			return ResponseEntity.ok(errorURL);
		}

		logger.info("COMPLETED | PayPal Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());

		//redirect user to the paypal site
		return ResponseEntity.ok(redirectUrl);
	}

	
	/**
	 * Execute a PayPal transaction
	 */
	@GetMapping(value = "/complete")
	public ResponseEntity<?> executePayment(@RequestParam String email, @RequestParam String paymentId, @RequestParam String token, @RequestParam String PayerID) {

		logger.info("INITIATED | PayPal Payment Execution");
		
		//TODO: what if transaction doesn't exist
		Transaction transaction = transactionService.findByPaymentId(paymentId);

		// check if paypal client exists
		Client client = clientService.findByEmail(email);
		if (client == null) {
			logger.error("CANCELED | PayPal Payment Execution");

			// redirect to the error page
			HttpHeaders headersRedirect = new HttpHeaders();
			headersRedirect.add("Location", transaction.getErrorUrl());
			headersRedirect.add("Access-Control-Allow-Origin", "*");
			return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
		}

		try {
			
			payPalService.executePayment(paymentId, token, PayerID, client);
		} 
		catch (PayPalRESTException e) {
			logger.error("CANCELED | PayPal Payment Execution");

			// redirect to the error page
			HttpHeaders headersRedirect = new HttpHeaders();
			headersRedirect.add("Location", transaction.getErrorUrl());
			headersRedirect.add("Access-Control-Allow-Origin", "*");
			return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
		}

		logger.info("COMPLETED | PayPal Payment Execution");

		// redirect to the success page
		HttpHeaders headersRedirect = new HttpHeaders();
		headersRedirect.add("Location", transaction.getSuccessUrl());
		headersRedirect.add("Access-Control-Allow-Origin", "*");
		return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
	}

	
	/**
	 * Create a new PayPal billing plan
	 */
	@PostMapping(value = "/plan/create")
	public ResponseEntity<?> createBillingPlan(@RequestBody BillingPlanDTO billingPlanDTO){
	  
		logger.info("INITIATED | PayPal Billing Plan | Amount: " + billingPlanDTO.getPaymentAmount() + " " + billingPlanDTO.getPaymentCurrency());
	  
		//check if paypal client exists
		Client client = clientService.findByEmail(billingPlanDTO.getEmail());
		if(client == null) { 
			logger.error("CANCELED | PayPal Billing Plan | Amount: " + billingPlanDTO.getPaymentAmount() + " " + billingPlanDTO.getPaymentCurrency());
			return ResponseEntity.ok(errorURL); 
		}
	  
		try {
			
			payPalService.createBillingPlan(billingPlanDTO, client);
		}
		catch (PayPalRESTException e) {			
			logger.error("CANCELED | PayPal Billing Plan | Amount: " + billingPlanDTO.getPaymentAmount() + " " + billingPlanDTO.getPaymentCurrency());
			return ResponseEntity.ok(errorURL);
		}

		logger.info("COMPLETED | PayPal Billing Plan | Amount: " + billingPlanDTO.getPaymentAmount() + " " + billingPlanDTO.getPaymentCurrency());

		//TODO: fix this url
		return ResponseEntity.ok(succesURLRedirect);  
	  }
	  
	/**
	 * Create a new billing agreement (subscription)
	 */
	@PostMapping(value = "/agreement/create") 
	public ResponseEntity<?> createBillingAgreement(@RequestBody BillingAgreementDTO billingAgreementDTO) {
	  
		logger.info("INITIATED | PayPal Billing Agreement");
	  
		//check if paypal client exists and if billing plan exists
		Client client = clientService.findByEmail(billingAgreementDTO.getEmail());
		BillingPlan billingPlan = billingPlanService.getOne(billingAgreementDTO.getBillingPlanId());
		if(client == null || billingPlan == null) { 
			logger.error("CANCELED | PayPal Billing Agreement");
			return ResponseEntity.ok(errorURL); 
		}
	  
		String redirectUrl = "";
		try {
			  
			redirectUrl = payPalService.createBillingAgreement(billingAgreementDTO, client, billingPlan);
		}
		catch (PayPalRESTException e) {			
			logger.error("CANCELED | PayPal Billing Agreement");
			return ResponseEntity.ok(errorURL);
		}
		catch (Exception e) {
			logger.error("CANCELED | PayPal Billing Agreement");
			return ResponseEntity.ok(errorURL);
		}
		  
		logger.info("COMPLETED | PayPal Billing Agreement");

		//redirect user to the paypal site
		return ResponseEntity.ok(redirectUrl);	  
	}
	 	
	
	/**
	 * Execute a billing agreement
	 */
	@GetMapping(value = "/agreement/complete") 
	public ResponseEntity<?> executeBillingAgreement(@RequestParam String email, @RequestParam String token) {
  
		logger.info("INITIATED | PayPal Billing Agreement Execution");
		
		//TODO: what if subscription doesn't exist
		Subscription subscription = subscriptionService.findByToken(token);
  
		//check if paypal client exists
		Client client = clientService.findByEmail(email);
		if(client == null) { 
			logger.error("CANCELED | PayPal Billing Agreement Execution");
			  
			HttpHeaders headersRedirect = new HttpHeaders();
			headersRedirect.add("Location", subscription.getErrorUrl());
			headersRedirect.add("Access-Control-Allow-Origin", "*"); 
			return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
		}
		  
		try {
			
			payPalService.executeBillingAgreement(client, token);
		}
		catch(PayPalRESTException e) {
			logger.error("CANCELED | PayPal Billing Agreement Execution");
			  
			HttpHeaders headersRedirect = new HttpHeaders();
			headersRedirect.add("Location", subscription.getErrorUrl());
			headersRedirect.add("Access-Control-Allow-Origin", "*"); 
			return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
		}
		  
		logger.info("COMPLETED | PayPal Billing Agreement Execution");

		//redirect to the success page
		HttpHeaders headersRedirect = new HttpHeaders();
		headersRedirect.add("Location", subscription.getSuccessUrl());
		headersRedirect.add("Access-Control-Allow-Origin", "*");
		return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
	}
		

	@GetMapping("/nesto")
	public String proba() {

		return "proba";
	}
}
