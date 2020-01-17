package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.DTOs.CompletedDTO;
import sep.project.DTOs.PayRequestDTO;
import sep.project.services.BankService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankController {

	@Autowired
	BankService bankService;

	@PostMapping(value = "/create")
	public ResponseEntity<String> initiatePayment(@RequestBody PayRequestDTO request) {

		return bankService.initiatePayment(request);

	}

	@PostMapping(value = "/finishPayment")
	public ResponseEntity finishPayment(@RequestBody CompletedDTO completedDTO) {

		return bankService.finishPayment(completedDTO);

	}

}
