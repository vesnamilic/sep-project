package sep.project.services;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.common.hash.Hashing;

import sep.project.DTOs.BuyerDTO;
import sep.project.DTOs.CompletedDTO;
import sep.project.DTOs.KPRequestDTO;
import sep.project.DTOs.PCCRequestDTO;
import sep.project.DTOs.PCCResponseDTO;
import sep.project.customExceptions.InvalidDataException;
import sep.project.customExceptions.NoEnoughFundException;
import sep.project.customExceptions.PaymentException;
import sep.project.enums.Status;
import sep.project.model.Card;
import sep.project.model.CardOwner;
import sep.project.model.PaymentInfo;
import sep.project.model.Transaction;
import sep.project.repository.CardOwnerRepository;
import sep.project.repository.CardRepository;
import sep.project.repository.PaymentInfoRepository;
import sep.project.repository.TransactionRepository;

@Service
public class AcquirerService {

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	PaymentInfoRepository paymentInfoRepository;

	@Autowired
	CardRepository cardRepository;

	@Autowired
	CardOwnerRepository cardOwnerRepository;

	private static String replyToKP;

	@Value("${replyToKP}")
	public void setBs(String s) {
		replyToKP = s;
	}

	private static String requestToPCC;

	@Value("${requestToPCC}")
	public void setss(String s) {
		requestToPCC = s;
	}

	private static String BankNumber;

	@Value("${BankNumber}")
	public void setBURL(String bankNumber) {
		BankNumber = bankNumber;
	}

//*****************************************For /firstRequest********************************************************//

	/**
	 * Function that validate first selling request
	 */
	public boolean validate(KPRequestDTO request) {

		System.out.println("DEBUG: validate called");
		
		CardOwner seller = cardOwnerRepository.findByMerchantID(request.getMerchantID());
		if (seller == null) {
			System.out.println("DEBUG: seller is null");

			return false;
		}

		if (request.getAmount() == null || request.getMerchantID() == null || request.getMerchantOrderID() == null
				|| request.getMerchantPass() == null || request.getMerchantTimestamp() == null) {
			System.out.println("DEBUG: not valid data");
			return false;
		}

		if (request.getAmount() <= 0) {
			System.out.println("DEBUG: amount<0");
			return false;
		}

		if (!seller.getMerchantPass().equals(request.getMerchantPass())) {
			System.out.println("bad password");
			return false;
		}
		return true;
	}

	public PaymentInfo createPaymentInfo(KPRequestDTO request) {
		System.out.println("DEBUG: creating payment info");

		Transaction t = createTransaction(request);
		if (t == null) {
			System.out.println("transaction does not exists");
			return null;
		}

		PaymentInfo paymentInfo = new PaymentInfo(createPaymentURLToken(request), t);

		t.setPaymentToken(paymentInfo.getPaymentToken());

		transactionRepository.save(t);
		paymentInfoRepository.save(paymentInfo);
		System.out.println("paymentInfo is returned");

		return paymentInfo;
	}

	/**
	 * Function that cretae transaction from KPRequestDTO
	 */
	public Transaction createTransaction(KPRequestDTO request) {
		System.out.println("creating transaction function");

		Transaction t = new Transaction();
		CardOwner seller = cardOwnerRepository.findByMerchantID(request.getMerchantID());
		if (seller == null) {
			System.out.println("DEBUG: create transaction, seller is null");
			return null;
		}
		t.setBuyer(null);
		t.setSeller(seller);
		t.setPaymentToken("");
		t.setTimestamp(new Date());
		t.setStatus(Status.CREATED);
		t.setSellerPan(seller.getCard().getPan());
		t.setBuyerPan(null);
		t.setAmount(request.getAmount());
		t.setSuccessURL(request.getSuccessURL());
		t.setFailedURL(request.getFailedURL());
		t.setErrorURL(request.getErrorURL());
		t.setMerchantOrderId(request.getMerchantOrderID());
		t.setMerchantTimestamp(request.getMerchantTimestamp());
		transactionRepository.save(t);
		return t;
	}

	/**
	 * Function that make hash that present paymentURLRoken
	 */
	public String createPaymentURLToken(KPRequestDTO request) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getMerchantID());
		sb.append(request.getAmount());
		sb.append(request.getMerchantOrderID());
		String hashCode = Hashing.sha256().hashString(sb.toString(), StandardCharsets.UTF_8).toString();
		return hashCode.substring(0, 50);
	}

