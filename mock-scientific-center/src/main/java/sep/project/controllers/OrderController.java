package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.OrderInformationDTO;
import sep.project.dto.OrderResponseDTO;
import sep.project.dto.RedirectDTO;
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

	@Value("https://localhost:4203/success")
	private String successUrl;

	@Value("https://localhost:4203/cancel")
	private String failedUrl;

	@Value("https://localhost:4203/error")
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

		orderInformationDTO.setErrorUrl(this.errorUrl + "?id=" + userOrder.getId());
		orderInformationDTO.setFailedUrl(this.failedUrl+ "?id=" + userOrder.getId());
		orderInformationDTO.setSuccessUrl(this.successUrl+ "?id=" + userOrder.getId());

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
		this.userOrderService.save(userOrder);
		System.out.println("VEKICA");
		RedirectDTO redirectDTO = new RedirectDTO();
		redirectDTO.setUrl(response.getBody().getRedirectUrl());
		return ResponseEntity.ok(redirectDTO);
	}
	
	@GetMapping("/success")
	private ResponseEntity<?> successfulOrder(@RequestParam("id") Long id) {
		UserOrder userOrder = this.userOrderService.getUserOrder(id);
		userOrder.setOrderStatus(OrderStatus.SUCCEEDED);
		this.userOrderService.save(userOrder);
		RedirectDTO redirectDTO = new RedirectDTO();
		redirectDTO.setUrl("https://localhost:4203/success");
		return ResponseEntity.ok(redirectDTO);
		
	}
	
	@GetMapping("/failed")
	private ResponseEntity<?> failedOrder(@RequestParam("id") Long id) {
		UserOrder userOrder = this.userOrderService.getUserOrder(id);
		userOrder.setOrderStatus(OrderStatus.FAILED);
		this.userOrderService.save(userOrder);
		
		RedirectDTO redirectDTO = new RedirectDTO();
		redirectDTO.setUrl("https://localhost:4203/cancel");
		return ResponseEntity.ok(redirectDTO);
		
	}
	
	@GetMapping("/error")
	private ResponseEntity<?> errorOrder(@RequestParam("id") Long id) {
		UserOrder userOrder = this.userOrderService.getUserOrder(id);
		userOrder.setOrderStatus(OrderStatus.ERROR);
		this.userOrderService.save(userOrder);
		RedirectDTO redirectDTO = new RedirectDTO();
		redirectDTO.setUrl("https://localhost:4203/error");
		return ResponseEntity.ok(redirectDTO);
		
	}

}
