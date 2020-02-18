package sep.project.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.OrderResponseDTO;
import sep.project.dto.OrderStatusInformationDTO;
import sep.project.dto.PaymentResponse;
import sep.project.dto.RedirectDTO;
import sep.project.dto.SubmitSubscriptionDTO;
import sep.project.dto.SubscriptionDTO;
import sep.project.dto.SubscriptionInformationDTO;
import sep.project.model.PaymentMethod;
import sep.project.model.Seller;
import sep.project.model.Subscription;
import sep.project.model.SubscriptionPlan;
import sep.project.model.SubscriptionStatus;
import sep.project.services.PaymentMethodService;
import sep.project.services.SellerService;
import sep.project.services.SubscriptionPlanService;
import sep.project.services.SubscriptionService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/subscription", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubscriptionController {
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private SellerService sellersService;
	
	@Autowired
	private SubscriptionPlanService subscriptionPlanService;
	
	@Autowired
	private PaymentMethodService paymentMethodService;
	
	private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

	/**
	 * Create a subscription and return the redirect link
	 */
	@PostMapping("/create")
	public ResponseEntity<?> createSubscription(@RequestBody @Valid SubscriptionInformationDTO subscriptionDTO) {
		
		logger.info("INITIATED | Creating a new subscription | Email: " + subscriptionDTO.getEmail());

		//check if seller with this email address exists
		Seller seller = this.sellersService.findByEmail((subscriptionDTO.getEmail()));
		if (seller == null  || !seller.isActivated()) {
			logger.error("CANCELED | Creating a new subscription | Email: " + subscriptionDTO.getEmail());
			return ResponseEntity.status(400).body("There is no seller with the given email address!");
		}
		
		//create and save the subscription
		Subscription subscription = new Subscription(subscriptionDTO, seller);		
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
	
	/**
	 * Submit the chosen subscription plan and send it to the chosen payment service
	 */
	@PutMapping("/complete/{orderUUID}")
	public ResponseEntity<?> submitSubscription(@PathVariable String orderUUID, @RequestBody @Valid SubmitSubscriptionDTO subscriptionDTO) {
		
		logger.info("INITIATED | Submiting subscription to the payment service");
		
		//check if subscription with the UUID exists
		Subscription subscription = subscriptionService.findByUuid(orderUUID);		
		if(subscription == null) {
			logger.error("CANCELED | Submiting subscription to the payment service");
			return ResponseEntity.status(400).build();
		}
		
		//check if subscription has expired or is already sent 
		if(!subscription.getSubscriptionStatus().equals(SubscriptionStatus.INITIATED) || this.subscriptionService.isExpired(subscription.getExpirationDate())) {
			logger.error("CANCELED | Submiting subscription to the payment service");
			return ResponseEntity.status(400).body("The subscription has expired.");
		}
		
		//check if payment method exists and if it supports subscription
		PaymentMethod paymentMethod = paymentMethodService.getByName(subscriptionDTO.getPaymentMethod());
		if (paymentMethod == null || !paymentMethod.isSubscription()) {
			logger.error("CANCELED | Submiting subscription to the payment service");
			return ResponseEntity.status(400).build();
		}
		
		//check if subscription plan exists
		SubscriptionPlan subscriptionPlan = subscriptionPlanService.getOne(subscriptionDTO.getSubscriptionPlanId());
		if (subscriptionPlan == null) {
			logger.error("CANCELED | Submiting subscription to the payment service");
			return ResponseEntity.status(400).build();
		}
		
		subscription.setPaymentMethod(paymentMethod.getName().toLowerCase());
		subscriptionService.save(subscription);
		
		//create a new billing agreement and send it to the payment method
		SubscriptionDTO dto = new SubscriptionDTO(subscriptionPlan, subscription);
		
		String url = "https://localhost:8762/api/" + paymentMethod.getName().toLowerCase() + "/subscription/create";
	    
	    RestTemplate restTemplate = new RestTemplate();

	    String redirectUrl;
		try {
			ResponseEntity<?> response = restTemplate.postForEntity(url, dto, String.class);  
			
			subscription.setSubscriptionStatus(SubscriptionStatus.CREATED);
			subscriptionService.save(subscription);
			
			redirectUrl = (String) response.getBody();
		} 
		catch (RestClientException e) {
			logger.error("CANCELED | Submiting subscription to the payment service");
			
			subscription.setSubscriptionStatus(SubscriptionStatus.CANCELED);
			subscriptionService.save(subscription);
			
			ResponseEntity<RedirectDTO> response2 = null;
			try {
				response2 = restTemplate.exchange(subscription.getErrorUrl(), HttpMethod.GET, null, RedirectDTO.class);
			} catch (RestClientException e2) {
				
				return ResponseEntity.status(400).body("An error occurred while trying to contact seller!");
			}
			
			return ResponseEntity.status(400).build();
		}
		
		if (redirectUrl == null) {
			logger.error("CANCELED | Submiting subscription to the payment service");
			return ResponseEntity.status(500).build();
		}
				
		PaymentResponse response = new PaymentResponse();
		response.setUrl(redirectUrl);
		
		logger.info("COMPLETED | Submiting subscription to the payment service");

		return ResponseEntity.ok(response);			
	}
	
	@GetMapping("/status")
	public ResponseEntity<?> getSubscriptionStatus(@RequestParam("orderId") Long id, @RequestParam("email") String email) {
		
		logger.info("INITIATED | Finding subscription status | Subscription id: " + id);
		
		Subscription subscription = this.subscriptionService.findClientSubscriptionBySubscriptionId(email, id);
		
		if(subscription == null) {
			logger.error("CANCELED | Finding subscription status | Subscription id: " + id);
			return ResponseEntity.notFound().build();
		}
					
		OrderStatusInformationDTO dto = new OrderStatusInformationDTO();
		dto.setStatus(subscription.getSubscriptionStatus().toString());
		
		logger.info("COMPLETED | Finding subscription status | Subscription id: " + id);
		
		return ResponseEntity.ok(dto);
	}

}
