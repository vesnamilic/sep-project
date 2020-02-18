package sep.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import sep.project.DTOs.PCCRequestDTO;
import sep.project.DTOs.PCCResponseDTO;
import sep.project.enums.Status;
import sep.project.model.Bank;
import sep.project.model.Request;
import sep.project.repository.RequestRepository;
import sep.project.services.PCCService;

@RestController
public class PCCController {

	@Autowired
	private PCCService pccService;

	@Autowired
	private RequestRepository requestRepository;

	private static final Logger logger = LoggerFactory.getLogger(PCCController.class);

	@PostMapping(value = "/request")
	public ResponseEntity<PCCResponseDTO> request(@RequestBody PCCRequestDTO requestDTO) {

		logger.info("INFO | PCC recieved request");

		// proverava da li vec postoji zahtev vezan za ovu transakciju, ako postoji
		// vraca null, ako ne postoji kreira ga
		Request request = pccService.checkRequest(requestDTO);
		if (request == null) {
			logger.error("ERROR | Request denied.");
			return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
		}

		Bank buyerBank = pccService.getBankByPan(requestDTO.getBuyerPan());

		// nema kojoj banci da posalje, vraca odgovor banci prodavca
		if (buyerBank == null) { 
			logger.error("ERROR | Request denied");
			request.setStatus(Status.FAILURE);
			requestRepository.save(request);
			return pccService.makeFiliureResponse(request);
		} 
		
		//salje request banci kupca
		else { 
			logger.info("INFO | Sending request to issuer");
			return pccService.sendIssuerRequest(request, requestDTO, buyerBank.getBankURL());
		}

	}
	
	@PostMapping(value = "/request/returnMonay")
	public Boolean retrunMonay(@RequestBody String request) {
		logger.info("INFO | retrunMonay is called");
		return pccService.returnMonay(request);
	}

}
