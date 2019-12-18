package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.DTOs.BankRequestDTO;
import sep.project.DTOs.BankResponseDTO;
import sep.project.DTOs.CompletedDTO;
import sep.project.services.BankService;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankController {

	@Autowired
	BankService bankService;

	@PostMapping(value = "/initiatePayment")
	public ResponseEntity<BankResponseDTO> initiatePayment(@RequestBody BankRequestDTO request) {

		System.out.println("DEBUG: initiatePayment called");
		ResponseEntity<BankResponseDTO> ret = bankService.initiatePayment(request);
		if (ret != null) {
			return ret;
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}
	
	@PostMapping(value = "/finishPayment")
	public ResponseEntity finishPayment(@RequestBody CompletedDTO request) {
		
		//TODO
		
		return null;

	}

}
