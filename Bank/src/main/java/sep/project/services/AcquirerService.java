package sep.project.services;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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

	private static String Bank1Number;

	@Value("${Bank1Number}")
	public void setBURL(String bank1Number) {
		Bank1Number = bank1Number;
	}

	private static String BankAddress;

	@Value("${BankAddress}")
	public void setBank1URL(String bankAdress) {
		BankAddress = bankAdress;
	}
	
	//*****************************************For /firstRequest********************************************************//	

		public boolean validate(KPRequestDTO request) {

			CardOwner seller = cardOwnerRepository.findByMerchantID(request.getMerchantID());

			if (seller == null) {
				return false;
			}

			if (request.getAmount() == null || request.getMerchantID() == null || request.getMerchantOrderID() == null
					|| request.getMerchantPass() == null || request.getMerchantTimestamp() == null) {
				return false;
			}

			if (request.getAmount() <= 0) {
				return false;
			}

			if (!seller.getMerchantPass().equals(request.getMerchantPass())) {
				return false;
			}

			return true;
		}

		public PaymentInfo createPaymentInfo(KPRequestDTO request) {

			Transaction t = createTransaction(request);

			PaymentInfo paymentInfo = new PaymentInfo(createURLToken(request), t);

			t.setPaymentURL(paymentInfo.getPaymentURL());

			transactionRepository.save(t);
			paymentInfoRepository.save(paymentInfo);

			return paymentInfo;

		}
		
		public String createURLToken(KPRequestDTO request) {
			StringBuilder sb = new StringBuilder();
			sb.append(request.getMerchantID());
			sb.append(request.getAmount());
			sb.append(request.getMerchantOrderID());
			String hashCode = Hashing.sha256()
					  .hashString(sb.toString(), StandardCharsets.UTF_8)
					  .toString();
			return hashCode.substring(0, 50);
		}

		public Transaction createTransaction(KPRequestDTO request) {

			CardOwner seller = cardOwnerRepository.findByMerchantID(request.getMerchantID());
			Transaction t = new Transaction();
			t.setBuyer(null);
			t.setSeller(seller);
			t.setPaymentURL("");
			t.setTimestamp(new Date());
			t.setStatus(Status.K);
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

	//*****************************************For /pay/{url}********************************************************//	

	public boolean checkCredentials(String url, BuyerDTO buyerDTO) {

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(url);
		if (paymentInfo == null) {
			return false;
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			return false;
		}

		CardOwner buyer = cardOwnerRepository.findByCardPan(buyerDTO.getPan());
		if (buyer == null) {
			// Ako je iz nase banke i ne mozemo da ga nedjemo, greska je u podacima
			if (buyerDTO.getPan().substring(0, 6).equals(Bank1Number)) {
				return false;
			} else {
				return true;
			}
		}

		if (!buyer.getName().toLowerCase().equals(buyerDTO.getName().toLowerCase().trim())) {
			return false;
		}

		if (!buyer.getLastName().toLowerCase().equals(buyerDTO.getLastName().toLowerCase().trim())) {
			return false;
		}

		if (!buyer.getCard().getCvv().equals(buyerDTO.getCvv().trim())) {
			return false;
		}

		String expDate = buyerDTO.getMonth() + "/" + buyerDTO.getYear();
		if (!buyer.getCard().getExpDate().equals(expDate))
		{
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
			e.printStackTrace();
		}

		Date expiresDate = null;
		try {
			expiresDate = dateFormat.parse("20" + year + "-" + month + "-31");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (expiresDate.compareTo(now) < 0) {
			System.out.println("The card has expired");
			return false;
		}

		return true;
	}

	public ResponseEntity<Map<String, String>> tryPayment(String url, BuyerDTO buyerDTO, HttpServletResponse response)
			throws PaymentException, InvalidDataException, NoEnoughFundException {

		Map<String, String> map = new HashMap<>();
		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(url);
		if (paymentInfo == null) {
			throw new PaymentException("URL does not exists!");
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			throw new PaymentException("This transaction does not exists!");
		}
		t.setBuyerPan(buyerDTO.getPan());

		CardOwner buyer = cardOwnerRepository.findByCardPan(buyerDTO.getPan());
		if (buyer == null) {
			// Check is seller and buyer in the same bank
			if (buyerDTO.getPan().substring(0, 6).equals(Bank1Number)) {
				throw new InvalidDataException("Data is not valid!");
			}
			// Seller and buyer are not in same banke, contacte PCC
			sendRequestToPCC(t,buyerDTO);
			map.put("Location", "/paymentSent");
			return new ResponseEntity<>(map, HttpStatus.OK);
		}

		System.out.println("Seller and buyer are in same bank...");

		Card buyerCard = buyer.getCard();
		if (buyerCard == null) {
			save(t, Status.N);
			paymentFailed(paymentInfo, t, url, buyerDTO);
			map.put("Location", "/failed");
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
		}
		if (buyerCard.getAvailableFunds() - t.getAmount() < 0)
			throw new NoEnoughFundException();

		String location = paymentSuccessful(paymentInfo, t);
		return placanjeIstaBanka(t, buyerCard, buyer, location);

	}

	public String paymentFailed(PaymentInfo paymentInfo, Transaction t, String url, BuyerDTO buyerDTO) {

		CompletedDTO completedDTO = new CompletedDTO();
		completedDTO.setTransactionStatus(Status.N);
		completedDTO.setMerchantOrderID(t.getMerchantOrderId());
		completedDTO.setAcquirerOrderID(t.getId());
		completedDTO.setAcquirerTimestamp(t.getTimestamp());
		completedDTO.setPaymentID(paymentInfo.getPaymentID());
		completedDTO.setRedirectURL(t.getFailedURL());
		save(t, Status.N);

		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<Boolean> response = template.postForEntity(replyToKP, completedDTO, Boolean.class);
			if (response.getBody())
				return t.getFailedURL();
			else
				return "/failed";
		} catch (Exception e) {
			System.out.println("KP is not available!");
			save(t, Status.N_KP);
			return "/failed";
		}

	}

	public void sendRequestToPCC(Transaction t, BuyerDTO buyerDTO) {

		save(t, Status.C);

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
		pccRequestDTO.setSellerBankNumber(Bank1Number);
		pccRequestDTO.setMerchantOrderID(t.getMerchantOrderId());
		pccRequestDTO.setMerchantTimestamp(t.getMerchantTimestamp());

		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity responseEntity = template.postForEntity(requestToPCC, pccRequestDTO, PCCRequestDTO.class);

		} catch (HttpStatusCodeException exception) {
			if (exception.getStatusCode().is5xxServerError()) {
				System.out.println("This transaction already exists on PCC");
				save(t, Status.N);
			}
		} catch (Exception e) {
			save(t, Status.C_PCC);
			PCCRequest pccRequest = new PCCRequest(pccRequestDTO);
			pccRequestRepository.save(pccRequest);
		}

	}
	
	public String paymentSuccessful(PaymentInfo paymentInfo, Transaction t){
        
        CompletedDTO completedDTO = new CompletedDTO();
		completedDTO.setTransactionStatus(Status.U);
		completedDTO.setMerchantOrderID(t.getMerchantOrderId());
		completedDTO.setAcquirerOrderID(t.getId());
		completedDTO.setAcquirerTimestamp(t.getTimestamp());
		completedDTO.setPaymentID(paymentInfo.getPaymentID());
		completedDTO.setRedirectURL(t.getSuccessURL());
        
        RestTemplate template = new RestTemplate();
        try {
            ResponseEntity<Boolean> response = template.postForEntity(replyToKP, completedDTO, Boolean.class);
            if(response.getBody()) {
                return t.getSuccessURL();
            }
            else {
            	return t.getErrorURL();
            }
        } catch(Exception e) {
            System.out.println("KP is not available");
            save(t, Status.U_KP);
            System.out.println("paymentSuccessful Funkcija catch blok jer je ista banka...");
            return "/paymentSent";
        }

    }
	
	 @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	    public ResponseEntity<Map<String, String>> placanjeIstaBanka(Transaction t, Card buyerCard, CardOwner buyer, String location ){
	        Map<String, String> map = new HashMap<>();
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

	        t.setStatus(Status.U);
	        transactionRepository.save(t);

	        map.put("Location", location);
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Location", location);
	        headers.add("Access-Control-Allow-Origin", "*");
	        return new ResponseEntity<>(map, headers, HttpStatus.OK);
	    }
	
	
