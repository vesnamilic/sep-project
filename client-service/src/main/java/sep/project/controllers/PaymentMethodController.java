package sep.project.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.model.PaymentMethod;
import sep.project.model.Seller;
import sep.project.model.Subscription;
import sep.project.services.PaymentMethodService;
import sep.project.services.SubscriptionService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/paymentmethod", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentMethodController {
	
	@Autowired
	private PaymentMethodService paymentMethodService;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentMethodController.class);

	
	/**
	 * Adding a new payment method to the PaymentHub
	 */
	@PostMapping("")
	public ResponseEntity<?> addPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
		
		logger.info("INITIATED | Adding a new payment method | Name: " + paymentMethod.getName());
		
		PaymentMethod newPaymentMethod = paymentMethodService.addPaymentMethod(paymentMethod);
		
		if(newPaymentMethod != null) {
			logger.info("COMPLETED | Adding a new payment method | Name: " + paymentMethod.getName());
			return new ResponseEntity<>(newPaymentMethod, HttpStatus.CREATED);
		}
		else {
			logger.error("CANCELED | Adding a new payment method | Name: " + paymentMethod.getName());
			return 	ResponseEntity.status(400).build();
		}
		
	}
	
	/**
	 * Getting all available payment methods in the PaymentHub
	 */
	@GetMapping("")
	public ResponseEntity<?> getPaymentMethods(){
		
		logger.info("INITIATED | Getting all available payment methods");
		
		List<PaymentMethod> paymentMethods = paymentMethodService.getAll();
		
		if(paymentMethods != null) {
			logger.info("COMPLETED | Getting all available payment methods");
			return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
		}
		else {
			logger.error("CANCELED | Getting all available payment methods");
			return ResponseEntity.status(400).build();
		}
	}
	
	/**
	 * Getting all available payment methods for the subscription with the given UUID
	 */
	@GetMapping("/subscription/{uuid}")
	public ResponseEntity<?> getSubscriptionPaymentMethods(@PathVariable String uuid){
		 
		logger.info("INITIATED | Getting all available payment methods for the subscription");
		
		//check if subscription with the UUID exists
		Subscription subscription = subscriptionService.findByUuid(uuid);				
		if(subscription == null) {
			logger.error("CANCELED | Getting all available payment methods for the subscription");
			return ResponseEntity.status(400).build();
		}
		
		Seller seller = subscription.getSeller();
				
		List<PaymentMethod> paymentMethods = paymentMethodService.findSellerPaymentMethodsWithSubscription((seller));
				
		if(paymentMethods == null) {
			logger.error("CANCELED | Getting all available payment methods for the subscription");
			return ResponseEntity.status(400).build();
		}
		
		logger.info("COMPLETED | Getting all available payment methods for the subscription");
		
		return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
	}

}
