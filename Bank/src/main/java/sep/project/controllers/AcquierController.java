package sep.project.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.DTOs.BuyerDTO;
import sep.project.DTOs.KPRequestDTO;
import sep.project.DTOs.PCCResponseDTO;
import sep.project.customExceptions.InvalidDataException;
import sep.project.customExceptions.NoEnoughFundException;
import sep.project.customExceptions.PaymentException;
import sep.project.model.PaymentInfo;
import sep.project.model.Transaction;
import sep.project.repository.PaymentInfoRepository;
import sep.project.services.AcquirerService;

@RestController
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
	public ResponseEntity<Map<String, String>> initiatePayment(@RequestBody KPRequestDTO request) {

		System.out.println("DEBUG: initiatePayment called");
		Map<String, String> retVal = new HashMap<String, String>();

		if (!acquirerService.validate(request)) {
			retVal.put("message", "MerchantID or MerchantPass is not valid");
			return new ResponseEntity<Map<String, String>>(retVal, HttpStatus.BAD_REQUEST);
		}

		PaymentInfo info = acquirerService.createPaymentInfo(request);
		retVal.put("paymentURL", BankAddress + "pay/" + info.getPaymentURL());
		retVal.put("paymentID", info.getPaymentID().toString());
		retVal.put("transactionId", info.getTransaction().getId().toString());

		return new ResponseEntity<Map<String, String>>(retVal, HttpStatus.OK);
	}

	@PostMapping(value = "/pccReply")
	public void pccReply(@RequestBody PCCResponseDTO pccResponseDTO) {
		System.out.println("DEBUG: pccReplay called");
		acquirerService.finalizePayment(pccResponseDTO);
	}

	@PostMapping(value = "/pay/{url}")
	public ResponseEntity<Map<String, String>> postPaymentForm(HttpServletResponse httpServletResponse,
			@PathVariable String url, @RequestBody BuyerDTO buyerDTO) {
		Map<String, String> map = new HashMap<>();

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentURL(url);
		if (paymentInfo == null) {
			map.put("Location", "/failed");
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			map.put("Location", "/failed");
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
		}

		if (acquirerService.checkCredentials(url, buyerDTO)) {
			try {
				return acquirerService.tryPayment(url, buyerDTO, httpServletResponse);
			} catch (PaymentException e) {
				acquirerService.paymentFailed(paymentInfo, t, url, buyerDTO);
				map.put("Location", "/failed");
				return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
			} catch (InvalidDataException e) {
				map.put("Poruka", "Data are not valid!");
				return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
			} catch (NoEnoughFundException e) {
				String location = acquirerService.paymentFailed(paymentInfo, t, url, buyerDTO);
				HttpHeaders headers = new HttpHeaders();
				headers.add("Location", location);
				headers.add("Access-Control-Allow-Origin", "*");
				map.put("Location", location);
				System.out.println(location);
				return new ResponseEntity<>(map, headers, HttpStatus.BAD_REQUEST);
			}
		} else {
			map.put("message", "Not valid data.");
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
		}
	}

}