package sep.project.controllers;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.DTOs.BuyerDTO;
import sep.project.DTOs.KPRequestDTO;
import sep.project.DTOs.KPResponseDTO;
import sep.project.enums.Status;
import sep.project.model.PaymentInfo;
import sep.project.model.Transaction;
import sep.project.repository.PaymentInfoRepository;
import sep.project.services.AcquirerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcquierController {

	private static String BankFrontAddress;

	@Value("${BankFrontAddress}")
	public void setBank1URL(String bankAdress) {
		BankFrontAddress = bankAdress;
	}

	@Autowired
	AcquirerService acquirerService;

	@Autowired
	PaymentInfoRepository paymentInfoRepository;

	private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

	@PostMapping(value = "/firstRequest")
	public ResponseEntity<KPResponseDTO> initiatePayment(@RequestBody KPRequestDTO request) {
		logger.info("INFO | initiatePayment started");

		if (!acquirerService.validate(request)) {
			logger.error("ERROR | data is not valid");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		PaymentInfo info = acquirerService.createPaymentInfo(request);

		// if seller does not exists PaymentInfo is null
		if (info == null) {
			logger.error("ERROR | paymentInfo does not exists");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		KPResponseDTO response = new KPResponseDTO();
		response.setPaymentID(info.getPaymentId());
		response.setPaymentURL(BankFrontAddress + "/form/");
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/pay/{paymentId}")
	public ResponseEntity<?> postPaymentForm(HttpServletResponse httpServletResponse,
			@PathVariable String paymentId, @RequestBody BuyerDTO buyerDTO) {
		logger.info("INFO | postPaymentForm started");
		return acquirerService.tryPayment(paymentId, buyerDTO);
	}

	@PostMapping(value = "/checkTransaction")
	public Status checkTransaction(@RequestBody String request) {
		logger.info("INFO | retrunMonay is called");
		return acquirerService.checkTransaction(request);
	}

}