package sep.project.controllers;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import sep.project.DTOs.UrlDTO;
import sep.project.customExceptions.InvalidDataException;
import sep.project.customExceptions.NoEnoughFundException;
import sep.project.customExceptions.PaymentException;
import sep.project.enums.Status;
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

	private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

	@PostMapping(value = "/firstRequest")
	public ResponseEntity<KPResponseDTO> initiatePayment(@RequestBody KPRequestDTO request) {
		logger.info("INFO | initiatePayment started");

		if (!acquirerService.validate(request)) {
			logger.error("ERROR | data is not valid");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		PaymentInfo info = acquirerService.createPaymentInfo(request);

		// if seller does not exists PaymentInfo is null
		if (info == null) {
			logger.error("ERROR | paymentInfo does not exists");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		KPResponseDTO response=new KPResponseDTO();
		response.setPaymentID(info.getPaymentId());
		response.setPaymentURL(BankFrontAddress + "/form/");
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/pay/{paymentId}")
	public ResponseEntity<UrlDTO> postPaymentForm(HttpServletResponse httpServletResponse, @PathVariable String paymentId,
			@RequestBody BuyerDTO buyerDTO) {
		logger.info("INFO | postPaymentForm started");

		PaymentInfo paymentInfo = paymentInfoRepository.findByPaymentId(paymentId);
		if (paymentInfo == null) {
			logger.error("ERROR | paymentInfo does not exists");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Transaction t = paymentInfo.getTransaction();
		if (t == null) {
			logger.error("ERROR | transaction does not exists");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if(t.getStatus()==Status.SUCCESSFULLY) {
			logger.error("ERROR | transaction is already finisheded");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if (acquirerService.checkCredentials(paymentId, buyerDTO)) {
			try {

				String location = acquirerService.tryPayment(paymentId, buyerDTO, httpServletResponse).getBody();
				UrlDTO retVal = new UrlDTO(location);

				if (location.equals(t.getFailedURL()) || location.equals(t.getErrorURL())) {
					logger.info("INFO | location is failed or error");
					return ResponseEntity.badRequest().body(retVal);
				}

				logger.info("INFO | location is success");
				return ResponseEntity.ok(retVal);
				
			} catch (InvalidDataException e) {
				logger.error("ERROR | Data is not valid");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} catch (PaymentException e) {
				logger.error("ERROR | payment failed");
				acquirerService.paymentFailed(paymentInfo, t);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			} catch (NoEnoughFundException e) {
				logger.error("ERROR | No enough money");
				acquirerService.paymentFailed(paymentInfo, t);
				UrlDTO retVal = new UrlDTO(t.getFailedURL());
				return ResponseEntity.badRequest().body(retVal);
			}
		} else {
			logger.error("ERROR | credentials is not valid");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping(value = "/checkTransaction")
	public Status checkTransaction(@RequestBody String request) {
		logger.info("INFO | retrunMonay is called");
		return acquirerService.checkTransaction(request);
	}


}