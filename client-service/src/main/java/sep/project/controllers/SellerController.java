package sep.project.controllers;

import java.security.Principal;
import java.sql.Date;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import sep.project.dto.RegistrationDTO;
import sep.project.model.PaymentMethod;
import sep.project.model.Seller;
import sep.project.security.JwtConfig;
import sep.project.security.UserTokenState;
import sep.project.services.PaymentMethodService;
import sep.project.services.SellerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/seller", produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerController {

	@Autowired
	private SellerService sellerService;
	
	@Autowired
	private PaymentMethodService paymentMethodService;
	
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
		
		//check if seller with this email already exists
		Seller checkSeller = sellerService.findByEmail(registrationDTO.getEmail());
		if(checkSeller != null) {
			logger.error("CANCELED | Registering a new client to the PaymentHub | Name: " + registrationDTO.getName());
			return new ResponseEntity<>("A client with this email address already exists!", HttpStatus.BAD_REQUEST);
		}
	
		//save the new seller
		Seller seller = sellerService.createSeller(registrationDTO);	
		Seller newSeller = sellerService.save(seller);

		if (newSeller == null) {
			logger.error("CANCELED | Registering a new client to the PaymentHub | Name: " + registrationDTO.getName());
			return ResponseEntity.status(400).build();
		}
	
		logger.info("COMPLETED | Registering a new client to the PaymentHub | Name: " + registrationDTO.getName());
		
		UsernamePasswordAuthenticationToken authentication = null;
	    try {
	      authentication = new UsernamePasswordAuthenticationToken(
	          registrationDTO.getEmail(), registrationDTO.getPassword(), Collections.emptyList());
	    } catch (AuthenticationException e) {
	      return new ResponseEntity<>("An error occurred. Please try again.", HttpStatus.BAD_REQUEST);
	    }
	    
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	  
	    Long now = System.currentTimeMillis();
	    String token = Jwts.builder()
	        .setSubject(authentication.getName())  
	        .claim("authorities", authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
	        .setIssuedAt(new Date(now))
	        .setExpiration(new Date(now + jwtProvider.getExpiration() * 1000))  
	        .signWith(SignatureAlgorithm.HS512, jwtProvider.getSecret().getBytes())
	        .compact();
	    
	    return ResponseEntity.ok(new UserTokenState(token, newSeller.getEmail()));
	}

	  /**
	   * Adding a new payment method to an existing client
	   */
	  @PostMapping("/paymentmethod/{paymentMethod}")
	  public ResponseEntity<?> addPaymentMethod(Principal principal, @RequestHeader("Authorization") String authorization, @PathVariable String paymentMethod, @RequestBody Map<String, Object> fieldValues) {
	    
	    logger.info("INITIATED | Adding a new payment method to an existing client | Method: " + paymentMethod);
	    	    
	    //check if anyone is logged in
	    if(principal == null) {
	    	logger.error("CANCELED | Adding a new payment method to an existing client | Method: " + paymentMethod);
	    	return new ResponseEntity<>("You are not authorized to add a new payment method.", HttpStatus.UNAUTHORIZED);
	    }
	    
	    Seller seller = sellerService.findByEmail(principal.getName());
	    
	    //check if seller exists
	    if(seller == null) {
	    	logger.error("CANCELED | Adding a new payment method to an existing client | Method: " + paymentMethod);
	    	return ResponseEntity.status(400).build();
	    }
	    	    
	    //check if payment method exists
	    PaymentMethod pMethod = paymentMethodService.getByName(paymentMethod);
	    
	    System.out.println(pMethod);
	    	
	    if(pMethod == null) {
	    	logger.error("CANCELED | Adding a new payment method to an existing client | Method: " + paymentMethod);
	    	return ResponseEntity.status(400).build();
	    }
	    
	    Gson gsonObj = new Gson(); 
	    String jsonString = gsonObj.toJson(fieldValues);
	    
	    String url = "https://localhost:8762/api/" + paymentMethod + "/client";
	    
	    RestTemplate restTemplate = new RestTemplate();
	    
	    //add Authorization header
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", authorization);
	    HttpEntity<String> request = new HttpEntity<String>(jsonString, headers);
	    
	    //send request to add a new client to the payment method microservice database
	    try {
	      restTemplate.postForEntity(url, request, ResponseEntity.class);      
	    }
	    catch(HttpClientErrorException e) {
	      logger.error("CANCELED | Adding a new payment method to an existing client | Method: " + paymentMethod);
	      return ResponseEntity.status(400).build();
	    }
	    
	    //add payment method to the seller
	    seller.getPaymentMethods().add(pMethod);
	    sellerService.save(seller);

	    logger.info("COMPLETED | Adding a new payment method to an existing client | Method: " + paymentMethod);
	    return ResponseEntity.status(200).build();  
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
	
	@GetMapping("/whoami")
	public ResponseEntity<?> getLoggedInUsername(@RequestHeader("Authorization") String authorization, Principal principal){		
		if(principal == null) {
			return ResponseEntity.status(404).build();
		}
		
		String email = principal.getName();
		return ResponseEntity.ok(email);
	}
	
	
	@GetMapping("/nesto")
	public String proba() {

		return "proba";
	}

}
