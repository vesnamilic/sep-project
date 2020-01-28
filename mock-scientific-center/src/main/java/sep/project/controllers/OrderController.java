package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.OrderInformationDTO;
import sep.project.dto.OrderResponseDTO;
import sep.project.model.Magazine;
import sep.project.model.OrderStatus;
import sep.project.model.UserOrder;
import sep.project.services.MagazineService;
import sep.project.services.UserOrderService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

	@Autowired
	private MagazineService magazineService;

	@Autowired
	private UserOrderService userOrderService;

	@Autowired
	private RestTemplate restTemplate;

	@Value("https://localhost:4200/")
	private String successUrl;

	@Value("https://localhost:4200/")
	private String failedUrl;

	@Value("https://localhost:4200/")
	private String errorUrl;

	@Value("https://localhost:8762/api/client/orders/create")
	private String kpUrl;

	@PostMapping("/create")
	public ResponseEntity<?> createOrder(@RequestBody OrderInformationDTO orderInformationDTO) {
		// Kada budemo imali login moci cemo da dobijemo korisnika koji je ulogovan i
		// stavimo da on vrsi placanje za sad preskacemo

		Magazine magazine = this.magazineService.findByEmail(orderInformationDTO.getEmail());

		if (magazine == null) {
			return ResponseEntity.status(400).build();
		}

		UserOrder userOrder = new UserOrder();
		userOrder.setMagazine(magazine);
		userOrder.setOrderStatus(OrderStatus.CREATED);
		userOrder.setPaymentAmount(orderInformationDTO.getPaymentAmount());
		userOrder.setPaymentCurrency(orderInformationDTO.getPaymentCurrency());

		userOrder = this.userOrderService.save(userOrder);

		if (userOrder == null) {
			return ResponseEntity.status(500).build();
		}

		orderInformationDTO.setErrorUrl(this.errorUrl);
		orderInformationDTO.setFailedUrl(this.failedUrl);
		orderInformationDTO.setSuccessUrl(this.successUrl);

		HttpEntity<OrderInformationDTO> request = new HttpEntity<>(orderInformationDTO);

		ResponseEntity<OrderResponseDTO> response = null;
		try {
			response = restTemplate.exchange(this.kpUrl, HttpMethod.POST, request, OrderResponseDTO.class);
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			userOrder.setOrderStatus(OrderStatus.ERROR);
			return ResponseEntity.status(400)
					.body("An error occurred while trying to contact the payment microservice!");
		}
		
		userOrder.setUuid(response.getBody().getUuid());

		HttpHeaders headersRedirect = new HttpHeaders();
		headersRedirect.add("Access-Control-Allow-Origin", "*");
		headersRedirect.add("Location", response.getBody().getRedirectUrl());
		return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
	}
	
	@GetMapping("")
	private ResponseEntity<?> get() {
		HttpHeaders headersRedirect = new HttpHeaders();
		headersRedirect.add("Access-Control-Allow-Origin", "*");
		headersRedirect.add("Location", "https://localhost:4203/");
		return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);
	}

}
