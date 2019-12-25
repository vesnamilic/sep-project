package sep.project.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.dto.FieldDTO;
import sep.project.model.Merchant;
import sep.project.services.MerchantService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class MerchantController {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	private MerchantService merchantService;
	
	
	@PostMapping("")
	public ResponseEntity<?> addClient(@RequestBody Merchant merchant) {
		
		logger.info("INITIATED | Adding a new PaymentHub client to the BitCoin database | Email: " + merchant.getEmail());

		Merchant savedMerchant = this.merchantService.save(merchant);
		
		if(savedMerchant != null) {
			logger.info("COMPLETED | Adding a new PaymentHub client to the BitCoin database | Email: " + merchant.getEmail());
			return ResponseEntity.status(201).build();
		}
		else {
			logger.error("CANCELED | Adding a new PaymentHub client to the BitCoin database | Email: " + merchant.getEmail());
			return ResponseEntity.status(400).build();
		}
	}

	@GetMapping("/fields")
	public ResponseEntity<?> getFields() {
		
		logger.info("INITIATED | Getting fields for registration dynamic form");


		List<FieldDTO> fields = this.merchantService.getFields();
		
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
