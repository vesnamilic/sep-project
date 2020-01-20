package sep.project.services;

import java.util.Date;

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

	/**
	 * If request exsists for this order return null, else create request
	 */
	public Request checkRequest(PCCRequestDTO requestDTO) {
		Request request = requestRepository.findByAcquirerOrderID(requestDTO.getAcquirerOrderID());
		if (request == null) {
			return createRequest(requestDTO);
		} else {
			return null;
		}
	}

	/**
	 * Crete request from RequestDTO
	 */
	public Request createRequest(PCCRequestDTO requestDTO) {
		Bank customerBank = bankRepository.findByBankNumber(requestDTO.getBuyerPan().substring(0, 6));
		Bank sellerBank = bankRepository.findByBankNumber(requestDTO.getSellerBankNumber());
		Request request = new Request();
		request.setStatus(Status.CREATED);
		request.setAcquirerOrderID(requestDTO.getAcquirerOrderID());
		request.setAcquirerTimestamp(requestDTO.getAcquirerTimestamp());
		request.setCustomerBank(customerBank);
		request.setSellerBank(sellerBank);
		request.setCreateTime(new Date(System.currentTimeMillis()));
		request.setReturnURL(requestDTO.getReturnURL());
		request.setMerchantOrderId(requestDTO.getMerchantOrderID());
		return request;
	}

	/**
	 * Find bank by bank number
	 */
	public Bank getBankByPan(String pan) {
		String bankNumber = pan.substring(0, 6);
		return bankRepository.findByBankNumber(bankNumber);
	}

	/**
	 * Function that make PCCResponse object for failiour respose
	 */
	public ResponseEntity<PCCResponseDTO> makeFiliureResponse(Request request) {
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

		RestTemplate template = new RestTemplate();
		try {
			ResponseEntity<PCCResponseDTO> response = template.postForEntity(url, requestDTO, PCCResponseDTO.class);

			Request savedRequest = requestRepository.findByAcquirerOrderID(response.getBody().getAcquirerOrderID());
			
			if (savedRequest == null) {
				request.setStatus(Status.FAILED_B2_DATA);
				requestRepository.save(request);
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			
			if (response.getBody().getStatus() == Status.FAILURE) {
				request.setStatus(Status.FAILURE);
			} 
			else {
				request.setStatus(Status.SUCCESSFUL);
			}

			requestRepository.save(request);

			return response;

		} catch (Exception e) {
			System.out.println("Error in sending request to ISSUER.");
			request.setStatus(Status.FAILURE_SENDING_TO_B2);
			requestRepository.save(request);
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
}
