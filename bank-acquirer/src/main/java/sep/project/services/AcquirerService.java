package sep.project.services;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.common.hash.Hashing;

import sep.project.DTOs.BuyerDTO;
import sep.project.DTOs.CompletedDTO;
import sep.project.DTOs.KPRequestDTO;
import sep.project.DTOs.PCCRequestDTO;
import sep.project.DTOs.PCCResponseDTO;
import sep.project.DTOs.UrlDTO;
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
	private TransactionRepository transactionRepository;

	@Autowired
	private PaymentInfoRepository paymentInfoRepository;

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private CardOwnerRepository cardOwnerRepository;

	private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

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
	
	private static String bankSuccessURL;

	@Value("${bankSuccessURL}")
	public void setBankSuccessURL(String url) {
		bankSuccessURL = url;
	}
	
	private static String bankErrorURL;

	@Value("${bankSuccessURL}")
	public void setBankErrorURL(String url) {
		bankErrorURL = url;
	}
	
	private static String bankFailedURL;

	@Value("${bankFailedURL}")
	public void setBankFailedURL(String url) {
		bankFailedURL = url;
	}

//*****************************************For /firstRequest********************************************************//

	/**
	 * Function that validate first selling request
	 */
	public boolean validate(KPRequestDTO request) {

		logger.info("INFO | validate called");

		CardOwner seller = cardOwnerRepository.findByMerchantID(request.getMerchantID());
		if (seller == null) {
			logger.error("ERROR | seller does not exists");
			return false;
		}

		if (request.getAmount() == null || request.getMerchantID() == null || request.getMerchantOrderID() == null
				|| request.getMerchantPass() == null || request.getMerchantTimestamp() == null) {
			logger.error("ERROR | some data is not valid");
			return false;
		}

		if (request.getAmount() <= 0) {
			logger.error("ERROR | amount error");
			return false;
		}

		if (!seller.getMerchantPass().equals(request.getMerchantPass())) {
			logger.error("ERROR | data is not valid");
			return false;
		}
		return true;
	}

	public PaymentInfo createPaymentInfo(KPRequestDTO request) {
		logger.info("INFO | creating payment info");

		Transaction t = createTransaction(request);
		if (t == null) {
			logger.error("ERROR | transaction does not exists");
			return null;
		}

		PaymentInfo paymentInfo = new PaymentInfo(createPaymentURLToken(request), t);

		t.setPaymentId(paymentInfo.getPaymentId());

		transactionRepository.save(t);
		paymentInfoRepository.save(paymentInfo);

		return paymentInfo;
	}

	/**
	 * Function that cretae transaction from KPRequestDTO
	 */
	public Transaction createTransaction(KPRequestDTO request) {
		logger.info("INFO | creating transaction function");

		Transaction t = new Transaction();
		CardOwner seller = cardOwnerRepository.findByMerchantID(request.getMerchantID());
		if (seller == null) {
			logger.error("ERROR | seller does not exists, can not create transaction");
			return null;
		}
		t.setBuyer(null);
		t.setSeller(seller);
		t.setPaymentId("");
		t.setTimestamp(new Date());
		t.setStatus(Status.CREATED);
		t.setSellerPan(seller.getCard().getPan());
		t.setBuyerPan(null);
		t.setAmount(request.getAmount());
		t.setMerchantOrderId(request.getMerchantOrderID());
		t.setMerchantTimestamp(request.getMerchantTimestamp());
		transactionRepository.save(t);

		return t;
	}

	/**
	 * Function that make hash that present paymentURLRoken
	 */
	public String createPaymentURLToken(KPRequestDTO request) {
		logger.info("INFO | creating payment token");
		StringBuilder sb = new StringBuilder();
		sb.append(request.getMerchantID());
		sb.append(request.getAmount());
		sb.append(request.getMerchantOrderID());
		sb.append(new Date().toString());
		String hashCode = Hashing.sha256().hashString(sb.toString(), StandardCharsets.UTF_8).toString();
		return hashCode.substring(0, 50);
	}

