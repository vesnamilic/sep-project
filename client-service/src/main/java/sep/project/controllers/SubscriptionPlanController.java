package sep.project.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

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

import sep.project.dto.SubscriptionPlanDTO;
import sep.project.model.Seller;
import sep.project.model.Subscription;
import sep.project.model.SubscriptionPlan;
import sep.project.services.SellerService;
import sep.project.services.SubscriptionPlanService;
import sep.project.services.SubscriptionService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/subscriptionplan", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubscriptionPlanController {
	
	@Autowired
	SubscriptionPlanService subscriptionPlanService;
	
	@Autowired
	SellerService sellerService;
	
	@Autowired
	SubscriptionService subscriptionService;
	
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	/**
	 * Create subscription plans for the logged in seller
	 */
	@PostMapping("")
	public ResponseEntity<?> createSubscriptionPlan(Principal principal, @RequestBody @Valid SubscriptionPlanDTO subscriptionPlanDTO) {
		
		logger.info("INITIATED | Creating a new subscription plan");
	    
	    //check if anyone is logged in
	    if(principal == null) {
	    	logger.error("CANCELED | Creating a new subscription plan");
	    	return new ResponseEntity<>("You are not authorized to add a new subscription plan.", HttpStatus.UNAUTHORIZED);
	    }
	    
	    Seller seller = sellerService.findByEmail(principal.getName());
	    
	    //check if seller exists
	    if(seller == null  || !seller.isActivated()) {
	    	logger.error("CANCELED | Creating a new subscription plan");
	    	return ResponseEntity.status(400).build();
	    }
	    
	    //create and save the new subscription plan
	    SubscriptionPlan subscriptionPlan = new SubscriptionPlan(subscriptionPlanDTO, seller);
		SubscriptionPlan savedSubscriptionPlan = subscriptionPlanService.save(subscriptionPlan);
	    
	    if(savedSubscriptionPlan == null) {
	    	logger.error("CANCELED | Creating a new subscription plan");
	    	return ResponseEntity.status(500).build();
	    }
	    
	    logger.info("COMPLETED | Creating a new subscription plan");
	    
	    return ResponseEntity.ok().build();
	}
	
	/**
	 * Find available subscription plans for an order with the given UUID
	 */
	@GetMapping("/{uuid}")
	public ResponseEntity<?> findSubscriptionPlans(@PathVariable String uuid){
		
		logger.info("INITIATED | Getting subscription plans for the order");
		
		//check if subscription with the UUID exists
		Subscription subscription = subscriptionService.findByUuid(uuid);		
		if(subscription == null) {
			logger.error("CANCELED | Getting subscription plans for the order");
			return ResponseEntity.status(400).build();
		}
		
		//find the seller
		Seller seller = subscription.getSeller();
		
		List<SubscriptionPlan> subscriptionPlans = subscriptionPlanService.findBySeller(seller);
		
		if(subscriptionPlans == null) {
			logger.error("CANCELED | Getting subscription plans for the order");
			return ResponseEntity.status(500).build();
		}
		
		List<SubscriptionPlanDTO> dtoPlans = new ArrayList<SubscriptionPlanDTO>();
		for(SubscriptionPlan plan : subscriptionPlans) {
			dtoPlans.add(new SubscriptionPlanDTO(plan));
		}
		
		logger.info("COMPLETED | Getting subscription plans for the order");
		
		return ResponseEntity.ok(dtoPlans);
	}
}
