package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.model.Client;
import sep.project.model.PaymentMethod;
import sep.project.services.ClientService;

@RestController
@RequestMapping(value = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {
	
	@Autowired
	private ClientService clientService;
	
	@PostMapping("")
	public ResponseEntity<?> addClient(@RequestBody Client client) {
		Client newClient = clientService.save(client);
		
		System.out.println("Dodavanje novog klijenta");
		
		return (newClient != null) ? new ResponseEntity<>(newClient, HttpStatus.CREATED) : ResponseEntity.status(400).build();
	}
	
	@GetMapping("nesto")
	public String proba() {
		
		return "poyyy";
	}

}
