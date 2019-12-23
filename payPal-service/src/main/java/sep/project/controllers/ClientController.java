package sep.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<?> addClient(@RequestBody Client client) {
		
		logger.info("INITIATED | Adding a new PaymentHub client to the PayPal database | Email: " + client.getEmail());

		Client newClient = clientService.save(client);
		
		if(newClient != null) {
			logger.info("COMPLETED | Adding a new PaymentHub client to the PayPal database | Email: " + client.getEmail());
			return ResponseEntity.status(201).build();
		}
		else {
			logger.error("CANCELED | Adding a new PaymentHub client to the PayPal database | Email: " + client.getEmail());
			return ResponseEntity.status(400).build();
		}
	}
	
}
