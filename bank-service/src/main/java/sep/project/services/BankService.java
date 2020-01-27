package sep.project.services;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sep.project.DTOs.BankRequestDTO;
import sep.project.DTOs.CompletedDTO;
import sep.project.DTOs.KPResponseDTO;
import sep.project.DTOs.PayRequestDTO;
import sep.project.DTOs.RegisterSellerDTO;
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
		Transaction savedTransaction = transactionRepository.save(t);
		savedTransaction.setMerchantOrderId(savedTransaction.getId());
		transactionRepository.save(savedTransaction);
		logger.info("COMPLETED | Saved transaction with id: " + savedTransaction.getId());

		BankRequestDTO bankRequest = createBankRequest(requestDTO, savedTransaction.getMerchantOrderId());

		if (bankRequest == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<KPResponseDTO> responseDTO = template.postForEntity(firstRequestURL, bankRequest,
					KPResponseDTO.class);
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
		bankRequest.setErrorURL(requestDTO.getErrorUrl());
		bankRequest.setSuccessURL(requestDTO.getSuccessUrl());
		bankRequest.setFailedURL(requestDTO.getFailedUrl());

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
		transaction.setMerchantOrderId(transaction.getId());
		transaction.setStatus(Status.CREATED);
		transaction.setSeller(seller);
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

	public ResponseEntity finishPayment(CompletedDTO completedDTO) {

		System.out.println("FINISH USAO");

		Transaction t = transactionRepository.findByMerchantOrderId(completedDTO.getMerchantOrderID());
		t.setStatus(completedDTO.getTransactionStatus());
		t.setAcquirerOrderId(completedDTO.getAcquirerOrderID());
		t.setAcquirerTimestamp(completedDTO.getAcquirerTimestamp());

		transactionRepository.save(t);

		logger.info("COMPLETED | Payment with id: " + completedDTO.getPaymentID() + " completed");

		return ResponseEntity.status(200).body(true);

	}

	@Scheduled(initialDelay = 1800000, fixedRate = 1800000)
	public void checkCreatedTransactions() {
		List<Transaction> transactions = transactionRepository.findAllByStatus(Status.CREATED);
		for (Transaction t : transactions) {
			String url = checkTransactionURL;
			// sending request to return money
			RestTemplate template = new RestTemplate();
			try {
				String paymentId = t.getPaymentID();
				ResponseEntity<Status> response = template.postForEntity(url, paymentId, Status.class);
				if (response.getBody() == Status.SUCCESSFULLY || response.getBody() == Status.UNSUCCESSFULLY
						|| response.getBody() == Status.CANCELED || response.getBody() == Status.EXPIRED) {
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