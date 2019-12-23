package sep.project.controllers;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.BitCoinPayment;
import sep.project.dto.CallBackDTO;
import sep.project.dto.PaymentRequestDTO;
import sep.project.dto.PaymentResponseDTO;
import sep.project.model.Merchant;
import sep.project.model.Transaction;
import sep.project.services.MerchantService;
import sep.project.services.TransactionService;

@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentController {
	
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
	 * @param paymentInfo informacije o placanju
	 * @return 
	 * @see BitCoinPayment
	 */
	@PostMapping("/")
	public ResponseEntity<?> createPayment(@RequestBody BitCoinPayment paymentInfo) {
		Merchant merchant = this.merchantService.getMerchant(paymentInfo.getEmail());
		
		if(merchant == null) {
			return ResponseEntity.badRequest().build();
		}
		
		PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO(paymentInfo.getPaymentAmount(), paymentInfo.getPaymentCurrency() ,"BTC");
		paymentRequestDTO.setToken(merchant.getUserToken());
		paymentRequestDTO.setCallback_url(this.callbackURL);
		paymentRequestDTO.setCancel_url(this.cancelURL);
		paymentRequestDTO.setSuccess_url(this.successURL);
		HttpHeaders headers = new HttpHeaders();
		headers.add(this.AUTH_HEADER, this.TOKEN_TYPE + " " + merchant.getUserToken());
		HttpEntity<PaymentRequestDTO> request = new HttpEntity<>(paymentRequestDTO, headers);
		
		Transaction transaction = this.transactionService.createInitialTransaction(merchant, paymentInfo.getPaymentAmount(), paymentInfo.getPaymentCurrency());
		
		// TODO: Dodati opis greske
		if(transaction == null) {
			return ResponseEntity.badRequest().build();
		}
		
		ResponseEntity<PaymentResponseDTO> response = null;

		try {
			response =  restTemplate.exchange(sandBoxURL, HttpMethod.POST, request, PaymentResponseDTO.class);
		} catch (Exception e) {
	      this.transactionService.changeTransactionStatus(transaction.getId(), "invalid");
	      return ResponseEntity.badRequest().build();
	    }
		
		if(response.getStatusCode() != HttpStatus.OK) {
			return ResponseEntity.status(response.getStatusCode()).build();
		}
		
		PaymentResponseDTO responseObject = response.getBody();
		System.out.println(responseObject);
		transaction.setCreationDate(responseObject.getCreated_at());
		transaction.setPaymentId(responseObject.getId());
		transaction.setStatus(responseObject.getStatus());
		transaction.setReceiveAmount(0.0);
		transaction.setReceiveCurrency("");
		transaction = this.transactionService.saveTransaction(transaction);
		
		if(transaction == null) {
			 return ResponseEntity.status(500).body("Error while trying to save payment");
		}
		
		// TODO: Otkomentarisaati prilikom pokusaja sa fronta
		/*HttpHeaders headersRedirect = new HttpHeaders();
		headers.add("Location", response.getBody().getPayment_url());

		return new ResponseEntity<byte []>(null,headersRedirect,HttpStatus.FOUND);*/
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/cancel")
	public ResponseEntity<?> cancelPayment() {

		return null;
	}
	
	@GetMapping("/success")
	public ResponseEntity<?> successfulPayment() {

		return null;
	}
	
	@PostMapping("/callback")
	public ResponseEntity<?> paymentStatusChanged(@RequestBody CallBackDTO callback) {

		return ResponseEntity.ok("");
	}
	
	// TODO: Ako bude trebalo
	/* 
	public ResponseEntity<?> checkPaymentDetails() {
		return null;
	}
	
	public ResponseEntity<?> getPayment() {
		return null;
	}
	
	public ResponseEntity<?> getPaymentList() {
		return null;
	}
	*/
}
