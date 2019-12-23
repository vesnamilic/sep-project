package sep.project.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sep.project.DTOs.BankRequestDTO;
import sep.project.DTOs.BankResponseDTO;
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

	public ResponseEntity<BankResponseDTO> initiatePayment(PayRequestDTO requestDTO) {

		Transaction t = createTransaction(requestDTO);
		if(t==null) {
			return new ResponseEntity<BankResponseDTO>(HttpStatus.BAD_REQUEST);
		}
		Transaction savedTransaction=transactionRepository.save(t);
		savedTransaction.setMerchantOrderId(savedTransaction.getId());
		transactionRepository.save(savedTransaction);
		
		BankRequestDTO bankRequest = createBankRequest(requestDTO, savedTransaction.getMerchantOrderId());

		if(bankRequest==null) {
			return new ResponseEntity<BankResponseDTO>(HttpStatus.BAD_REQUEST);
		}
		
		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<BankResponseDTO> responseDTO = template
					.postForEntity("https://localhost:8081/api/firstRequest", bankRequest, BankResponseDTO.class);
			if (responseDTO != null) {
				return responseDTO;
			} else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Bank is not available!");
			t.setStatus(Status.UNSECCESSFULY_BANK);
			transactionRepository.save(t);
			return null;
		}

	}

	public BankRequestDTO createBankRequest(PayRequestDTO requestDTO, Long orderId) {

		Seller seller = sellerRepository.findByEmail(requestDTO.getEmail());
		if(seller==null) {
			return null;
		}
		BankRequestDTO bankRequest = new BankRequestDTO();
		bankRequest.setAmount(requestDTO.getPriceAmount());
		bankRequest.setMerchantID(seller.getMerchantID());
		bankRequest.setMerchantPass(seller.getMerchantPassword());
		bankRequest.setMerchantTimestamp(new Date());
		bankRequest.setMerchantOrderID(orderId);
		bankRequest.setErrorURL(errorURL);
		bankRequest.setSuccessURL(successURL);
		bankRequest.setFailedURL(failedURL);

		return bankRequest;

	}

	public Transaction createTransaction(PayRequestDTO requestDTO) {
		
		Seller seller = sellerRepository.findByEmail(requestDTO.getEmail());
		if(seller==null) {
			return null;
		}
		
		Transaction transaction = new Transaction();
		transaction.setAmount(requestDTO.getPriceAmount());
		transaction.setMerchantOrderId(transaction.getId());
		transaction.setStatus(Status.CREATED);
		transaction.setSeller(seller);
		return transaction;
	}

	public Boolean registerSeller(RegisterSellerDTO registerSellerDTO) {

		Seller seller = new Seller();
		seller.setMerchantID(registerSellerDTO.getMerchantID());
		seller.setMerchantPassword(registerSellerDTO.getMerchantPassword());
		seller.setSellerName(registerSellerDTO.getSellerName());
		seller.setEmail(registerSellerDTO.getEmail());
		
		Seller savedSeller = sellerRepository.save(seller);

		if (savedSeller != null) {
			return true;
		} else {
			return false;
		}
	}

	public ResponseEntity finishPayment(CompletedDTO completedDTO) {

		Transaction t = transactionRepository.findByMerchantOrderId(completedDTO.getMerchantOrderID());
		System.out.println("trenuntni status: " + t.getStatus());
		System.out.println(completedDTO.getTransactionStatus());
		t.setStatus(completedDTO.getTransactionStatus());
		t.setAcquirerOrderId(completedDTO.getAcquirerOrderID());
		t.setAcquirerTimestamp(completedDTO.getAcquirerTimestamp());
		t.setPaymentID(completedDTO.getPaymentID());
		transactionRepository.save(t);

		return new ResponseEntity<>(HttpStatus.OK);

	}

}