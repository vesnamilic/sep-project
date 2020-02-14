package sep.project.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.OrderStatusInformationDTO;
import sep.project.dto.PaymentDTO;
import sep.project.dto.RedirectDTO;
import sep.project.model.Client;
import sep.project.model.Transaction;
import sep.project.services.ClientService;
import sep.project.services.PaymentService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController {
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private PaymentService paymentService;
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	/**
	 * Create a new transaction
	 */
	@PostMapping(value = "/create")
	public ResponseEntity<?> createPayment(@RequestBody @Valid PaymentDTO paymentDTO) {
		
		logger.info("INITIATED | Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());

		//check if client exists
		Client client = clientService.findByEmail(paymentDTO.getEmail());
		if (client == null) {
			logger.error("CANCELED | Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Transaction transaction = paymentService.createPayment(paymentDTO, client);
		
		logger.info("COMPLETED | Payment | Amount: " + paymentDTO.getPaymentAmount() + " " + paymentDTO.getPaymentCurrency());
		
		//update seller and get redirect url
		String url = updateSeller(transaction.getSuccessUrl());

		// redirect to the success page
		HttpHeaders headersRedirect = new HttpHeaders();
		headersRedirect.add("Location", url);
		headersRedirect.add("Access-Control-Allow-Origin", "*");
		return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);		
	}
	
	@GetMapping("/payment")
	public ResponseEntity<?> getPaymentInfo(@RequestParam("orderId") Long id, @RequestParam("email") String email) {
		
		OrderStatusInformationDTO status = new OrderStatusInformationDTO();
		status.setStatus("COMPLETED");
		
		return ResponseEntity.ok(status);
	}

	private String updateSeller(String url){
		
		RestTemplate restTemplate = new RestTemplate();
		
		String redirectUrl = null;
		
		try {
	    	ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.GET, null, RedirectDTO.class);
	       
	    	RedirectDTO redirect = (RedirectDTO) response.getBody();
	    	redirectUrl = redirect.getUrl();
	    } 
		catch (RestClientException e) {
			//TODO: what happens here?
		}
		
		return redirectUrl;
	}
}
