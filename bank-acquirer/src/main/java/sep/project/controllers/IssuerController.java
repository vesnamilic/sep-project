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

import sep.project.DTOs.PCCRequestDTO;
import sep.project.DTOs.PCCResponseDTO;
import sep.project.model.Transaction;
import sep.project.services.IssuerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/acquirer", produces = MediaType.APPLICATION_JSON_VALUE)
public class IssuerController {

	@Autowired
    private IssuerService issuerService;

	private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

	@PostMapping(value = "/paymentRequest")
	public ResponseEntity<PCCResponseDTO> paymentRequest(@RequestBody PCCRequestDTO request) {
		logger.info("INFO | paymentRequest is called");
		return issuerService.checkPayment(request);
	}

}
