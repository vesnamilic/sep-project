package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sep.project.DTOs.BankRequestDTO;
import sep.project.DTOs.BankResponseDTO;
import sep.project.DTOs.CompletedDTO;
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

	public ResponseEntity<BankResponseDTO> initiatePayment(BankRequestDTO requestDTO) {

		Transaction t = createTransaction(requestDTO);
		transactionRepository.save(t);

		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<BankResponseDTO> responseDTO = template
					.postForEntity("https://localhost:8081/api/firstRequest", requestDTO, BankResponseDTO.class);
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

	public Transaction createTransaction(BankRequestDTO requestDTO) {
		Transaction transaction = new Transaction();
		transaction.setAmount(requestDTO.getAmount());
		transaction.setMerchantOrderId(requestDTO.getMerchantOrderID());
		transaction.setStatus(Status.CREATED);
		return transaction;
	}

	public Boolean registerSeller(RegisterSellerDTO registerSellerDTO) {

		Seller seller = new Seller();
		seller.setMerchantID(registerSellerDTO.getMerchantID());
		seller.setMerchantPassword(registerSellerDTO.getMerchantPassword());
		seller.setSellerName(registerSellerDTO.getSellerName());

		Seller savedSeller = sellerRepository.save(seller);

		if (savedSeller != null) {
			return true;
		} else {
			return false;
		}
	}

	public ResponseEntity finishPayment(CompletedDTO completedDTO) {

		Transaction t = transactionRepository.findByMerchantOrderId(completedDTO.getMerchantOrderID());
		System.out.println("trenuntni status: "+ t.getStatus());
		System.out.println(completedDTO.getTransactionStatus());
		t.setStatus(completedDTO.getTransactionStatus());
		t.setAcquirerOrderId(completedDTO.getAcquirerOrderID());
		t.setAcquirerTimestamp(completedDTO.getAcquirerTimestamp());
		t.setPaymentID(completedDTO.getPaymentID());
		transactionRepository.save(t);
		
		return new ResponseEntity<>(HttpStatus.OK);

	}

}