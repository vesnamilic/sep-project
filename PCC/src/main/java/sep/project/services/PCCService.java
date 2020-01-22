package sep.project.services;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sep.project.DTOs.PCCRequestDTO;
import sep.project.DTOs.PCCResponseDTO;
import sep.project.enums.Status;
import sep.project.model.Bank;
import sep.project.model.Request;
import sep.project.repository.BankRepository;
import sep.project.repository.RequestRepository;

@Service
public class PCCService {

	@Autowired
	private RequestRepository requestRepository;

	@Autowired
	private BankRepository bankRepository;

	private static final Logger logger = LoggerFactory.getLogger(PCCService.class);

	/**
	 * If request exsists for this order return null, else create request
	 */
	public Request checkRequest(PCCRequestDTO requestDTO) {
		logger.info("INFO | check request function called");
		Request request = requestRepository.findByAcquirerOrderID(requestDTO.getAcquirerOrderID());
		if (request == null) {
			logger.info("INFO | Request doest not exists, creating request...");
			return createRequest(requestDTO);
		} else {
			logger.error("ERROR | Request already exist");
			return null;
		}
	}

	/**
	 * Crete request from RequestDTO
	 */
	public Request createRequest(PCCRequestDTO requestDTO) {
		logger.info("INFO | create request function called");
		Bank customerBank = bankRepository.findByBankNumber(requestDTO.getBuyerPan().substring(0, 6));
		Bank sellerBank = bankRepository.findByBankNumber(requestDTO.getSellerBankNumber());
		Request request = new Request();
		request.setStatus(Status.CREATED);
		request.setAcquirerOrderID(requestDTO.getAcquirerOrderID());
		request.setAcquirerTimestamp(requestDTO.getAcquirerTimestamp());
		request.setCustomerBank(customerBank);
		request.setSellerBank(sellerBank);
		request.setCreateTime(new Date(System.currentTimeMillis()));
		request.setMerchantOrderId(requestDTO.getMerchantOrderID());
		requestRepository.save(request);
		return request;
	}

	/**
	 * Find bank by bank number
	 */
	public Bank getBankByPan(String pan) {
		logger.info("INFO | get bank form pan function is called");
		String bankNumber = pan.substring(0, 6);
		return bankRepository.findByBankNumber(bankNumber);
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
		return new ResponseEntity<PCCResponseDTO>(rersponseDTO, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Function that send request to issuer bank and return response
	 */
	public ResponseEntity<PCCResponseDTO> sendIssuerRequest(Request request, PCCRequestDTO requestDTO, String url) {

		logger.info("INFO | send issuer request is called function is called");

		RestTemplate template = new RestTemplate();
		try {
			logger.info("INFO | sending request to issuer bank");

			ResponseEntity<PCCResponseDTO> response = template.postForEntity(url, requestDTO, PCCResponseDTO.class);

			if(response.getStatusCode()==HttpStatus.BAD_REQUEST) {
				logger.info("INFO | issuer bank return bad request.");
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			Request savedRequest = requestRepository.findByAcquirerOrderID(response.getBody().getAcquirerOrderID());

			if (savedRequest == null) {
				logger.error("ERROR | Request does not exists in PCC.");
				request.setStatus(Status.FAILED_B2_DATA);
				requestRepository.save(request);
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			
			if (response.getBody().getStatus() == Status.UNSUCCESSFULLY) {
				logger.info("INFO | issuer bank return status UNSUCCESSFULLY");
				request.setStatus(Status.FAILURE);
			} 
			else {
				logger.info("INFO | issuer bank return status SUCCESSFULLY");
				request.setStatus(Status.SUCCESSFULLY);
			}

			request.setIssuerOrderID(response.getBody().getIssuerOrderID());
			request.setIssuerTimestamp(response.getBody().getIssuerTimestamp());
			requestRepository.save(request);

			if (response.getBody().getStatus() == Status.UNSUCCESSFULLY) {
				logger.info("INFO | issuer bank return bad request, sending bad request response to acquierer bank.");
				return  new ResponseEntity<>(response.getBody(), HttpStatus.BAD_REQUEST);
			}
			return response;

		} catch (Exception e) {
			logger.error("ERROR | issuer is not avialable");
			request.setStatus(Status.FAILURE_SENDING_TO_B2);
			requestRepository.save(request);
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
}