// *****************************************For /pay/{url}********************************************************//

	/**
	 * Function that validate user data from bank front
	 */
	public boolean checkCredentials(String paymentId, BuyerDTO buyerDTO) {

		logger.info("INFO | checking credentials function called");

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentId(paymentId);
		if (paymentInfo == null) {
			logger.error("ERROR | payment info does not exists");
			return false;
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			logger.error("ERROR | transaction does not exists");
			return false;
		}

		CardOwner buyer = cardOwnerRepository.findByCardPan(buyerDTO.getPan());
		if (buyer == null) {
			// If they are from the same bank it is error
			if(buyerDTO.getPan().length()<6)
				return false;
			if (buyerDTO.getPan().substring(0, 6).equals(BankNumber)) {
				logger.error("ERROR | seller and buyer is in the same bank, but data is not valid");
				return false;
			} else {
				logger.info("INFO | seller and buyer are in different banks");
				return true;
			}
		}

		if (!buyer.getName().toLowerCase().equals(buyerDTO.getName().toLowerCase().trim())) {
			logger.error("ERROR | data is not valid");
			return false;
		}

		if (!buyer.getLastName().toLowerCase().equals(buyerDTO.getLastName().toLowerCase().trim())) {
			logger.error("ERROR | data is not valid");
			return false;
		}

		if (!buyer.getCard().getCvv().equals(buyerDTO.getCvv().trim())) {
			logger.error("ERROR | data is not valid");
			return false;
		}

		// Date is in format MM/YY
		String expDate = buyerDTO.getMonth() + "/" + buyerDTO.getYear();
		if (!buyer.getCard().getExpDate().equals(expDate)) {
			logger.error("ERROR | data is not valid");
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
			logger.error("ERROR | data is not valid");
		}

		if (expiresDate.compareTo(now) < 0) {
			logger.error("ERROR | data is not valid");
			return false;
		}

		return true;
	}

	/**
	 * Function to pay that check is buyer and seller in same bank and call
	 * functions for paying in same or in differente banks
	 */
	public ResponseEntity<?> tryPayment(String paymentId, BuyerDTO buyerDTO) {

		logger.info("INFO | try payment function is called");

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentId(paymentId);

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			logger.error("ERROR | transaction does not exists");
			return notifyKP(paymentInfo, t);
		}

		if (checkExpiration(t)) {
			logger.error("ERROR | time for transaction is expiered");
			save(t, Status.EXPIRED);
			return notifyKP(paymentInfo, t);
		}

		if (!checkCredentials(paymentId, buyerDTO)) {
			logger.error("ERROR | credentials is not valid");
			return ResponseEntity.badRequest().body("Data is not valid");
		}

		t.setBuyerPan(buyerDTO.getPan());
		t.setIssuerTimestamp(new Date());
		t.setIssuerOrderId(paymentInfo.getId());

		CardOwner buyer = cardOwnerRepository.findByCardPan(buyerDTO.getPan());
		if (buyer == null) {
			// Check is seller and buyer in the same bank
			if (buyerDTO.getPan().substring(0, 6).equals(BankNumber)) {
				logger.error("ERROR | Seller and buyer are in the same bank, but data is not valid");
				save(t, Status.UNSUCCESSFULLY);
				return notifyKP(paymentInfo, t);
			}
			logger.error("ERROR | seller and buyer are in different banks");
			return sendRequestToPCC(t, buyerDTO);
		}

		t.setBuyer(buyer);
		transactionRepository.save(t);

		Card buyerCard = buyer.getCard();
		// Kartica ne postoji, greska
		if (buyerCard == null) {
			logger.error("ERROR | data is not valid");
			save(t, Status.UNSUCCESSFULLY);
			return notifyKP(paymentInfo, t);
		}

		// nema dovoljno sredstava
		if (buyerCard.getAvailableFunds() - t.getAmount() < 0) {
			logger.error("ERROR | data is not valid, no enough founds");
			save(t, Status.UNSUCCESSFULLY);
			return notifyKP(paymentInfo, t);
		}

		paymentSameBank(t, buyerCard, buyer);

		return notifyKP(paymentInfo, t);
	}

	/**
	 * Function that send CompleteDTO to KP if payment is successfull. Return
	 * successURL
	 */
	public ResponseEntity<UrlDTO> notifyKP(PaymentInfo paymentInfo, Transaction t) {

		logger.info("INFO | payment successful is called");

		CompletedDTO completedDTO = new CompletedDTO();

		if (t != null) {
			completedDTO.setTransactionStatus(t.getStatus());
			completedDTO.setMerchantOrderID(t.getMerchantOrderId());
			completedDTO.setAcquirerOrderID(t.getId());
			completedDTO.setAcquirerTimestamp(t.getTimestamp());
		}

		if (paymentInfo != null) {
			completedDTO.setPaymentID(paymentInfo.getPaymentId());
		}

		RestTemplate template = new RestTemplate();
		try {
			logger.info("INFO | sending request to KP");
			ResponseEntity<UrlDTO> returnDTO = template.postForEntity(replyToKP, completedDTO, UrlDTO.class);
			System.out.println("URL: "+returnDTO.getBody().getUrl());
			return returnDTO;

		} catch (HttpClientErrorException e) {
			//Ako je bad request zanci da nije uspesno kontaktirana nc
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				logger.info("INFO | KP return bad request");
				
				//ako je status transakcije uspesno onda saljemo na success od banke
				if(t.getStatus()==Status.SUCCESSFULLY) {
					return ResponseEntity.ok(new UrlDTO(bankSuccessURL));
				}
				//ako je status error saljemo na error od banke
				if(t.getStatus()==Status.ERROR) {
					return ResponseEntity.ok(new UrlDTO(bankErrorURL));
				}
				//ako je status failed saljemo na failed od banke
				return ResponseEntity.ok(new UrlDTO(bankFailedURL));
			} 
			else {
				return ResponseEntity.ok(new UrlDTO(bankErrorURL));
			}
		} catch (Exception e) {
			return ResponseEntity.ok(new UrlDTO(bankErrorURL));
		}

	}

	/**
	 * Function for paying in same bank
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public boolean paymentSameBank(Transaction t, Card buyerCard, CardOwner buyer) {

		logger.info("INFO | payment in same bank is called");

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
		logger.info("INFO | payment in same bank is successful");

		return true;

	}

	/**
	 * Function that send payment request to PCC
	 */
	public ResponseEntity<?> sendRequestToPCC(Transaction t, BuyerDTO buyerDTO) {

		logger.info("INFO | sending request to PCC function is called");

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
		pccRequestDTO.setPaymentId(t.getPaymentId());

		RestTemplate template = new RestTemplate();
		try {
			logger.info("INFO | sending request to PCC");
			ResponseEntity<PCCResponseDTO> response = template.postForEntity(requestToPCC, pccRequestDTO,
					PCCResponseDTO.class);

			Transaction transaction = transactionRepository.findById(response.getBody().getAcquirerOrderID()).get();

			logger.info("INFO | PCC send response");
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

			PaymentInfo pi = paymentInfoRepository.findByTransaction(transaction);
			logger.info("INFO | PCC retruned success url");

			ResponseEntity<UrlDTO> notifyDTO = notifyKP(pi, transaction);
			System.out.println("url "+notifyDTO.getBody().getUrl());
			return notifyDTO;

		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				if (e.getResponseBodyAsString() == null) {
					save(t, Status.ERROR);
					PaymentInfo pi = paymentInfoRepository.findByTransaction(t);
					return notifyKP(pi, t);
				}
				logger.info("INFO | PCC return bad request");
				save(t, Status.UNSUCCESSFULLY);
				PaymentInfo pi = paymentInfoRepository.findByTransaction(t);
				return notifyKP(pi, t);
			} else {
				PaymentInfo pi = paymentInfoRepository.findByTransaction(t);
				save(t, Status.ERROR);
				return notifyKP(pi, t);
			}
		} catch (Exception e) {
			PaymentInfo pi = paymentInfoRepository.findByTransaction(t);
			save(t, Status.ERROR);
			return notifyKP(pi, t);
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

	@Scheduled(initialDelay = 1800000, fixedRate = 1800000)
	public void checkWaitingTransactions() {
		List<Transaction> transactions = transactionRepository.findAllByStatus(Status.WAITING);
		for (Transaction t : transactions) {
			String url = requestToPCC + "/returnMonay";

			// sending request to return money
			RestTemplate template = new RestTemplate();
			try {
				String paymentId = t.getPaymentId();
				ResponseEntity<Boolean> response = template.postForEntity(url, paymentId, Boolean.class);
				if (response.getBody()) {
					t.setStatus(Status.CANCELED);
					transactionRepository.save(t);
				}
			} catch (Exception e) {
				logger.error("ERROR| Transaction does not exists in pcc");
			}
		}
	}

	public Status checkTransaction(String paymentId) {
		Transaction t = transactionRepository.findByPaymentId(paymentId);
		if (t == null) {
			return null;
		}
		return t.getStatus();
	}

	/**
	 * Function for checking is transaction expiered.
	 */
	public boolean checkExpiration(Transaction t) {
		Date createDate = t.getTimestamp();
		Date nowDate = new Date();
		long diffInMillies = Math.abs(nowDate.getTime() - createDate.getTime());
		long diffInMinutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
		System.out.println("proslo je minuta: " + diffInMinutes);
		if (diffInMinutes > 30)
			return true;
		return false;
	}

}