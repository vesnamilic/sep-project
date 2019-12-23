package sep.project.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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

import sep.project.model.PaymentMethod;
import sep.project.model.Seller;
import sep.project.services.SellerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/seller", produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerController {

	@Autowired
	private SellerService sellerService;

	/**
	 * Dodavanje novog klijenta (novog prodavca) u KP
	 */
	@PostMapping("")
	public ResponseEntity<?> addSeller(@RequestBody Seller seller) {
		Seller newSeller = sellerService.addSeller(seller);

		System.out.println("Creating a new seller: " + seller.getName());

		return (newSeller != null) ? new ResponseEntity<>(newSeller, HttpStatus.CREATED)
				: ResponseEntity.status(400).build();
	}

	/**
	 * Dodavanje novog načina plaćanja postojećem klijentu KP-a
	 */
	@PutMapping("/paymentmethod/{sellerId}")
	public ResponseEntity<?> addPaymentMethod(@PathVariable Long sellerId, @RequestBody PaymentMethod paymentMethod) {

		Seller seller = sellerService.addPayment(sellerId, paymentMethod);

		return (seller != null) ? ResponseEntity.status(200).build() : ResponseEntity.status(400).build();
	}

	/**
	 * Preuzimanje omogućenih načina plaćanja za postojećeg klijenta KP-a
	 */
	@GetMapping("/paymentmethod/{sellerId}")
	public ResponseEntity<?> getPaymentMethods(@PathVariable Long sellerId) {

		Set<PaymentMethod> paymentMethods = sellerService.getPayments(sellerId);

		return (paymentMethods != null) ? new ResponseEntity<>(paymentMethods, HttpStatus.OK) : ResponseEntity.status(400).build();
	}

	@GetMapping("/nesto")
	public String proba() {

		return "proba";
	}

}
