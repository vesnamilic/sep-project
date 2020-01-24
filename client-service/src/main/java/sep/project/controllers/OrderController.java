package sep.project.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

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
	private ResponseEntity<?> createOrder(@RequestBody @Valid OrderInformationDTO order) {
		
		logger.info("INITIATED | Adding a new order | Seller's email: " + order.getEmail());
		
		Seller seller = this.sellersService.getSeller(order.getEmail());

		if (seller == null) {
			logger.error("CANCELED | Finding a seller based on the given email address | Email: " + order.getEmail());
			return ResponseEntity.status(400).body("There is no seller registred with the given email address");
		}
		UserOrder createdOrder = this.orderService.createOrder(order, seller);
		createdOrder = this.orderService.saveOrder(createdOrder);

		// TODO: Dodati poruku korisniku
		if (createdOrder == null) {
			logger.error("CANCELED | Saving user order | Seller's email: " + order.getEmail());
			return ResponseEntity.status(400).body("");
		}
		
		logger.info("COMPLETED | Order created | Order id: " + createdOrder.getId());
		return ResponseEntity.ok(createdOrder.getUuid());
	}
	
  
	@PutMapping("/complete/{orderUUID}")
	private ResponseEntity<?> sendOrder(@PathVariable String orderUUID, @RequestBody String paymentMethodName) {

		logger.info("INITIATED | Sending order to payment service | Order uuid: " + orderUUID + ", Payment method: " + paymentMethodName);
		
		UserOrder order = this.orderService.findOrderByUUID(orderUUID);

		if (order == null) {
			logger.error("CANCELED | Finding an order based on the given id |  Order uuid: " + orderUUID);
			return ResponseEntity.status(400).body("The order with given id doesn't exist.");
		}
		
		if(this.orderService.isExpired(order.getExpirationDate())) {
			logger.error("CANCELED | Checking if the order is expired |  Order uuid: " + orderUUID);
			return ResponseEntity.status(400).body("The order with given id is expired exist.");
		}

		PaymentMethod paymentMethod = this.paymentMethodService.getByName(paymentMethodName);

		if (paymentMethod == null) {
			logger.error("CANCELED | Finding the payment method |  Payment method: " + paymentMethodName);
			return ResponseEntity.status(400).body("Selected payment method doesn't exist");
		}
		
		// Slanje narudzbine servisu za placanje

		OrderInformationDTO orderDTO = new OrderInformationDTO(order);
		HttpEntity<OrderInformationDTO> request = new HttpEntity<>(orderDTO);
		System.out.println(paymentMethodName);
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(this.paymentMethodsRedirectURL + paymentMethodName.toLowerCase() + "/create", HttpMethod.POST, request, String.class);
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			logger.error("CANCELED | Sending the request to payment method service |  Payment method: " + paymentMethod);
			return ResponseEntity.status(400).body("An error occurred while trying to contact the payment microservice!");
		}
		
		
		PaymentResponse url = new PaymentResponse();
		url.setUrl(response.getBody());

		logger.info("COMPLETED | Sending order to payment service | Order uuid: " + order.getUuid() + ", Payment method: " + paymentMethodName);

		
		return ResponseEntity.ok(url);
		
	}

	@GetMapping("/paymentMethods/{uuid}")
	private ResponseEntity<?> getOrdersPossiblePaymentMethod(@PathVariable String uuid) {
		
		logger.info("INITIATED | Getting all available payment methods for an order | Order uuid: " + uuid);
		
		UserOrder order = this.orderService.findOrderByUUID(uuid);
		
		if (order == null) {
			logger.error("CANCELED | Finding an order based on the given id |  Order uuid: " + uuid);
			return ResponseEntity.status(400).body("The order with given id doesn't exist.");
		}
		
		if(this.orderService.isExpired(order.getExpirationDate())) {
			logger.error("CANCELED | Checking if the order is expired |  Order uuid: " + uuid);
			return ResponseEntity.status(400).body("The order with given id is expired exist.");
		}

		Seller seller = order.getSeller();
		Set<PaymentMethod> paymentMethods = this.sellersService.getPayments(seller.getEmail());

		if (paymentMethods != null) {
			Set<String> payments = paymentMethods.stream().map(payment -> payment.getName()).collect(Collectors.toSet());
			logger.info("COMPLETED | Getting all available payment methods for an existing client | Email: " + seller.getEmail());
			return new ResponseEntity<>(payments, HttpStatus.OK);
		} else {
			logger.error("CANCELED | Getting all available payment methods for an existing client | Email: " + seller.getEmail());
			return ResponseEntity.status(400).build();
		}
	}
}
