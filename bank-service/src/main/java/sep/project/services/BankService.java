package sep.project.services;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sep.project.DTOs.BankRequestDTO;
import sep.project.DTOs.CompletedDTO;
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
	SellerRepository sellerRepository;

	@Autowired
	TransactionRepository transactionRepository;

	private static String errorURL;

	@Value("${errorURL}")
	public void setErrorURL(String s) {
		errorURL = s;
	}

	private static String successURL;

	@Value("${successURL}")
	public void setSuccessURL(String s) {
		successURL = s;
	}

	private static String failedURL;

	@Value("${failedURL}")
	public void setFailedURL(String s) {
		failedURL = s;
	}

	private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

	public ResponseEntity<String> initiatePayment(PayRequestDTO requestDTO) {

		Transaction t = createTransaction(requestDTO);
		if(t==null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Transaction savedTransaction=transactionRepository.save(t);
		savedTransaction.setMerchantOrderId(savedTransaction.getId());
		transactionRepository.save(savedTransaction);
		logger.info("COMPLETED | Saved transaction with id: "+savedTransaction.getId());
		   
		BankRequestDTO bankRequest = createBankRequest(requestDTO, savedTransaction.getMerchantOrderId());

		if(bankRequest==null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<String> responseDTO = template
					.postForEntity("https://localhost:8081/firstRequest", bankRequest, String.class);
			if (responseDTO.getStatusCode()==HttpStatus.OK) {
				logger.info("INFO | Bank return value");
				return ResponseEntity.ok(responseDTO.getBody());
			} else {
				logger.error("ERROR | Error in bank");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			logger.error("ERROR | Bank is not available");
			t.setStatus(Status.UNSECCESSFULY_BANK);
			transactionRepository.save(t);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	public BankRequestDTO createBankRequest(PayRequestDTO requestDTO, Long orderId) {

		Seller seller = sellerRepository.findByEmail(requestDTO.getEmail());
		if(seller==null) {
			logger.error("ERROR | Bank request can not be made, seller does not exists");
			return null;
		}
		BankRequestDTO bankRequest = new BankRequestDTO();
		bankRequest.setAmount(requestDTO.getPaymentAmount());
		bankRequest.setMerchantID(seller.getMerchantID());
		bankRequest.setMerchantPass(seller.getMerchantPassword());
		bankRequest.setMerchantTimestamp(new Date());
		bankRequest.setMerchantOrderID(orderId);
		bankRequest.setErrorURL(errorURL);
		bankRequest.setSuccessURL(successURL);
		bankRequest.setFailedURL(failedURL);
		
		logger.info("COMPLETED | Bank request created");
		return bankRequest;

	}

	public Transaction createTransaction(PayRequestDTO requestDTO) {
		
		Seller seller = sellerRepository.findByEmail(requestDTO.getEmail());
		if(seller==null) {
			logger.error("ERROR | Transaction can not be made, seller does not exists");
			return null;
		}
		
		Transaction transaction = new Transaction();
		transaction.setAmount(requestDTO.getPaymentAmount());
		transaction.setMerchantOrderId(transaction.getId());
		transaction.setStatus(Status.CREATED);
		transaction.setSeller(seller);
		logger.info("COMPLETED | Transaction with id: "+transaction.getId()+" created");
		return transaction;
	}

	public Boolean registerSeller(RegisterSellerDTO registerSellerDTO) {

		Seller seller = new Seller();
		seller.setMerchantID(registerSellerDTO.getMerchantID());
		seller.setMerchantPassword(registerSellerDTO.getMerchantPassword());
		seller.setEmail(registerSellerDTO.getEmail());
		
		Seller savedSeller = sellerRepository.save(seller);

		if (savedSeller != null) {
			logger.info("COMPLETED | Seller with email: "+savedSeller.getEmail()+" is successfuly added to card payment system");
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
		t.setPaymentID(completedDTO.getPaymentID());

		transactionRepository.save(t);

		logger.info("COMPLETED | Payment with id: "+completedDTO.getPaymentID()+" completed");

		return ResponseEntity.status(200).body(true);

	}

}