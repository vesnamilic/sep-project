package sep.project.services;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import sep.project.DTOs.PayResponseDTO;
import sep.project.customExceptions.InvalidDataException;
import sep.project.customExceptions.NoEnoughFundException;
import sep.project.customExceptions.PaymentException;
import sep.project.enums.Status;
import sep.project.model.Card;
import sep.project.model.CardOwner;
import sep.project.model.PCCRequest;
import sep.project.model.PaymentInfo;
import sep.project.model.Transaction;
import sep.project.repository.CardOwnerRepository;
import sep.project.repository.CardRepository;
import sep.project.repository.PCCRequestRepository;
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

	@Autowired
	PCCRequestRepository pccRequestRepository;

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

	private static String BankAddress;

	@Value("${BankAddress}")
	public void setBank1URL(String bankAdress) {
		BankAddress = bankAdress;
	}

// *****************************************For /firstRequest********************************************************//

	public boolean validate(KPRequestDTO request) {
		/*
		 * CryptoConverter c = new CryptoConverter(); String a =
		 * c.convertToDatabaseColumn("111111111"); String b =
		 * c.convertToDatabaseColumn("222222222"); System.out.println(a);
		 * System.out.println(b);
		 */

		CardOwner seller = cardOwnerRepository.findByMerchantID(request.getMerchantID());
		if (seller == null) {
			logger.error("Unknown seller");
			return false;
		}

		if (request.getAmount() == null || request.getMerchantID() == null || request.getMerchantOrderID() == null
				|| request.getMerchantPass() == null || request.getMerchantTimestamp() == null) {
			logger.error("Invalid data");
			return false;
		}

		if (request.getAmount() <= 0) {
			logger.error("Amount is les then 0");
			return false;
		}

		if (!seller.getMerchantPass().equals(request.getMerchantPass())) {
			logger.error("bad password");
			return false;
		}
		return true;
	}

	public PaymentInfo createPaymentInfo(KPRequestDTO request) {
		Transaction t = createTransaction(request);
		PaymentInfo paymentInfo = new PaymentInfo(createPaymentURLToken(request), t);

		t.setPaymentURL(paymentInfo.getPaymentURL());

		transactionRepository.save(t);
		paymentInfoRepository.save(paymentInfo);
		
		logger.info("payment info created");
		return paymentInfo;

	}

	public String createPaymentURLToken(KPRequestDTO request) {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getMerchantID());
		sb.append(request.getAmount());
		sb.append(request.getMerchantOrderID());
		String hashCode = Hashing.sha256().hashString(sb.toString(), StandardCharsets.UTF_8).toString();
		return hashCode.substring(0, 50);
	}

	public Transaction createTransaction(KPRequestDTO request) {

		Transaction t = new Transaction();
		CardOwner seller = cardOwnerRepository.findByMerchantID(request.getMerchantID());
		if(seller==null) {
			logger.error("unsucessfull creating transaction, sellerr does not exists");
			return null;
		}
		t.setBuyer(null);
		t.setSeller(seller);
		t.setPaymentURL("");
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
		logger.info("transaction created");
		return t;
	}

