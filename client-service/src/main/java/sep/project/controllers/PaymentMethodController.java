package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.model.PaymentMethod;
import sep.project.services.PaymentMethodService;

@RestController
@RequestMapping(value = "/paymentmethod", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentMethodController {
	
	@Autowired
	private PaymentMethodService paymentMethodService;
	
	@PostMapping("")
	public ResponseEntity<?> addPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
		PaymentMethod newPaymentMethod = paymentMethodService.save(paymentMethod);
		return (newPaymentMethod != null) ? new ResponseEntity<>(newPaymentMethod, HttpStatus.CREATED) : ResponseEntity.status(400).build();
	}
	
	@GetMapping("")
	public String addPaymentMethod() {
		
		return "poyyy";
	}

}
