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

import com.google.gson.Gson;

import sep.project.DTOs.FieldDTO;
import sep.project.model.Seller;
import sep.project.services.SellerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerController {

	@Autowired
	SellerService sellerService;
	
	private static final Logger logger = LoggerFactory.getLogger(SellerController.class);

	@PostMapping("")
	public ResponseEntity<?> addSeller(@RequestBody String clientString) {

		logger.info("INITIATED | Adding a new PaymentHub seller to the Bank database");

		Gson gson = new Gson();
		Seller seller = gson.fromJson(clientString, Seller.class);

		// check if client with this email address already exists
		Seller checkClient = sellerService.getSeller(seller.getEmail());
		if (checkClient != null) {
			logger.error("CANCELED | Adding a new PaymentHub client to the PayPal database");
			return ResponseEntity.status(400).build();
		}

		Seller newSeller = sellerService.save(seller);

		if (newSeller != null) {
			logger.info(
					"COMPLETED | Adding a new PaymentHub seller to the Bank database | Email: " + seller.getEmail());
			return ResponseEntity.status(201).build();
		} else {
			logger.error(
					"CANCELED | Adding a new PaymentHub client to the Bank database | Email: " + seller.getEmail());
			return ResponseEntity.status(400).build();
		}

	}

	/**
	 * Getting fields for registration dynamic form
	 */
	@GetMapping("fields")
	public ResponseEntity<?> getFields() {

		logger.info("INITIATED | Getting fields for registration dynamic form");

		List<FieldDTO> fields = sellerService.getFields();

		if (fields != null) {
			logger.info("COMPLETED | Getting fields for registration dynamic form");
			return new ResponseEntity<>(fields, HttpStatus.OK);
		} else {
			logger.error("COMPLETED | Getting fields for registration dynamic form");
			return ResponseEntity.status(400).build();
		}
	}

}
