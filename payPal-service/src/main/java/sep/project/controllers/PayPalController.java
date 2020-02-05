package sep.project.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.paypal.base.rest.PayPalRESTException;

import sep.project.dto.PaymentDTO;
import sep.project.dto.RedirectDTO;
import sep.project.dto.SubscriptionDTO;
import sep.project.model.Client;
import sep.project.model.Subscription;
import sep.project.model.Transaction;
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

	private static final Logger logger = LoggerFactory.getLogger(PayPalController.class);
	
	/**
	 * Create a new PayPal transaction
	 */
	@PostMapping(value = "/create")
	public ResponseEntity<?> createPayment(@RequestBody @Valid PaymentDTO paymentDTO) {

		logger.info("INITIATED | PayPal Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());

		//check if paypal client exists
		Client client = clientService.findByEmail(paymentDTO.getEmail());
		if (client == null) {
			logger.error("CANCELED | PayPal Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		String redirectUrl = "";
		try {
			
			redirectUrl = payPalService.createPayment(paymentDTO, client);
		} 
		catch (PayPalRESTException e) {	
			logger.error("CANCELED | PayPal Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		logger.info("COMPLETED | PayPal Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());

		//redirect user to the paypal site
		return ResponseEntity.ok(redirectUrl);
	}

	
	/**
	 * Execute a PayPal transaction
	 */
	@GetMapping(value = "/complete")
	public ResponseEntity<?> executePayment(@RequestParam String paymentId, @RequestParam String token, @RequestParam String PayerID) {

		logger.info("INITIATED | PayPal Payment Execution");
		
		//TODO: what if transaction doesn't exist
		Transaction transaction = transactionService.findByPaymentId(paymentId);

		try {
			
			payPalService.executePayment(transaction, paymentId, PayerID);
		} 
		catch (PayPalRESTException e) {
			logger.error("CANCELED | PayPal Payment Execution");
			
			//update seller and get redirect url
			String url = updateSeller(transaction.getFailedUrl());

			// redirect to the error page
			HttpHeaders headersRedirect = new HttpHeaders();
			headersRedirect.add("Location", url);
			headersRedirect.add("Access-Control-Allow-Origin", "*");
			return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
		}

		logger.info("COMPLETED | PayPal Payment Execution");
		
		//update seller and get redirect url
		String url = updateSeller(transaction.getSuccessUrl());

		// redirect to the success page
		HttpHeaders headersRedirect = new HttpHeaders();
		headersRedirect.add("Location", url);
		headersRedirect.add("Access-Control-Allow-Origin", "*");
		return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
	}
	
	/**
	 * Create a new PayPal subscription
	 */
	@PostMapping("/subscription/create")
	public ResponseEntity<?> createSubscription(@RequestBody @Valid SubscriptionDTO subscriptionDTO) {
		
		logger.info("INITIATED | PayPal Subscription");

		//check if paypal client exists and if billing plan exists
		Client client = clientService.findByEmail(subscriptionDTO.getEmail());
		if(client == null) { 
			logger.error("CANCELED | PayPal Subscription");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		//CREATE A BILLING PLAN
		Long subscriptionId;
		try {
			
			subscriptionId = payPalService.createBillingPlan(subscriptionDTO, client);
		}
		catch (PayPalRESTException e) {			
			logger.error("CANCELED | PayPal Subscription");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		//CREATE A BILLING AGREEMENT
		String redirectUrl = "";
		try {
			  
			redirectUrl = payPalService.createBillingAgreement(subscriptionDTO, client, subscriptionId);
		}
		catch (PayPalRESTException e) {			
			logger.error("CANCELED | PayPal Subscription");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("CANCELED | PayPal Subscription");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		logger.info("COMPLETED | PayPal Subscription");

		//redirect user to the paypal site
		return ResponseEntity.ok(redirectUrl);
	} 	
	
	@GetMapping(value = "/subscription/complete")
	public ResponseEntity<?> executeSubscription(@RequestParam Long subscriptionId, @RequestParam String token) {
		
		logger.info("INITIATED | PayPal Subscription Execution");
		
		//TODO: what if subscription doesn't exist
		Subscription subscription = subscriptionService.getOne(subscriptionId);
		
		try {
			
			payPalService.executeBillingAgreement(subscription, token);
		}
		catch(PayPalRESTException e) {
			logger.error("CANCELED | PayPal Subscription Execution");
			
			//update seller and get redirect url
			String url = updateSeller(subscription.getFailedUrl());
			  
			HttpHeaders headersRedirect = new HttpHeaders();
			headersRedirect.add("Location", url);
			headersRedirect.add("Access-Control-Allow-Origin", "*"); 
			return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
		}
		
		logger.info("COMPLETED | PayPal Subscription Execution");
		
		//update seller and get redirect url
		String url = updateSeller(subscription.getSuccessUrl());
		
		//redirect to the success page
		HttpHeaders headersRedirect = new HttpHeaders();
		headersRedirect.add("Location", url);
		headersRedirect.add("Access-Control-Allow-Origin", "*");
		return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);

	}
	
	private String updateSeller(String url){
		
		RestTemplate restTemplate = new RestTemplate();
		
		String redirectUrl = null;
		
		try {
	    	ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.GET, null, RedirectDTO.class);
	       
	    	RedirectDTO redirect = (RedirectDTO) response.getBody();
	    	redirectUrl = redirect.getUrl();
	    } 
		catch (RestClientException e) {
			//TODO: what happens here?
		}
		
		return redirectUrl;
	}
}
