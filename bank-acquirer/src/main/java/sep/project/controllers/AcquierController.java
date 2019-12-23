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
import sep.project.DTOs.PayResponseDTO;
import sep.project.customExceptions.InvalidDataException;
import sep.project.customExceptions.NoEnoughFundException;
import sep.project.customExceptions.PaymentException;
import sep.project.model.PaymentInfo;
import sep.project.model.Transaction;
import sep.project.repository.PaymentInfoRepository;
import sep.project.services.AcquirerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcquierController {

	private static String BankAddress;

	@Value("${BankAddress}")
	public void setBank1URL(String bankAdress) {
		BankAddress = bankAdress;
	}

	@Autowired
	AcquirerService acquirerService;

	@Autowired
	PaymentInfoRepository paymentInfoRepository;

	@PostMapping(value = "/firstRequest")
	public ResponseEntity<KPResponseDTO> initiatePayment(@RequestBody KPRequestDTO request) {

		System.out.println("DEBUG: initiatePayment called");
		KPResponseDTO retVal = new KPResponseDTO();
		if (!acquirerService.validate(request)) {
			retVal.setMessage("MerchantID or MerchantPass is not valid");
			return new ResponseEntity<KPResponseDTO>(retVal, HttpStatus.BAD_REQUEST);
		}

		PaymentInfo info = acquirerService.createPaymentInfo(request);
		retVal.setPaymentURL(BankAddress + "/pay/" + info.getPaymentURL());
		retVal.setPaymentID(info.getPaymentID().toString());
		retVal.setTransactionId(info.getTransaction().getId().toString());

		return new ResponseEntity<KPResponseDTO>(retVal, HttpStatus.OK);
	}

	/*
	 * @PostMapping(value = "/pccReply") public void pccReply(@RequestBody
	 * PCCResponseDTO pccResponseDTO) {
	 * System.out.println("DEBUG: pccReplay called");
	 * acquirerService.finalizePayment(pccResponseDTO); }
	 */

	@PostMapping(value = "/pay/{url}")
	public ResponseEntity<PayResponseDTO> postPaymentForm(HttpServletResponse httpServletResponse,
			@PathVariable String url, @RequestBody BuyerDTO buyerDTO) {

		PayResponseDTO response = new PayResponseDTO();

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(url);
		if (paymentInfo == null) {
			response.setLocation("/failed");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			response.setLocation("/failed");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		if (acquirerService.checkCredentials(url, buyerDTO)) {
			try {
				return acquirerService.tryPayment(url, buyerDTO, httpServletResponse);
			} catch (PaymentException e) {
				acquirerService.paymentFailed(paymentInfo, t, url, buyerDTO);
				response.setLocation("/failed");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

			} catch (InvalidDataException e) {
				response.setMessage("Data are not valid!");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

			} catch (NoEnoughFundException e) {
				String location = acquirerService.paymentFailed(paymentInfo, t, url, buyerDTO);
				response.setLocation(location);
				System.out.println(location);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		} else {
			response.setMessage("Data are not valid!");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
	}

}