// *****************************************For /pay/{url}********************************************************//

	/**
	 * Function that validate user data from bank front
	 */
	public boolean checkCredentials(String token, BuyerDTO buyerDTO) {

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentToken(token);
		if (paymentInfo == null) {
			System.out.println("paymentInfo is null");

			return false;
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			System.out.println("transaction is null");

			return false;
		}

		CardOwner buyer = cardOwnerRepository.findByCardPan(buyerDTO.getPan());
		if (buyer == null) {
			System.out.println("buyer is null");
			// If they are from the same bank it is error
			if (buyerDTO.getPan().substring(0, 6).equals(BankNumber)) {
				System.out.println("same bank, error");
				return false;
			} else {
				System.out.println("different bank");
				return true;
			}
		}

		if (!buyer.getName().toLowerCase().equals(buyerDTO.getName().toLowerCase().trim())) {
			System.out.println("name is not valid");
			return false;
		}

		if (!buyer.getLastName().toLowerCase().equals(buyerDTO.getLastName().toLowerCase().trim())) {
			System.out.println("last name is not valid");
			return false;
		}

		if (!buyer.getCard().getCvv().equals(buyerDTO.getCvv().trim())) {
			System.out.println("cvv is not valid");
			return false;
		}

		// Date is in format MM/YY
		String expDate = buyerDTO.getMonth() + "/" + buyerDTO.getYear();
		if (!buyer.getCard().getExpDate().equals(expDate)) {
			System.out.println("date is not valid");
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

	/**
	 * Function to pay that check is buyer and seller in same bank and call
	 * functions for paying in same or in differente banks
	 */
	public ResponseEntity<String> tryPayment(String token, BuyerDTO buyerDTO, HttpServletResponse response)
			throws PaymentException, InvalidDataException, NoEnoughFundException {

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentToken(token);
		if (paymentInfo == null) {
			throw new PaymentException("URL does not exists!");
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			throw new PaymentException("This transaction does not exists!");
		}

		t.setBuyerPan(buyerDTO.getPan());
		t.setIssuerTimestamp(new Date());
		t.setIssuerOrderId(paymentInfo.getPaymentID());

		CardOwner buyer = cardOwnerRepository.findByCardPan(buyerDTO.getPan());
		if (buyer == null) {
			// Check is seller and buyer in the same bank
			if (buyerDTO.getPan().substring(0, 6).equals(BankNumber)) {
				throw new InvalidDataException("Data is not valid!");
			}
			// Seller and buyer are not in same banke, contacte PCC
			System.out.println("NISU U ISTOJ BANCI");
			boolean isSent=sendRequestToPCC(t, buyerDTO);
			if(isSent) {
				return ResponseEntity.ok(t.getSuccessURL());
			}
			return ResponseEntity.badRequest().body(t.getFailedURL());
		}

		t.setBuyer(buyer);
		transactionRepository.save(t);

		System.out.println("Seller and buyer are in same bank...");

		Card buyerCard = buyer.getCard();
		// Kartica ne postoji, greska
		if (buyerCard == null) {
			save(t, Status.UNSUCCESSFULLY);
			String failedUrl = paymentFailed(paymentInfo, t);
			return new ResponseEntity<>(failedUrl, HttpStatus.BAD_REQUEST);
		}

		// nema dovoljno sredstava
		if (buyerCard.getAvailableFunds() - t.getAmount() < 0) {
			throw new NoEnoughFundException();
		}

		String location = paymentSuccessful(paymentInfo, t);
		return paymentSameBank(t, buyerCard, buyer, location);

	}

	/**
	 * Function that send CompleteDTO to KP if payment failed. Return failedURL
	 */
	public String paymentFailed(PaymentInfo paymentInfo, Transaction t) {

		CompletedDTO completedDTO = new CompletedDTO();
		completedDTO.setTransactionStatus(Status.UNSUCCESSFULLY);
		completedDTO.setMerchantOrderID(t.getMerchantOrderId());
		completedDTO.setAcquirerOrderID(t.getId());
		completedDTO.setAcquirerTimestamp(t.getTimestamp());
		completedDTO.setPaymentID(paymentInfo.getPaymentID());
		completedDTO.setRedirectURL(t.getFailedURL());
		
		RestTemplate template = new RestTemplate();
		try {
			template.postForEntity(replyToKP, completedDTO, Boolean.class);
			return t.getFailedURL();
		} catch (Exception e) {
			System.out.println("KP is not available!");
			save(t, Status.UNAVAILABLE_KP);
			return t.getErrorURL();
		}

	}

	/**
	 * Function that send CompleteDTO to KP if payment is successfull. Return
	 * successURL
	 */
	public String paymentSuccessful(PaymentInfo paymentInfo, Transaction t) {

		CompletedDTO completedDTO = new CompletedDTO();
		completedDTO.setTransactionStatus(Status.SUCCESSFULLY);
		completedDTO.setMerchantOrderID(t.getMerchantOrderId());
		completedDTO.setAcquirerOrderID(t.getId());
		completedDTO.setAcquirerTimestamp(t.getTimestamp());
		completedDTO.setPaymentID(paymentInfo.getPaymentID());
		completedDTO.setRedirectURL(t.getSuccessURL());

		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<Boolean> response=template.postForEntity(replyToKP, completedDTO, Boolean.class);
			if(response.getStatusCode()==HttpStatus.BAD_REQUEST) {
				return t.getFailedURL();
			}
			return t.getSuccessURL();
		} catch (Exception e) {
			System.out.println("KP is unavilable");
			save(t, Status.UNAVAILABLE_KP);
			return t.getErrorURL();
		}

	}

	/**
	 * Function for paying in same bank
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public ResponseEntity<String> paymentSameBank(Transaction t, Card buyerCard, CardOwner buyer, String successURL) {

		Float available = buyerCard.getAvailableFunds();
		buyerCard.setAvailableFunds(available - t.getAmount());
		cardRepository.save(buyerCard);
		buyer.setCard(buyerCard);
		cardOwnerRepository.save(buyer);

		Card sellerCard = cardRepository.findByPan(t.getSellerPan());
		available = sellerCard.getAvailableFunds();
		sellerCard.setAvailableFunds(available + t.getAmount());
		cardRepository.save(sellerCard);
		CardOwner seller = cardOwnerRepository.findByCardPan(sellerCard.getPan());
		seller.setCard(sellerCard);
		cardOwnerRepository.save(seller);

		t.setStatus(Status.SUCCESSFULLY);
		transactionRepository.save(t);

		return new ResponseEntity<>(successURL, HttpStatus.OK);
	}

	/**
	 * Function that send payment request to PCC
	 */
	public boolean sendRequestToPCC(Transaction t, BuyerDTO buyerDTO) {

		save(t, Status.WAITING);

		PCCRequestDTO pccRequestDTO = new PCCRequestDTO();
		pccRequestDTO.setAcquirerOrderID(t.getId());
		pccRequestDTO.setAcquirerTimestamp(t.getTimestamp());
		pccRequestDTO.setCvv(buyerDTO.getCvv());
		pccRequestDTO.setYear(buyerDTO.getYear());
		pccRequestDTO.setMonth(buyerDTO.getMonth());
		pccRequestDTO.setName(buyerDTO.getName());
		pccRequestDTO.setLastName(buyerDTO.getLastName());
		pccRequestDTO.setBuyerPan(buyerDTO.getPan());
		pccRequestDTO.setAmount(t.getAmount());
		pccRequestDTO.setSellerPan(t.getSellerPan());
		pccRequestDTO.setSellerBankNumber(BankNumber);
		pccRequestDTO.setMerchantOrderID(t.getMerchantOrderId());
		pccRequestDTO.setMerchantTimestamp(t.getMerchantTimestamp());

		RestTemplate template = new RestTemplate();
		try {
			System.out.println("SALJEM ZAHTEV NA PCC");
			ResponseEntity<PCCResponseDTO> response = template.postForEntity(requestToPCC, pccRequestDTO,
					PCCResponseDTO.class);
			
			Transaction transaction = transactionRepository.findById(response.getBody().getAcquirerOrderID()).get();

			if (response.getBody().getStatus() == Status.FAILURE) {
				save(transaction, Status.UNSUCCESSFULLY);
				return false;
			} else {
				transaction.setIssuerOrderId(response.getBody().getIssuerOrderID());
				transaction.setIssuerTimestamp(response.getBody().getIssuerTimestamp());
				save(transaction, Status.SUCCESSFULLY);

				Card recieverCard = cardRepository.findByPan(transaction.getSellerPan());

				Float available = recieverCard.getAvailableFunds();
				recieverCard.setAvailableFunds(available + transaction.getAmount());
				cardRepository.save(recieverCard);

				CardOwner seller = cardOwnerRepository.findByCardPan(recieverCard.getPan());
				seller.setCard(recieverCard);
				cardOwnerRepository.save(seller);
				
				PaymentInfo pi=paymentInfoRepository.findByTransaction(transaction);
				String url=paymentSuccessful(pi, transaction);
				if(url.equals(transaction.getFailedURL()))
					return false;
				return true;
			}

		} catch (HttpStatusCodeException exception) {
			
			PaymentInfo pi=paymentInfoRepository.findByTransaction(t);
			paymentFailed(pi, t);
			
			if (exception.getStatusCode().is5xxServerError()) {
				System.out.println("This transaction already exists on PCC");
				save(t, Status.UNSUCCESSFULLY);
			}
			return false;
		} catch (Exception e) {
			PaymentInfo pi=paymentInfoRepository.findByTransaction(t);
			paymentFailed(pi, t);
			save(t, Status.WAITING_PCC);
			return false;
		}

	}

	/**
	 * Function for changing transaction status and saving transacion
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public void save(Transaction t, Status s) {
		t.setStatus(s);
		transactionRepository.save(t);
	}

}