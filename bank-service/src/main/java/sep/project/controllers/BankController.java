package sep.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sep.project.DTOs.CompletedDTO;
import sep.project.DTOs.OrderStatusInformationDTO;
import sep.project.DTOs.PayRequestDTO;
import sep.project.enums.Status;
import sep.project.model.Transaction;
import sep.project.services.BankService;
import sep.project.services.TransactionService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class BankController {

	@Autowired
	private BankService bankService;

	@Autowired
	private TransactionService transactionService;
	
	private static final Logger logger = LoggerFactory.getLogger(Transaction.class);
	
	@PostMapping(value = "/create")
	public ResponseEntity<String> initiatePayment(@RequestBody PayRequestDTO request) {
		return bankService.initiatePayment(request);
	}

	@PostMapping(value = "/finishPayment")
	public ResponseEntity<?> finishPayment(@RequestBody CompletedDTO completedDTO) {
		return bankService.finishPayment(completedDTO);
	}
	
	@GetMapping("/payment")
	public ResponseEntity<?> getPaymentInfo(@RequestParam("orderId") Long id, @RequestParam("email") String email) {
		logger.info("INFO | getPaymentInfo is called");

		Transaction transaction = this.transactionService.findMerchantTransactionBasedOnId(id, email);
		if (transaction != null) {
			OrderStatusInformationDTO status = new OrderStatusInformationDTO();
			if (transaction.getStatus() == Status.CREATED) {
				logger.info("INFO | status is CREATED");
				status.setStatus("CREATED");
			} else if (transaction.getStatus() == Status.SUCCESSFULLY) {
				logger.info("INFO | status is COMPLETED");
				status.setStatus("COMPLETED");
			} else if (transaction.getStatus() == Status.UNSUCCESSFULLY || transaction.getStatus()== Status.EXPIRED || transaction.getStatus()== Status.ERROR) {
				logger.info("INFO | status is INVALID");
				status.setStatus("INVALID");
			} else {
				logger.info("INFO | status is CANCELED");
				status.setStatus("CANCELED");
			}
			return ResponseEntity.ok(status);
		}
		return ResponseEntity.notFound().build();
	}

}
