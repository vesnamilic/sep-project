package sep.project.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.DTOs.BuyerDTO;
import sep.project.DTOs.KPRequestDTO;
import sep.project.DTOs.KPResponseDTO;
import sep.project.DTOs.PCCResponseDTO;
import sep.project.DTOs.UrlDTO;
import sep.project.customExceptions.InvalidDataException;
import sep.project.customExceptions.NoEnoughFundException;
import sep.project.customExceptions.PaymentException;
import sep.project.model.PaymentInfo;
import sep.project.model.Transaction;
import sep.project.repository.PaymentInfoRepository;
import sep.project.services.AcquirerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcquierController {

	private static String BankFrontAddress;

	@Value("${BankFrontAddress}")
	public void setBank1URL(String bankAdress) {
		BankFrontAddress = bankAdress;
	}

	@Autowired
	AcquirerService acquirerService;

	@Autowired
	PaymentInfoRepository paymentInfoRepository;

	@PostMapping(value = "/firstRequest")
	public ResponseEntity<String> initiatePayment(@RequestBody KPRequestDTO request) {

		System.out.println("DEBUG: initiatePayment called");
		KPResponseDTO retVal = new KPResponseDTO();
		if (!acquirerService.validate(request)) {
			retVal.setMessage("MerchantID or MerchantPass is not valid");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		PaymentInfo info = acquirerService.createPaymentInfo(request);
		
		return ResponseEntity.ok(BankFrontAddress + "/form/" + info.getPaymentURL());
	}

	@PostMapping(value = "/pay/{url}")
	public ResponseEntity<UrlDTO> postPaymentForm(HttpServletResponse httpServletResponse, @PathVariable String url,
			@RequestBody BuyerDTO buyerDTO) {

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(url);
		if (paymentInfo == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (acquirerService.checkCredentials(url, buyerDTO)) {
			try {
				String location = acquirerService.tryPayment(url, buyerDTO, httpServletResponse).getBody();
				UrlDTO retVal=new UrlDTO(location);
				return ResponseEntity.ok(retVal);
			} catch (InvalidDataException e) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} catch (PaymentException e) {
				acquirerService.paymentFailed(paymentInfo, t, url, buyerDTO);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} catch (NoEnoughFundException e) {
				String location = acquirerService.paymentFailed(paymentInfo, t, url, buyerDTO);
				UrlDTO retVal=new UrlDTO(location);
				return ResponseEntity.ok(retVal);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/pccReply")
	public void pccReply(@RequestBody PCCResponseDTO pccResponseDTO) {
		System.out.println("DEBUG: pccReplay called");
		acquirerService.finalizePayment(pccResponseDTO);
	}

}