//*****************************************For comunications with PCC********************************************************//	
	

	public void finalizePayment(PCCResponseDTO pccResponseDTO) {

		Transaction t = transactionRepository.findById(pccResponseDTO.getAcquirerOrderID()).get();

		PaymentInfo paymentInfo = paymentInfoRepository.findByTransaction(t);

		if (pccResponseDTO.getStatus() == Status.N) {
			save(t, Status.N);
		} else {
			t.setIssuerOrderId(pccResponseDTO.getIssuerOrderID());
			t.setIssuerTimestamp(pccResponseDTO.getIssuerTimestamp());
			save(t, Status.U);
			Card recieverCard = cardRepository.findByPan(t.getSellerPan());

			Float available = recieverCard.getAvailableFunds();
			recieverCard.setAvailableFunds(available + t.getAmount());
			cardRepository.save(recieverCard);

			CardOwner seller = cardOwnerRepository.findByCardPan(recieverCard.getPan());
			seller.setCard(recieverCard);
			cardOwnerRepository.save(seller);

		}
		System.out.println("Novi status transakcije ID: " + t.getId() + " je " + t.getStatus().toString());
		sendReplyToKP(t, paymentInfo);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public void save(Transaction t, Status s) {
		t.setStatus(s);
		transactionRepository.save(t);
	}

	private void sendReplyToKP(Transaction t, PaymentInfo pccResponse) {

		CompletedDTO completedDTO = new CompletedDTO();
		completedDTO.setTransactionStatus(t.getStatus());
		completedDTO.setMerchantOrderID(t.getMerchantOrderId());
		completedDTO.setAcquirerOrderID(t.getId());
		completedDTO.setAcquirerTimestamp(t.getTimestamp());
		completedDTO.setPaymentID(pccResponse.getPaymentID());
		if (t.getStatus() == Status.U) {
			completedDTO.setRedirectURL(t.getSuccessURL());
		} else {
			completedDTO.setRedirectURL(t.getFailedURL());
		}

		RestTemplate template = new RestTemplate();
		try {
			template.postForEntity(replyToKP, completedDTO, Boolean.class);
		} catch (Exception e) {
			System.out.println("KP nije dostupan,metoda sendReplyToKP.");
			if (t.getStatus() == Status.U) {
				save(t, Status.U_KP);
			} else {
				save(t, Status.N_KP);
			}
		}
	}

	
}