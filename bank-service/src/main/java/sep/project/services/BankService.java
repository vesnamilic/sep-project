package sep.project.services;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.DTOs.BankRequestDTO;
import sep.project.DTOs.CompletedDTO;
import sep.project.DTOs.BankResponseDTO;
import sep.project.DTOs.PayRequestDTO;
import sep.project.DTOs.RedirectDTO;
import sep.project.DTOs.RegisterSellerDTO;
import sep.project.DTOs.UrlDTO;
import sep.project.enums.Status;
import sep.project.model.Seller;
import sep.project.model.Transaction;
import sep.project.repository.SellerRepository;
import sep.project.repository.TransactionRepository;

@Service
public class BankService {

	@Autowired
	private SellerRepository sellerRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	private static String firstRequestURL;

	@Value("${firstRequestURL}")
	public void setFirstRequestURL(String s) {
		firstRequestURL = s;
	}

	private static String checkTransactionURL;

	@Value("${checkTransactionURL}")
	public void setCheckTransactionURL(String s) {
		checkTransactionURL = s;
	}

	private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

	public ResponseEntity<String> initiatePayment(PayRequestDTO requestDTO) {

		Transaction t = createTransaction(requestDTO);
		if (t == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		transactionRepository.save(t);
		logger.info("COMPLETED | Saved transaction with id: " + t.getId());

		BankRequestDTO bankRequest = createBankRequest(requestDTO, t.getMerchantOrderId());

		if (bankRequest == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<BankResponseDTO> responseDTO = template.postForEntity(firstRequestURL, bankRequest,
					BankResponseDTO.class);
			logger.info("INFO | Bank return value");
			t.setPaymentID(responseDTO.getBody().getPaymentID());
			transactionRepository.save(t);
			return ResponseEntity.ok(responseDTO.getBody().getPaymentURL() + responseDTO.getBody().getPaymentID());

		} catch (Exception e) {
			logger.error("ERROR | Bank is not available");
			t.setStatus(Status.UNSUCCESSFULLY);
			transactionRepository.save(t);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	public BankRequestDTO createBankRequest(PayRequestDTO requestDTO, Long orderId) {

		Seller seller = sellerRepository.findByEmail(requestDTO.getEmail());
		if (seller == null) {
			logger.error("ERROR | Bank request can not be made, seller does not exists");
			return null;
		}
		BankRequestDTO bankRequest = new BankRequestDTO();
		bankRequest.setAmount(requestDTO.getPaymentAmount());
		bankRequest.setMerchantID(seller.getMerchantID());
		bankRequest.setMerchantPass(seller.getMerchantPassword());
		bankRequest.setMerchantTimestamp(new Date());
		bankRequest.setMerchantOrderID(orderId);
	
		logger.info("COMPLETED | Bank request created");
		return bankRequest;

	}

	public Transaction createTransaction(PayRequestDTO requestDTO) {

		Seller seller = sellerRepository.findByEmail(requestDTO.getEmail());
		if (seller == null) {
			logger.error("ERROR | Transaction can not be made, seller does not exists");
			return null;
		}

		Transaction transaction = new Transaction();
		transaction.setAmount(requestDTO.getPaymentAmount());
		transaction.setMerchantOrderId(requestDTO.getOrderId());
		transaction.setStatus(Status.CREATED);
		transaction.setSeller(seller);
		transaction.setSuccessURL(requestDTO.getSuccessUrl());
		transaction.setErrorURL(requestDTO.getErrorUrl());
		transaction.setFailedURL(requestDTO.getFailedUrl());
		logger.info("COMPLETED | Transaction with id: " + transaction.getId() + " created");
		return transaction;
	}

	public Boolean registerSeller(RegisterSellerDTO registerSellerDTO) {

		Seller seller = new Seller();
		seller.setMerchantID(registerSellerDTO.getMerchantID());
		seller.setMerchantPassword(registerSellerDTO.getMerchantPassword());
		seller.setEmail(registerSellerDTO.getEmail());

		Seller savedSeller = sellerRepository.save(seller);

		if (savedSeller != null) {
			logger.info("COMPLETED | Seller with email: " + savedSeller.getEmail()
					+ " is successfuly added to card payment system");
			return true;
		} else {
			logger.error("ERROR | Seller can not be added to card payment system, email is null");
			return false;
		}
	}

	public ResponseEntity<?> finishPayment(CompletedDTO completedDTO) {

		logger.info("INFO | finishPayment is called");

		Transaction t = transactionRepository.findByPaymentID(completedDTO.getPaymentID());
		t.setAcquirerOrderId(completedDTO.getAcquirerOrderID());
		t.setAcquirerTimestamp(completedDTO.getAcquirerTimestamp());
		t.setStatus(completedDTO.getTransactionStatus());
		transactionRepository.save(t);

		logger.info("COMPLETED | Payment with id: " + completedDTO.getPaymentID() + " completed");

		String url = null;
		if (completedDTO.getTransactionStatus() == Status.SUCCESSFULLY)
			url = updateSeller(t.getSuccessURL());
		if (completedDTO.getTransactionStatus() == Status.ERROR)
			url = updateSeller(t.getErrorURL());
		if (completedDTO.getTransactionStatus() == Status.EXPIRED
				|| completedDTO.getTransactionStatus() == Status.UNSUCCESSFULLY)
			url = updateSeller(t.getFailedURL());
		
		logger.error("ERROR | Error in constacting Scientific center");

		if (url == null) {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(new UrlDTO(url));
	}

	private String updateSeller(String url) {
		RestTemplate restTemplate = new RestTemplate();
		String redirectUrl = null;
		try {
			ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.GET, null, RedirectDTO.class);
			RedirectDTO redirect = (RedirectDTO) response.getBody();
			redirectUrl = redirect.getUrl();
		} catch (RestClientException e) {
			return null;
		}
		return redirectUrl;
	}

	/**
	 * Contact bank to check status of created transactions
	 */
	@Scheduled(initialDelay = 1800000, fixedRate = 1800000)
	public void checkCreatedTransactions() {
		List<Transaction> transactions = transactionRepository.findAllByStatus(Status.CREATED);
		for (Transaction t : transactions) {
			String url = checkTransactionURL;

			RestTemplate template = new RestTemplate();
			try {
				String paymentId = t.getPaymentID();
				ResponseEntity<Status> response = template.postForEntity(url, paymentId, Status.class);
				
				if (response.getBody() == Status.SUCCESSFULLY || response.getBody() == Status.UNSUCCESSFULLY
						|| response.getBody() == Status.CANCELED || response.getBody() == Status.EXPIRED || response.getBody() == Status.ERROR) {
					t.setStatus(response.getBody());
					transactionRepository.save(t);
				}
				
				if (response.getBody() == null) {
					t.setStatus(Status.CANCELED);
					transactionRepository.save(t);
				}
				
			} catch (Exception e) {
				logger.error("ERROR| Transaction does not exists in buyer bank");
			}
		}
	}

}