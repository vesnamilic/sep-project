package sep.project.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import sep.project.dto.FieldDTO;
import sep.project.model.Client;
import sep.project.services.ClientService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
	
	@Autowired
	ClientService clientService;
	
	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	
	/**
	 * Adding a new PaymentHub client to the PayPal database 
	 */
	@PostMapping("")
	public ResponseEntity<?> addClient(@RequestHeader("Authorization") String authorization, @RequestBody String clientString) {		
        
		logger.info("INITIATED | Adding a new PaymentHub client to the PayPal database");
		
		String email = "";

		String url = "https://localhost:8762/api/client/seller/whoami";
	    
	    RestTemplate restTemplate = new RestTemplate();
	    	    
	    //add Authorization header
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", authorization);
	    
	    HttpEntity<?> httpEntity = new HttpEntity<Object>(headers);
	    
	    //send request to find out who is logged in
	    try {
	    	ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
	      
	      email = (String) response.getBody();
	    }
	    catch(HttpClientErrorException e) {
	    	logger.error("CANCELED | Adding a new PaymentHub client to the PayPal database");
	    	return ResponseEntity.status(401).build();
	    }
	    catch(Exception e) {
	    	logger.error("CANCELED | Adding a new PaymentHub client to the PayPal database");
			return ResponseEntity.status(401).build();
	    }
	    	
		Gson gson = new Gson();
		Client client;
		
		try {
			client = gson.fromJson(clientString, Client.class);
		}
		catch(JsonSyntaxException e) {
			logger.error("CANCELED | Adding a new PaymentHub client to the PayPal database");
			return ResponseEntity.status(400).build();
		}
		        
        client.setEmail(email);
                                		
		//check if client with this email address already exists
		Client checkClient = clientService.findByEmail(client.getEmail());	
		if(checkClient != null) {
			logger.error("CANCELED | Adding a new PaymentHub client to the PayPal database");
			return ResponseEntity.status(400).build();
		}

		Client newClient = clientService.save(client);
		
		if(newClient != null) {
			logger.info("COMPLETED | Adding a new PaymentHub client to the PayPal database");
			return ResponseEntity.status(200).build();
		}
		else {
			logger.error("CANCELED | Adding a new PaymentHub client to the PayPal database");
			return ResponseEntity.status(400).build();
		}	
	}
	
	/**
	 * Getting fields for registration dynamic form
	 */
	@GetMapping("fields")
	public ResponseEntity<?> getFields() {
		
		logger.info("INITIATED | Getting fields for registration dynamic form");


		List<FieldDTO> fields = clientService.getFields();
		
		if(fields != null) {
			logger.info("COMPLETED | Getting fields for registration dynamic form");
			return new ResponseEntity<>(fields, HttpStatus.OK);
		}
		else {
			logger.error("CANCELED | Getting fields for registration dynamic form");
			return ResponseEntity.status(400).build();
		}
	}
	
}
