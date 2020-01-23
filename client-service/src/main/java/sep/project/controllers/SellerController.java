package sep.project.controllers;

import java.sql.Date;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import sep.project.dto.RegistrationDTO;
import sep.project.model.PaymentMethod;
import sep.project.model.Seller;
import sep.project.security.JwtConfig;
import sep.project.security.UserTokenState;
import sep.project.services.SellerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/seller", produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerController {

	@Autowired
	private SellerService sellerService;
	
	@Autowired
	private JwtConfig jwtProvider;
	
	@Lazy
	@Autowired
	private AuthenticationManager authenticationManger;

	private static final Logger logger = LoggerFactory.getLogger(SellerController.class);

	/**
	 * Registering a new client (seller) to the PaymentHub
	 */
	@PostMapping("")
	public ResponseEntity<?> addSeller(@RequestBody RegistrationDTO registrationDTO) {

		logger.info("INITIATED | Registering a new client to the PaymentHub | Name: " + registrationDTO.getName());
		
		Seller newSeller = sellerService.addSeller(this.sellerService.createSeller(registrationDTO));

		if (newSeller == null) {
			logger.error("CANCELED | Registering a new client to the PaymentHub | Name: " + registrationDTO.getName());
			return ResponseEntity.status(400).build();
		}
	
		logger.info("COMPLETED | Registering a new client to the PaymentHub | Name: " + registrationDTO.getName());
		Long now = System.currentTimeMillis();
		String token = Jwts.builder()
				.setSubject(newSeller.getEmail())	
				// Convert to list of strings. 
				// This is important because it affects the way we get them back in the Gateway.
				.claim("authorities", new HashMap<>())
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + jwtProvider.getExpiration() * 1000))  // in milliseconds
				.signWith(SignatureAlgorithm.HS512, jwtProvider.getSecret().getBytes())
				.compact();
		return ResponseEntity.ok(new UserTokenState(token, newSeller.getEmail()));

	}

	/**
	 * Adding a new payment method to an existing client
	 */
	@PutMapping("/paymentmethod/{sellerId}")
	public ResponseEntity<?> addPaymentMethod(@PathVariable Long sellerId, @RequestBody PaymentMethod paymentMethod) {

		logger.info(
				"INITIATED | Adding a new payment method to an existing client | Method: " + paymentMethod.getName());

		Seller seller = sellerService.addPayment(sellerId, paymentMethod);

		if (seller != null) {
			logger.info("COMPLETED | Adding a new payment method to an existing client | Method: "
					+ paymentMethod.getName());
			return ResponseEntity.status(200).build();
		} else {
			logger.error("CANCELED | Adding a new payment method to an existing client | Method: "
					+ paymentMethod.getName());
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

		if (paymentMethods != null) {
			logger.info(
					"COMPLETED | Getting all available payment methods for an existing client | Email: " + sellerEmail);
			return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
		} else {
			logger.error(
					"CANCELED | Getting all available payment methods for an existing client | Email: " + sellerEmail);
			return ResponseEntity.status(400).build();
		}
	}

	@GetMapping("/nesto")
	public String proba() {

		return "proba";
	}

}
