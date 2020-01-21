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
		if (!acquirerService.validate(request)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		PaymentInfo info = acquirerService.createPaymentInfo(request);
		
		//if seller does not exists PaymentInfo is null
		if(info==null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(BankFrontAddress + "/form/" + info.getPaymentToken());
	}

	@PostMapping(value = "/pay/{token}")
	public ResponseEntity<UrlDTO> postPaymentForm(HttpServletResponse httpServletResponse, @PathVariable String token,
			@RequestBody BuyerDTO buyerDTO) {

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentToken(token);
		if (paymentInfo == null) {
			System.out.println("paymentInfo is null");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			System.out.println("transaction is null");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (acquirerService.checkCredentials(token, buyerDTO)) {
			try {
				System.out.println("checking crecentials");
				String location = acquirerService.tryPayment(token, buyerDTO, httpServletResponse).getBody();
				UrlDTO retVal=new UrlDTO(location);
				return ResponseEntity.ok(retVal);
			} catch (InvalidDataException e) {
				System.out.println("invalid data exception");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} catch (PaymentException e) {
				System.out.println("payment exception");
				acquirerService.paymentFailed(paymentInfo, t);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} catch (NoEnoughFundException e) {
				System.out.println("no enoygh money");
				String location = acquirerService.paymentFailed(paymentInfo, t);
				UrlDTO retVal=new UrlDTO(location);
				return ResponseEntity.ok(retVal);
			}
		} else {
			System.out.println("credential is not valid");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}