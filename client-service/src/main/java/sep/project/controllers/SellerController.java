package sep.project.controllers;

import java.util.Set;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.model.PaymentMethod;
import sep.project.model.Seller;
import sep.project.services.SellerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/seller", produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerController {

	@Autowired
	private SellerService sellerService;
	
	private static final Logger logger = LoggerFactory.getLogger(SellerController.class);

	/**
	 * Registering a new client (seller) to the PaymentHub
	 */
	@PostMapping("")
	public ResponseEntity<?> addSeller(@RequestBody Seller seller) {
		
		logger.info("INITIATED | Registering a new client to the PaymentHub | Name: " + seller.getName());
		
		Seller newSeller = sellerService.addSeller(seller);

		if(newSeller != null) {
			logger.info("COMPLETED | Registering a new client to the PaymentHub | Name: " + seller.getName());
			return new ResponseEntity<>(newSeller, HttpStatus.CREATED);
		}
		else {
			logger.error("CANCELED | Registering a new client to the PaymentHub | Name: " + seller.getName());
			return ResponseEntity.status(400).build();
		}

	}

	/**
	 * Adding a new payment method to an existing client
	 */
	@PutMapping("/paymentmethod/{sellerId}")
	public ResponseEntity<?> addPaymentMethod(@PathVariable Long sellerId, @RequestBody PaymentMethod paymentMethod) {
		
		logger.info("INITIATED | Adding a new payment method to an existing client | Method: " + paymentMethod.getName());

		Seller seller = sellerService.addPayment(sellerId, paymentMethod);

		if(seller != null) {
			logger.info("COMPLETED | Adding a new payment method to an existing client | Method: " + paymentMethod.getName());
			return ResponseEntity.status(200).build();
		}
		else {
			logger.error("CANCELED | Adding a new payment method to an existing client | Method: " + paymentMethod.getName());
			return ResponseEntity.status(400).build();
		}
	}

	/**
	 * Getting all available payment methods for an existing client
	 */
	@GetMapping("/paymentmethod/{sellerEmail}")
	public ResponseEntity<?> getPaymentMethods(@PathVariable String sellerEmail) {
		
		logger.info("INITIATED | Getting all available payment methods for an existing client | Email: " + sellerEmail);

		Set<PaymentMethod> paymentMethods = sellerService.getPayments(sellerEmail);
		
		if(paymentMethods != null) {
			logger.info("COMPLETED | Getting all available payment methods for an existing client | Email: " + sellerEmail);
			return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
		}
		else {
			logger.error("CANCELED | Getting all available payment methods for an existing client | Email: " + sellerEmail);
			return ResponseEntity.status(400).build();
		}
	}

	@GetMapping("/nesto")
	public String proba() {

		return "proba";
	}

}
