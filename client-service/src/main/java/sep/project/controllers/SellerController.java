package sep.project.controllers;

import java.util.List;

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

import sep.project.model.Seller;
import sep.project.services.SellerService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/seller", produces = MediaType.APPLICATION_JSON_VALUE)
public class SellerController {
	
	@Autowired
	private SellerService sellerService;
	
	@PostMapping("")
	public ResponseEntity<?> addSeller(@RequestBody Seller seller) {
		Seller newSeller = sellerService.save(seller);
		
		System.out.println("Dodavanje novog prodavca: " + seller.getName());
		
		return (newSeller != null) ? new ResponseEntity<>(newSeller, HttpStatus.CREATED) : ResponseEntity.status(400).build();
	}
	
	@PutMapping("/{sellerId}")
	public ResponseEntity<?> addPaymentMethod(@PathVariable Long sellerId, @RequestBody List<Long> methods){
		
		Seller seller = sellerService.addPayment(sellerId, methods);
		
		return (seller != null) ? new ResponseEntity<>(null, HttpStatus.OK) : ResponseEntity.status(400).build();
	}
	
	@GetMapping("nesto")
	public String proba() {
		
		return "proba";
	}

}
