package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	
	/**
	 * Adding a new PaymentHub client to the PayPal database 
	 */
	@PostMapping("")
	public ResponseEntity<?> addClient(@RequestBody Client client) {
		Client newClient = clientService.save(client);
		
		System.out.println("Adding a new client: " + client.getEmail());
		
		return (newClient != null) ? ResponseEntity.status(201).build() : ResponseEntity.status(400).build();
	}
	

}