// *****************************************For /pay/{url}********************************************************//

	public boolean checkCredentials(String url, BuyerDTO buyerDTO) {

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(url);
		if (paymentInfo == null) {
			logger.error("payment info with url:"+url+" does not exists");
			return false;
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			logger.error("transaction does not exists in payment with id:"+paymentInfo.getPaymentID());
			return false;
		}

		CardOwner buyer = cardOwnerRepository.findByCardPan(buyerDTO.getPan());
		if (buyer == null) {
			// If they are from the same bank it is error
			if (buyerDTO.getPan().substring(0, 6).equals(BankNumber)) {
				logger.error("Buyer and seller are in the sam bank, data is not valid");
				return false;
			} else {
				logger.info("Buyer and seller are in different banks");
				return true;
			}
		}

		if (!buyer.getName().toLowerCase().equals(buyerDTO.getName().toLowerCase().trim())) {
			logger.error("Buyer data is not valid");
			return false;
		}

		if (!buyer.getLastName().toLowerCase().equals(buyerDTO.getLastName().toLowerCase().trim())) {
			logger.error("Buyer data is not valid");
			return false;
		}

		if (!buyer.getCard().getCvv().equals(buyerDTO.getCvv().trim())) {
			logger.error("Buyer data is not valid");
			return false;
		}

		// Date is in format MM/YY
		String expDate = buyerDTO.getMonth() + "/" + buyerDTO.getYear();
		if (!buyer.getCard().getExpDate().equals(expDate)) {
			logger.error("Buyer data is not valid");
			return false;
		}

		int pos = expDate.lastIndexOf("/");
		int year = Integer.valueOf(expDate.substring(pos + 1));
		int month = Integer.valueOf(expDate.substring(0, pos));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateNowString = dateFormat.format(new Date(System.currentTimeMillis()));
		Date now = null;
		try {
			now = dateFormat.parse(dateNowString);
		} catch (ParseException e) {
			logger.error("Date format exception");
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
			logger.error("Date format exception");
			e.printStackTrace();
		}

		if (expiresDate.compareTo(now) < 0) {
			logger.error("Date format exceptionCard was expired");
			return false;
		}

		return true;
	}

	public ResponseEntity<PayResponseDTO> tryPayment(String url, BuyerDTO buyerDTO, HttpServletResponse response)
			throws PaymentException, InvalidDataException, NoEnoughFundException {

		PayResponseDTO responseDTO = new PayResponseDTO();
		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(url);
		if (paymentInfo == null) {
			logger.error("url: "+paymentInfo.getPaymentURL()+" does not exists");
			throw new PaymentException("URL does not exists!");
		}

		Transaction t = paymentInfo.getTransaction();

		if (t == null) {
			logger.error("Transaction does not exists");
			throw new PaymentException("This transaction does not exists!");
		}
		t.setBuyerPan(buyerDTO.getPan());
		t.setIssuerTimestamp(new Date());
		t.setIssuerOrderId(paymentInfo.getPaymentID());

		CardOwner buyer = cardOwnerRepository.findByCardPan(buyerDTO.getPan());
		if (buyer == null) {

			// Check is seller and buyer in the same bank
			if (buyerDTO.getPan().substring(0, 6).equals(BankNumber)) {
				logger.error("Buyer data is not valid");
				throw new InvalidDataException("Data is not valid!");
			}
			// Seller and buyer are not in same banke, contacte PCC
			sendRequestToPCC(t, buyerDTO);
			responseDTO.setLocation("/paymentSent");
			logger.info("Request sent to PCC");
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		}

		t.setBuyer(buyer);
		transactionRepository.save(t);

		System.out.println("Seller and buyer are in same bank...");

		Card buyerCard = buyer.getCard();
		if (buyerCard == null) {
			logger.error("buyer does not have a card");
			save(t, Status.UNSUCCESSFULLY);
			String failedUrl = paymentFailed(paymentInfo, t, url, buyerDTO);
			responseDTO.setLocation(failedUrl);
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
		if (buyerCard.getAvailableFunds() - t.getAmount() < 0) {
			logger.error("There is no enough money in the card");
			throw new NoEnoughFundException();
		}

		String location = paymentSuccessful(paymentInfo, t);
		return paymentSameBank(t, buyerCard, buyer, location);

	}

	public String paymentFailed(PaymentInfo paymentInfo, Transaction t, String url, BuyerDTO buyerDTO) {

		CompletedDTO completedDTO = new CompletedDTO();
		completedDTO.setTransactionStatus(Status.UNSUCCESSFULLY);
		completedDTO.setMerchantOrderID(t.getMerchantOrderId());
		completedDTO.setAcquirerOrderID(t.getId());
		completedDTO.setAcquirerTimestamp(t.getTimestamp());
		completedDTO.setPaymentID(paymentInfo.getPaymentID());
		completedDTO.setRedirectURL(t.getFailedURL());
		save(t, Status.UNSUCCESSFULLY);

		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<Boolean> response = template.postForEntity(replyToKP, completedDTO, Boolean.class);
			if (response.getBody()) {
				logger.info("KP is informed");
				return t.getFailedURL();
			}
			else {
				logger.info("KP is informed");
				return "/failed";
			}
		} catch (Exception e) {
			logger.error("KP is not available");
			System.out.println("KP is not available!");
			save(t, Status.UNSUCCESSFULLY_KP);
			return "/failed";
		}

	}

	public void sendRequestToPCC(Transaction t, BuyerDTO buyerDTO) {

		save(t, Status.WAITING);

		PCCRequestDTO pccRequestDTO = new PCCRequestDTO();
		pccRequestDTO.setAcquirerOrderID(t.getId());
		pccRequestDTO.setAcquirerTimestamp(t.getTimestamp());
		pccRequestDTO.setCvv(buyerDTO.getCvv());
		pccRequestDTO.setYear(buyerDTO.getYear());
		pccRequestDTO.setMonth(buyerDTO.getMonth());
		pccRequestDTO.setName(buyerDTO.getName());
		pccRequestDTO.setLastName(buyerDTO.getLastName());
		pccRequestDTO.setSenderPan(buyerDTO.getPan());
		pccRequestDTO.setAmount(t.getAmount());
		pccRequestDTO.setReturnURL(BankAddress + "pccReply");
		pccRequestDTO.setRecieverPan(t.getSellerPan());
		pccRequestDTO.setSellerBankNumber(BankNumber);
		pccRequestDTO.setMerchantOrderID(t.getMerchantOrderId());
		pccRequestDTO.setMerchantTimestamp(t.getMerchantTimestamp());

		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity responseEntity = template.postForEntity(requestToPCC, pccRequestDTO, PCCRequestDTO.class);
			logger.info("PCC request sent");

		} catch (HttpStatusCodeException exception) {
			if (exception.getStatusCode().is5xxServerError()) {
				logger.error("Transaction with id: "+t.getId()+" already exists on PCC");
				System.out.println("This transaction already exists on PCC");
				save(t, Status.UNSUCCESSFULLY);
			}
		} catch (Exception e) {
			save(t, Status.WAITING_PCC);
			logger.error("PCC is not available");
			PCCRequest pccRequest = new PCCRequest(pccRequestDTO);
			pccRequestRepository.save(pccRequest);
		}

	}

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
			ResponseEntity<Boolean> response = template.postForEntity(replyToKP, completedDTO, Boolean.class);
			if (response.getBody()) {
				logger.info("KP is informed about success");
				return t.getSuccessURL();
			} else {
				logger.info("KP is informed about success");
				return t.getErrorURL();
			}
		} catch (Exception e) {
			logger.error("KP is not available");
			System.out.println("KP is not available");
			save(t, Status.SUCCESSFULLY_KP);
			return "/paymentSent";
		}

	}

	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public ResponseEntity<PayResponseDTO> paymentSameBank(Transaction t, Card buyerCard, CardOwner buyer,
			String location) {

		PayResponseDTO response = new PayResponseDTO();

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

		response.setLocation(location);
		logger.info("Payed successfull");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

//*****************************************For comunications with PCC********************************************************//	

	/*
	 * public void finalizePayment(PCCResponseDTO pccResponseDTO) {
	 * 
	 * Transaction t =
	 * transactionRepository.findById(pccResponseDTO.getAcquirerOrderID()).get();
	 * 
	 * PaymentInfo paymentInfo = paymentInfoRepository.findByTransaction(t);
	 * 
	 * if (pccResponseDTO.getStatus() == Status.UNSUCCESSFULLY) { save(t,
	 * Status.UNSUCCESSFULLY); } else {
	 * t.setIssuerOrderId(pccResponseDTO.getIssuerOrderID());
	 * t.setIssuerTimestamp(pccResponseDTO.getIssuerTimestamp()); save(t,
	 * Status.SUCCESSFULLY); Card recieverCard =
	 * cardRepository.findByPan(t.getSellerPan());
	 * 
	 * Float available = recieverCard.getAvailableFunds();
	 * recieverCard.setAvailableFunds(available + t.getAmount());
	 * cardRepository.save(recieverCard);
	 * 
	 * CardOwner seller = cardOwnerRepository.findByCardPan(recieverCard.getPan());
	 * seller.setCard(recieverCard); cardOwnerRepository.save(seller);
	 * 
	 * } System.out.println("Novi status transakcije ID: " + t.getId() + " je " +
	 * t.getStatus().toString()); sendReplyToKP(t, paymentInfo); }
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public void save(Transaction t, Status s) {
		t.setStatus(s);
		transactionRepository.save(t);
	}
	/*
	 * private void sendReplyToKP(Transaction t, PaymentInfo pccResponse) {
	 * 
	 * CompletedDTO completedDTO = new CompletedDTO();
	 * completedDTO.setTransactionStatus(t.getStatus());
	 * completedDTO.setMerchantOrderID(t.getMerchantOrderId());
	 * completedDTO.setAcquirerOrderID(t.getId());
	 * completedDTO.setAcquirerTimestamp(t.getTimestamp());
	 * completedDTO.setPaymentID(pccResponse.getPaymentID()); if (t.getStatus() ==
	 * Status.SUCCESSFULLY) { completedDTO.setRedirectURL(t.getSuccessURL()); } else
	 * { completedDTO.setRedirectURL(t.getFailedURL()); }
	 * 
	 * RestTemplate template = new RestTemplate(); try {
	 * template.postForEntity(replyToKP, completedDTO, Boolean.class); } catch
	 * (Exception e) { System.out.println("KP nije dostupan,metoda sendReplyToKP.");
	 * if (t.getStatus() == Status.SUCCESSFULLY) { save(t, Status.SUCCESSFULLY_KP);
	 * } else { save(t, Status.UNSUCCESSFULLY_KP); } } }
	 */
}