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
import sep.project.model.Merchant;
import sep.project.services.MerchantService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
public class MerchantController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private RestTemplate restTemplate;

	@PostMapping("")
	public ResponseEntity<?> addClient(@RequestHeader("Authorization") String authorization,
			@RequestBody String clientString) {

		logger.info("INITIATED | Adding a new PaymentHub client to the BitCoin database");

		String email = "";

		String url = "https://localhost:8762/api/client/seller/whoami";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authorization);

		HttpEntity<?> httpEntity = new HttpEntity<Object>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
			email = (String) response.getBody();
		} catch (HttpClientErrorException e) {
			logger.error("CANCELED | Adding a new PaymentHub client to the BitCoin database");
			return ResponseEntity.status(401).build();
		} catch (Exception e) {
			logger.error("CANCELED | Adding a new PaymentHub client to the BitCoin database");
			return ResponseEntity.status(401).build();
		}

		Gson gson = new Gson();
		Merchant merchant;

		try {
			merchant = gson.fromJson(clientString, Merchant.class);
		} catch (JsonSyntaxException e) {
			logger.error("CANCELED | Adding a new PaymentHub client to the BitCoin database");
			return ResponseEntity.status(400).build();
		}

		merchant.setEmail(email);

		// check if client with this email address already exists
		Merchant checkClient = merchantService.getMerchant(merchant.getEmail());
		if (checkClient != null) {
			logger.error("CANCELED | Adding a new PaymentHub client to the BitCoin database");
			return ResponseEntity.status(400).build();
		}

		Merchant newClient = merchantService.save(merchant);

		if (newClient != null) {
			logger.info("COMPLETED | Adding a new PaymentHub client to the BitCoin database");
			return ResponseEntity.status(200).build();
		} else {
			logger.error("CANCELED | Adding a new PaymentHub client to the BitCoin database");
			return ResponseEntity.status(400).build();
		}
	}

	@GetMapping("/fields")
	public ResponseEntity<?> getFields() {

		logger.info("INITIATED | Getting fields for registration dynamic form");

		List<FieldDTO> fields = this.merchantService.getFields();

		if (fields != null) {
			logger.info("COMPLETED | Getting fields for registration dynamic form");
			return new ResponseEntity<>(fields, HttpStatus.OK);
		} else {
			logger.error("CANCELED | Getting fields for registration dynamic form");
			return ResponseEntity.status(400).build();
		}
	}
}
