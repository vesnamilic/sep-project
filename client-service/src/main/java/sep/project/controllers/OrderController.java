package sep.project.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.OrderInformationDTO;
import sep.project.dto.PaymentResponse;
import sep.project.model.PaymentMethod;
import sep.project.model.Seller;
import sep.project.model.UserOrder;
import sep.project.services.OrderService;
import sep.project.services.PaymentMethodService;
import sep.project.services.SellerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;

	@Autowired
	private SellerService sellersService;

	@Autowired
	private PaymentMethodService paymentMethodService;

	@Autowired
	private RestTemplate restTemplate;

	@Value("https://localhost:8762/api/")
	private String paymentMethodsRedirectURL;

	@PostMapping("/create")
	private ResponseEntity<?> createOrder(@RequestBody OrderInformationDTO order) {
		
		logger.info("INITIATED | Adding a new order | Seller's email: " + order.getEmail());
		Seller seller = this.sellersService.getSeller(order.getEmail());

		if (seller == null) {
			logger.error("CANCELED | Finding a seller based on the given email address | Email: "
					+ order.getEmail());
			return ResponseEntity.status(400).body("There is no seller registred with the given email address");
		}

		UserOrder saved = this.orderService
				.saveOrder(new UserOrder(null, order.getPaymentAmount(), order.getPaymentCurrency(), seller));

		// TODO: Dodati poruku korisniku
		if (saved == null) {
			logger.error("CANCELED | Saving user order | Seller's email: " + order.getEmail());
			return ResponseEntity.status(400).body("");
		}
		
		logger.info("COMPLETED | Order created | Order id: " + saved.getId());

		return ResponseEntity.ok(saved.getId());
	}

	@PutMapping("/complete/{orderId}")
	private ResponseEntity<?> sendOrder(@PathVariable Long orderId, @RequestBody String paymentMethodName) {

		logger.info("INITIATED | Sending order to payment service | Order id: " + orderId + ", Payment method: " + paymentMethodName);
		
		UserOrder order = this.orderService.getOrder(orderId);

		if (order == null) {
			logger.error("CANCELED | Finding an order based on the given id |  Order id: " + orderId);
			return ResponseEntity.status(400).body("The order with given id doesn't exist.");
		}

		PaymentMethod paymentMethod = this.paymentMethodService.getByName(paymentMethodName);

		if (paymentMethod == null) {
			logger.error("CANCELED | Finding the payment method |  Payment method: " + paymentMethodName);
			return ResponseEntity.status(400).body("Selected payment method doesn't exist");
		}

		OrderInformationDTO orderDTO = new OrderInformationDTO();
		orderDTO.setPaymentAmount(order.getPaymentAmount());
		orderDTO.setPaymentCurrency(order.getPaymentCurrency());
		orderDTO.setEmail(order.getSeller().getEmail());

		HttpEntity<OrderInformationDTO> request = new HttpEntity<>(orderDTO);
		System.out.println(paymentMethodName);
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(this.paymentMethodsRedirectURL + paymentMethodName + "/create", HttpMethod.POST, request, String.class);
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			logger.error("CANCELED | Sending the request to payment method service |  Payment method: " + paymentMethod);
			return ResponseEntity.status(400).body("An error occurred while trying to contact the payment microservice!");
		}
		// TODO: Pitati
		/*HttpHeaders headersRedirect = new HttpHeaders();
		headersRedirect.add("Location", response.getBody());
		headersRedirect.add("Access-Control-Allow-Origin", "*");
		return new ResponseEntity<byte[]>(null, headersRedirect, HttpStatus.FOUND);*/
		
		PaymentResponse url = new PaymentResponse();
		url.setUrl(response.getBody());

		logger.error("COMPLETED | Sending order to payment service | Order id: " + orderId + ", Payment method: " + paymentMethodName);
		
		return ResponseEntity.ok(url);
	}

	@GetMapping("/paymentMethods/{orderId}")
	private ResponseEntity<?> getOrdersPossiblePaymentMethod(@PathVariable Long orderId) {
		UserOrder order = this.orderService.getOrder(orderId);
		logger.info("INITIATED | Getting all available payment methods for an order | Order id: " + orderId);
		if (order == null) {
			logger.error("CANCELED | Finding an order based on the given id |  Order id: " + orderId);
			return ResponseEntity.status(400).body("The order with given id doesn't exist.");
		}

		Seller seller = order.getSeller();

		Set<PaymentMethod> paymentMethods = this.sellersService.getPayments(seller.getEmail());

		if (paymentMethods != null) {

			Set<String> payments = paymentMethods.stream().map(payment -> payment.getName())
					.collect(Collectors.toSet());
			logger.info("COMPLETED | Getting all available payment methods for an existing client | Email: " + seller.getEmail());
			return new ResponseEntity<>(payments, HttpStatus.OK);
		} else {
			logger.error("CANCELED | Getting all available payment methods for an existing client | Email: " + seller.getEmail());
			return ResponseEntity.status(400).build();
		}
	}
}
