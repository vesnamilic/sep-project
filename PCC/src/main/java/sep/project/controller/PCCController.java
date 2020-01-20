package sep.project.controller;

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

	@PostMapping(value = "/request")
	public ResponseEntity<PCCResponseDTO> request(@RequestBody PCCRequestDTO requestDTO) {

		System.out.println("PCC recieved request ");

		// proverava da li vec postoji zahtev vezan za ovu transakciju, ako postoji
		// vraca null, ako ne postoji kreira ga
		Request request = pccService.checkRequest(requestDTO);
		if (request == null) {
			System.out.println("Request denied, it already exists for this transaction.");
			return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
		}

		Bank buyerBank = pccService.getBankByPan(requestDTO.getBuyerPan());

		// nema kojoj banci da posalje, vraca odgovor banci prodavca
		if (buyerBank == null) { 
			request.setStatus(Status.FAILURE);
			requestRepository.save(request);
			return pccService.makeFiliureResponse(request);
		} 
		
		//salje request banci kupca
		else { 
			return pccService.sendIssuerRequest(request, requestDTO, buyerBank.getBankURL());
		}

	}

}
