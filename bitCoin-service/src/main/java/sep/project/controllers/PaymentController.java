package sep.project.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.BitCoinPayment;
import sep.project.dto.PaymentRequestDTO;
import sep.project.dto.PaymentResponseDTO;
import sep.project.model.Merchant;
import sep.project.model.Transaction;
import sep.project.services.MerchantService;
import sep.project.services.TransactionService;

@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private TransactionService transactionService;

	@Value("${sandbox_url}")
	private String sandBoxURL;

	@Value("${success_url}")
	private String successURL;

	@Value("${cancel_url}")
	private String cancelURL;

	@Value("${callback_url}")
	private String callbackURL;

	@Value("Authorization")
	private String AUTH_HEADER;

	@Value("Bearer")
	private String TOKEN_TYPE;

	/**
	 * Metoda za kreiranje placanja i slanje podataka o placanju na CoinGate
	 * 
	 * @param paymentInfo informacije o placanju
	 * @return
	 * @see BitCoinPayment
	 */
	@PostMapping("/create")
	public ResponseEntity<?> createPayment(@RequestBody BitCoinPayment paymentInfo) {

		logger.info("INITIATED | Creating payment | Merchant's email: " + paymentInfo.getEmail() + " , Amount: "
				+ paymentInfo.getPaymentAmount());

		Merchant merchant = this.merchantService.getMerchant(paymentInfo.getEmail());

		if (merchant == null) {
			logger.error("CANCELED | Finding a merchant based on the given email address | Merchant's email: "
					+ paymentInfo.getEmail());
			return ResponseEntity.badRequest().build();
		}
		// Kreiranje transakcije na osnovu narudzbine
		Transaction transaction = this.transactionService.createInitialTransaction(merchant, paymentInfo);

		// TODO: Dodati opis greske
		if (transaction == null) {
			logger.error("CANCELED | Saving initial transaction for the order | Merchant's email: "
					+ paymentInfo.getEmail());
			return ResponseEntity.badRequest().build();
		}

		PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO(transaction.getId().toString(),
				paymentInfo.getPaymentAmount(), paymentInfo.getPaymentCurrency(), "BTC", this.callbackURL,
				this.cancelURL + "?id=" + transaction.getId(), this.successURL + "?id=" + transaction.getId(),
				merchant.getToken());

		// Dodavanje Authorization headera na osnovu tokena prodavca
		HttpHeaders headers = new HttpHeaders();
		headers.add(this.AUTH_HEADER, this.TOKEN_TYPE + " " + merchant.getToken());
		HttpEntity<PaymentRequestDTO> request = new HttpEntity<>(paymentRequestDTO, headers);

		ResponseEntity<PaymentResponseDTO> response = null;
		try {
			response = restTemplate.exchange(sandBoxURL, HttpMethod.POST, request, PaymentResponseDTO.class);
		} catch (Exception e) {
			logger.error("CANCELED | Contacting the bitcoin service | Service url: " + sandBoxURL);
			this.transactionService.changeTransactionStatus(transaction.getId(), "invalid");
			return ResponseEntity.badRequest().build();
		}

		PaymentResponseDTO responseObject = response.getBody();
		transaction = this.transactionService.changeTransaction(transaction, responseObject);

		if (transaction == null) {
			logger.error(
					"CANCELED | Saving payment transaction for the payment | Payment id: " + responseObject.getId());
			// return ResponseEntity.status(500).body("Error while trying to save payment");
			return ResponseEntity.badRequest().build();

		}

		logger.info("COMPLETED | Creating payment | Merchant's email: " + paymentInfo.getEmail() + " , Amount: "
				+ paymentInfo.getPaymentAmount());
		return ResponseEntity.ok(response.getBody().getPayment_url());

	}

	@GetMapping("/cancel")
	public ResponseEntity<?> cancelPayment(@RequestParam Long id) {
		Transaction transaction = this.transactionService.getTransaction(id);
		if (transaction == null) {
			return ResponseEntity.status(400).build();
		}

		HttpHeaders headersRedirect = new HttpHeaders();
		headersRedirect.add("Access-Control-Allow-Origin", "*");

		if (this.transactionService.checkTransaction(transaction)) {
			headersRedirect.add("Location", transaction.getFailedUrl());
		} else {
			headersRedirect.add("Location", transaction.getErrorUrl());
		}

		return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);

	}

	@GetMapping("/success")
	public ResponseEntity<?> successfulPayment(@RequestParam Long id) {
		Transaction transaction = this.transactionService.getTransaction(id);
		if (transaction == null) {
			return ResponseEntity.status(400).build();
		}
		HttpHeaders headersRedirect = new HttpHeaders();
		headersRedirect.add("Access-Control-Allow-Origin", "*");

		if (this.transactionService.checkTransaction(transaction)) {
			headersRedirect.add("Location", transaction.getSuccessUrl());
		} else {
			headersRedirect.add("Location", transaction.getErrorUrl());
		}

		return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);

	}

	// TODO: Ako bude trebalo

	/*
	 * @PostMapping("/callback") public ResponseEntity<?>
	 * paymentStatusChanged(@RequestBody CallBackDTO callback) { Transaction
	 * transaction =
	 * this.transactionService.getTransactionByPayment(callback.getId()); if
	 * (transaction == null) { return ResponseEntity.status(400).build(); }
	 * 
	 * if (this.transactionService.checkTransaction(transaction)) { return
	 * ResponseEntity.ok().build(); } else { return
	 * ResponseEntity.status(400).build(); }
	 * 
	 * }
	 * 
	 * public ResponseEntity<?> checkPaymentDetails() { return null; }
	 * 
	 * public ResponseEntity<?> getPayment() { return null; }
	 * 
	 * public ResponseEntity<?> getPaymentList() { return null; }
	 */

}
