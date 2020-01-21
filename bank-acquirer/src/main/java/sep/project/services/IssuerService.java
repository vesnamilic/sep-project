package sep.project.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sep.project.DTOs.PCCRequestDTO;
import sep.project.DTOs.PCCResponseDTO;
import sep.project.enums.Status;
import sep.project.model.Card;
import sep.project.model.CardOwner;
import sep.project.model.Transaction;
import sep.project.repository.CardOwnerRepository;
import sep.project.repository.CardRepository;
import sep.project.repository.TransactionRepository;

@Service
public class IssuerService {

	@Autowired
	CardOwnerRepository cardOwnerRepository;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	CardRepository cardRepository;

	public ResponseEntity<PCCResponseDTO> checkPayment(PCCRequestDTO request) {

		CardOwner cardOwner = cardOwnerRepository.findByCardPan(request.getBuyerPan());
		Transaction t = createTransaction(request, cardOwner);

		if (cardOwner != null) {
			t.setIssuerOrderId(t.getId());
			if (checkCredentials(request, cardOwner)) {
				return processPayment(request, t, cardOwner);
			} else {
				System.out.println("Buyer credentials is not valid!");
				save(t, Status.UNSUCCESSFULLY);
				PCCResponseDTO pccResponseDTO = new PCCResponseDTO();
				pccResponseDTO.setAcquirerOrderID(request.getAcquirerOrderID());
				pccResponseDTO.setMerchantOrderID(request.getMerchantOrderID());
				pccResponseDTO.setStatus(Status.UNSUCCESSFULLY);
				return new ResponseEntity<PCCResponseDTO>(pccResponseDTO, HttpStatus.OK);
			}
		} else {
			System.out.println("Buyer does not have account in this bank!");	
			save(t, Status.UNSUCCESSFULLY);
			PCCResponseDTO pccResponseDTO = new PCCResponseDTO();
			pccResponseDTO.setAcquirerOrderID(request.getAcquirerOrderID());
			pccResponseDTO.setMerchantOrderID(request.getMerchantOrderID());
			pccResponseDTO.setStatus(Status.UNSUCCESSFULLY);
			return new ResponseEntity<PCCResponseDTO>(pccResponseDTO, HttpStatus.OK);
		}
	}

	public Transaction createTransaction(PCCRequestDTO request, CardOwner cardOwner) {

		Card card = cardRepository.findByPan(request.getBuyerPan());

		Transaction t = new Transaction();
		if (cardOwner != null) {
			t.setBuyer(cardOwner);
		}
		if (card != null) {
			t.setBuyerPan(card.getPan());
		}

		t.setSellerPan(request.getSellerPan());
		t.setIssuerTimestamp(new Date());
		t.setStatus(Status.CREATED);
		t.setTimestamp(new Date());
		t.setSellerPan(request.getSellerPan());
		t.setAmount(request.getAmount());
		t.setMerchantOrderId(request.getMerchantOrderID());
		t.setMerchantTimestamp(request.getMerchantTimestamp());

		transactionRepository.save(t);
		return t;

	}

	public boolean checkCredentials(PCCRequestDTO request, CardOwner buyer) {

		if (buyer == null) {
			return false;
		}

		if (!buyer.getName().toLowerCase().equals(request.getName().toLowerCase().trim())) {
			return false;
		}

		if (!buyer.getLastName().toLowerCase().equals(request.getLastName().toLowerCase().trim())) {
			return false;
		}

		if (!buyer.getCard().getCvv().equals(request.getCvv().trim())) {
			return false;
		}

		// Date is in format MM/YY
		String expDate = request.getMonth() + "/" + request.getYear();
		if (!buyer.getCard().getExpDate().equals(expDate)) {
			return false;
		}

		// check is card expiered
		int pos = expDate.lastIndexOf("/");
		int year = Integer.valueOf(expDate.substring(pos + 1));
		int month = Integer.valueOf(expDate.substring(0, pos));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowString = dateFormat.format(new Date(System.currentTimeMillis()));
		Date now = null;
		try {
			now = dateFormat.parse(dateNowString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Date expiresDate = null;
		try {
			// function from:
			// https://stackoverflow.com/questions/8940438/number-of-days-in-particular-month-of-particular-year
			YearMonth yearMonthObject = YearMonth.of(year, month);
			int daysInMonth = yearMonthObject.lengthOfMonth(); // 28
			expiresDate = dateFormat.parse("20" + year + "-" + month + "-" + daysInMonth);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (expiresDate.compareTo(now) < 0) {
			return false;
		}

		return true;

	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public ResponseEntity<PCCResponseDTO> processPayment(PCCRequestDTO request, Transaction t, CardOwner cardOwner) {

		PCCResponseDTO pccResponseDTO = new PCCResponseDTO();
		pccResponseDTO.setAcquirerOrderID(request.getAcquirerOrderID());
		pccResponseDTO.setMerchantOrderID(request.getMerchantOrderID());
		Card card = cardRepository.findByPan(t.getBuyerPan());

		if (card == null) {
			System.out.println("Transakcija neuspesna, nije pronadjena kartica.");
			t.setStatus(Status.UNSUCCESSFULLY);
			transactionRepository.save(t);
			pccResponseDTO.setStatus(Status.UNSUCCESSFULLY);
			return new ResponseEntity<PCCResponseDTO>(pccResponseDTO, HttpStatus.OK);
		}

		Float available = card.getAvailableFunds();

		if (available - t.getAmount() < 0) {
			System.out.println("Transakcija neuspesna, nedovoljno sredstava.");
			t.setStatus(Status.UNSUCCESSFULLY);
			transactionRepository.save(t);
			pccResponseDTO.setStatus(Status.UNSUCCESSFULLY);
			return new ResponseEntity<PCCResponseDTO>(pccResponseDTO, HttpStatus.OK);
		}

		card.setAvailableFunds(available - t.getAmount());
		cardRepository.save(card);

		cardOwner.setCard(card);
		cardOwnerRepository.save(cardOwner);

		t.setStatus(Status.SUCCESSFULLY);
		transactionRepository.save(t);

		pccResponseDTO.setIssuerOrderID(t.getId());
		pccResponseDTO.setIssuerTimestamp(t.getTimestamp());
		pccResponseDTO.setStatus(Status.SUCCESSFULLY);
		System.out.println("Transakcija je uspesna.");
		return new ResponseEntity<PCCResponseDTO>(pccResponseDTO, HttpStatus.OK);
		
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public void save(Transaction t, Status s) {
		t.setStatus(s);
		transactionRepository.save(t);
	}

}
