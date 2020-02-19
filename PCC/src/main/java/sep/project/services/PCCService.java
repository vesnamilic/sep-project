package sep.project.services;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import sep.project.DTOs.PCCRequestDTO;
import sep.project.DTOs.PCCResponseDTO;
import sep.project.enums.Status;
import sep.project.model.Bank;
import sep.project.model.Request;
import sep.project.repository.BankRepository;
import sep.project.repository.RequestRepository;
import sep.project.utils.CryptoConverter;

@Service
public class PCCService {

	@Autowired
	private RequestRepository requestRepository;

	@Autowired
	private BankRepository bankRepository;

	private CryptoConverter crypto=new CryptoConverter();
	
	private static final Logger logger = LoggerFactory.getLogger(PCCService.class);

	/**
	 * If request exsists for this order return null, else create request
	 */
	public Request checkRequest(PCCRequestDTO requestDTO) {
		logger.info("INFO | check request function called");
		Request request = requestRepository.findByAcquirerOrderID(requestDTO.getAcquirerOrderID());
		if (request == null) {
			logger.info("INFO | Request is created.");
			return createRequest(requestDTO);
		} else {
			logger.error("ERROR | Request exists");
			return null;
		}
	}

	/**
	 * Crete request from RequestDTO
	 */
	public Request createRequest(PCCRequestDTO requestDTO) {
		logger.info("INFO | create request function called");

		Bank customerBank = null;
		Bank sellerBank=null;
		List<Bank>banks=bankRepository.findAll();
		for(Bank b:banks) {
			String bankNumber=crypto.convertToEntityAttribute(b.getBankNumber());
			if(bankNumber.equals(requestDTO.getBuyerPan().substring(0, 6))) {
				customerBank=b;
			}
			if(bankNumber.equals(requestDTO.getSellerBankNumber())) {
				sellerBank=b;
			}
		}

		Request request = new Request();
		request.setStatus(Status.CREATED);
		request.setAcquirerOrderID(requestDTO.getAcquirerOrderID());
		request.setAcquirerTimestamp(requestDTO.getAcquirerTimestamp());
		request.setCustomerBank(customerBank);
		request.setSellerBank(sellerBank);
		request.setCreateTime(new Date(System.currentTimeMillis()));
		request.setMerchantOrderId(requestDTO.getMerchantOrderID());
		request.setPaymentId(crypto.convertToDatabaseColumn(requestDTO.getPaymentId()));
		requestRepository.save(request);
		logger.info("INFO | request is created");
		
		return request;
	}

	/**
	 * Find bank by bank number
	 */
	public Bank getBankByPan(String pan) {
		logger.info("INFO | get bank form pan function is called");
		String bankNumber = pan.substring(0, 6);
		
		List<Bank>banks=bankRepository.findAll();
		for(Bank b:banks) {
			String bNumber=crypto.convertToEntityAttribute(b.getBankNumber());
			if(bankNumber.equals(bNumber)) {
				return b;
			}
		}
		return null;
		
	}

	/**
	 * Function that make PCCResponse object for failiour respose
	 */
	public ResponseEntity<PCCResponseDTO> makeFiliureResponse(Request request) {
		logger.info("INFO | make failoure response function is called");
		PCCResponseDTO rersponseDTO = new PCCResponseDTO();
		rersponseDTO.setAcquirerOrderID(request.getAcquirerOrderID());
		rersponseDTO.setAcquirerTimestamp(request.getAcquirerTimestamp());
		rersponseDTO.setMerchantOrderID(request.getMerchantOrderId());
		rersponseDTO.setStatus(Status.FAILURE);
		rersponseDTO.setPaymentId(request.getPaymentId());
		request.setIssuerOrderID(request.getIssuerOrderID());
		request.setIssuerTimestamp(request.getIssuerTimestamp());

		return new ResponseEntity<PCCResponseDTO>(rersponseDTO, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Function that send request to issuer bank and return response
	 */
	public ResponseEntity<PCCResponseDTO> sendIssuerRequest(Request request, PCCRequestDTO requestDTO, String url) {

		logger.info("INFO | send issuer request is called function is called");

		request.setStatus(Status.WAITING);
		requestRepository.save(request);

		RestTemplate template = new RestTemplate();
		try {
			logger.info("INFO | sending request to issuer bank");

			ResponseEntity<PCCResponseDTO> response = template.postForEntity(url, requestDTO, PCCResponseDTO.class);

			Request savedRequest = requestRepository.findByAcquirerOrderID(response.getBody().getAcquirerOrderID());

			if (savedRequest == null) {
				logger.error("ERROR | Request is unsuccessfull");
				request.setStatus(Status.UNSUCCESSFULLY);
				requestRepository.save(request);
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			if (response.getBody().getStatus() == Status.SUCCESSFULLY) {
				logger.error("INFO | Request is successfull");
				request.setStatus(Status.SUCCESSFULLY);
			}
			request.setIssuerOrderID(response.getBody().getIssuerOrderID());
			request.setIssuerTimestamp(response.getBody().getIssuerTimestamp());
			requestRepository.save(request);
			return response;

		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				// znaci da zaista nije moguce obaciti transakciju nema nova, pogresni
				// kredencijali, ne postoji taj klijent...
				logger.error("ERROR | Request is failed");
				request.setStatus(Status.FAILURE);

				Gson gson = new Gson(); // Or use new GsonBuilder().create();
				PCCResponseDTO response = gson.fromJson(((HttpClientErrorException) e).getResponseBodyAsString(),
						PCCResponseDTO.class); // deserializes json into target2

				request.setIssuerOrderID(response.getIssuerOrderID());
				request.setIssuerTimestamp(response.getIssuerTimestamp());
				requestRepository.save(request);

				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			} else {
				logger.error("ERROR | issuer is not avialable");
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			logger.error("ERROR | issuer is not avialable");
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@Scheduled(initialDelay = 1800000, fixedRate = 1800000)
	public void checkWaitingTransactions() {
		List<Request> requests = requestRepository.findAllByStatus(Status.WAITING);
		for (Request r : requests) {
			String url=r.getCustomerBank().getBankURL()+"/returnMonay";
			
			//sending request to return money
			RestTemplate template = new RestTemplate();
			try {
				String paymentId=r.getPaymentId();
				ResponseEntity<Boolean> response = template.postForEntity(url, paymentId, Boolean.class);
				if(response.getBody()) {
					r.setStatus(Status.CANCELED);
					requestRepository.save(r);
				}
			}catch (Exception e) {
				logger.error("ERROR| Buyer bank return bad request");
			}
		}
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Boolean returnMonay(String paymentId) {
		logger.info("INFO | Returning monay starts");
		Request r=findRequestByPaymentId(paymentId);
		RestTemplate template = new RestTemplate();
		String url=r.getCustomerBank().getBankURL()+"/returnMonay";
		try {
			ResponseEntity<Boolean> response = template.postForEntity(url, paymentId, Boolean.class);
			if(response.getBody()) {
				r.setStatus(Status.CANCELED);
				requestRepository.save(r);
				return true;
			}
			return false;
		}catch (Exception e) {
			logger.error("ERROR| Buyer bank return bad request");
			return false;
		}
	}
	
	public Request findRequestByPaymentId(String paymentId) {
		List<Request>requests=requestRepository.findAll();
		for(Request r:requests) {
			String paymentIdFromDB=crypto.convertToEntityAttribute(r.getPaymentId());
			if(paymentIdFromDB.equals(paymentId)) {
				return r;
			}
		}
		return null;
	}
	
}
