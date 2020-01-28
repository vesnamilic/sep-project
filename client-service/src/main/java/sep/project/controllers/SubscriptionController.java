package sep.project.controllers;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.BillingAgreementDTO;
import sep.project.dto.BillingPlanDTO;
import sep.project.dto.OrderResponseDTO;
import sep.project.dto.PaymentResponse;
import sep.project.dto.SubscriptionDTO;
import sep.project.model.Seller;
import sep.project.model.Subscription;
import sep.project.model.SubscriptionStatus;
import sep.project.services.SellerService;
import sep.project.services.SubscriptionService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/subscription", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubscriptionController {
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private SellerService sellersService;
	
	private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);
	
	@PostMapping("/create/{paymentMethod}")
	private ResponseEntity<?> createSubscriptionPlan(@PathVariable String paymentMethod){
		
		return ResponseEntity.ok().build();
	}

	@PostMapping("/create")
	private ResponseEntity<?> createSubscription(@RequestBody @Valid SubscriptionDTO subscriptionDTO) {
		
		logger.info("INITIATED | Creating a new subscription | Email: " + subscriptionDTO.getEmail());

		//check if seller with this email address exists
		Seller seller = this.sellersService.findByEmail((subscriptionDTO.getEmail()));
		if (seller == null) {
			logger.error("CANCELED | Creating a new subscription | Email: " + subscriptionDTO.getEmail());
			return ResponseEntity.status(400).body("There is no seller with the given email address!");
		}
		
		//TODO: check if he has paypal? or not
		
		Date date = new Date();
		DateTime originalDateTime = new DateTime(date);
		DateTime expirationDateTime = originalDateTime.plusDays(1);
		
		//create and save the subscription
		Subscription subscription = new Subscription(UUID.randomUUID().toString(), expirationDateTime.toDate(), SubscriptionStatus.CREATED, subscriptionDTO.getSuccessUrl(), subscriptionDTO.getErrorUrl(), subscriptionDTO.getFailedUrl(), seller);		
		Subscription newSubscription = subscriptionService.save(subscription);

		if (newSubscription == null) {
			logger.error("CANCELED | Creating a new subscription | Email: " + subscriptionDTO.getEmail());
			return ResponseEntity.status(500).build();
		}
		
		logger.info("COMPLETED | Creating a new subscription | Email: " + subscriptionDTO.getEmail());
		
		OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
		orderResponseDTO.setRedirectUrl("https://localhost:4200/#/subscription/" + newSubscription.getUuid());
		orderResponseDTO.setUuid(newSubscription.getUuid());
		return ResponseEntity.ok(orderResponseDTO);
	}
	
	@PutMapping("/complete/{uuid}/{planId}")
	private ResponseEntity<?> sendOrder(@PathVariable String uuid, @PathVariable Long planId) {

		logger.info("INITIATED | Sending subscription to the payment service | Subscription uuid: " + uuid);
		
		//check if subscription exists
		Subscription subscription = this.subscriptionService.findByUuid(uuid);		
		if (subscription == null) {
			logger.error("CANCELED | Sending subscription to the payment service | Subscription uuid: " + uuid);
			return ResponseEntity.status(400).body("There is no subscription with the given id.");
		}
		
		//check if subscription is already sent or has expired
		if(subscription.getSubscriptionStatus().equals(SubscriptionStatus.SENT) || this.subscriptionService.isExpired(subscription.getExpirationDate())) {
			logger.error("CANCELED | Sending subscription to the payment service | Subscription uuid: " + uuid);
			return ResponseEntity.status(400).body("Subscription has expired.");
		}
		
		Seller seller = subscription.getSeller();
		
		//send subscription information to PayPal service
		BillingAgreementDTO billingAgreementDTO = new BillingAgreementDTO(seller.getEmail(), planId, subscription.getSuccessUrl(), subscription.getErrorUrl(), subscription.getFailedUrl());

		String url = "https://localhost:8762/api/paypal/agreement/create";
	    
	    RestTemplate restTemplate = new RestTemplate();

	    String redirectUrl;
		try {
			ResponseEntity<?> response = restTemplate.postForEntity(url, billingAgreementDTO, String.class);  
			
			redirectUrl = (String) response.getBody();
		} 
		catch (RestClientException e) {
			logger.error("CANCELED | Sending subscription to the payment service | Subscription uuid: " + uuid);
			return ResponseEntity.status(400).build();
		}
		
		if (redirectUrl == null) {
			logger.error("CANCELED | Getting subscription plans | Subscription uuid: " + uuid);
			return ResponseEntity.status(500).build();
		}
		
		logger.info("COMPLETED | Sending subscription to the payment service | Subscription uuid: " + uuid);
		
		//change subscription status to SENT and save subscription
		subscription.setSubscriptionStatus(SubscriptionStatus.SENT);
		subscriptionService.save(subscription);
		
		PaymentResponse response = new PaymentResponse();
		response.setUrl(redirectUrl);
		
		return ResponseEntity.ok(response);	
	}
	
	@GetMapping("/plans/{uuid}")
	private ResponseEntity<?> getSubscriptionPlans(@PathVariable String uuid) {
		
		logger.info("INITIATED | Getting subscription plans | Subscription uuid: " + uuid);
		
		//check if subscription exists
		Subscription subscription = this.subscriptionService.findByUuid(uuid);		
		if (subscription == null) {
			logger.error("CANCELED | Getting subscription plans | Subscription uuid: " + uuid);
			return ResponseEntity.status(400).body("There is no subscription with the given id.");
		}
		
		//check if subscription is already sent or has expired
		if(subscription.getSubscriptionStatus().equals(SubscriptionStatus.SENT) || this.subscriptionService.isExpired(subscription.getExpirationDate())) {
			logger.error("CANCELED | Getting subscription plans | Subscription uuid: " + uuid);
			return ResponseEntity.status(400).body("Subscription has expired.");
		}

		//find the seller
		Seller seller = subscription.getSeller();
		
		String url = "https://localhost:8762/api/paypal/client/plans/" + seller.getEmail();
	    
	    RestTemplate restTemplate = new RestTemplate();
	    
	    //send request to PayPal to get the billing plans for the subscription
    	List<BillingPlanDTO> billingPlanDTO;
	    try {
	    		    	
	    	ResponseEntity<?> response = restTemplate.getForEntity(url, List.class);
	    		      
	    	billingPlanDTO = (List<BillingPlanDTO>) response.getBody();
	    }
	    catch(HttpClientErrorException e) {
			logger.error("CANCELED | Getting subscription plans | Subscription uuid: " + uuid);
			return new ResponseEntity<>(e.getStatusCode());
	    }
	    catch(Exception e) {
			logger.error("CANCELED | Getting subscription plans | Subscription uuid: " + uuid);
			return ResponseEntity.status(500).build();
	    }

		if (billingPlanDTO == null) {
			logger.error("CANCELED | Getting subscription plans | Subscription uuid: " + uuid);
			return ResponseEntity.status(500).build();
		}
		
		logger.info("COMPLETED | Getting subscription plans | Subscription uuid: " + uuid);
		return ResponseEntity.ok(billingPlanDTO);
	}
}
