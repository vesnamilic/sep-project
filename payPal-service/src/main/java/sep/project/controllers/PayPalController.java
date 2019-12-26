package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sep.project.dto.CreatePaymentDTO;
import sep.project.services.PayPalService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class PayPalController {

    private final PayPalService payPalService;   

    @Autowired
    PayPalController(PayPalService payPalService){
        this.payPalService = payPalService;
    }

    /**
     * Creating a new PayPal transaction
     */
    @PostMapping(value = "/create")
    public ResponseEntity<?> makePayment(@RequestBody CreatePaymentDTO paymentDTO){  	
        
    	ResponseEntity<?> response = payPalService.createPayment(paymentDTO);
        
        return response;

    }
    
    /**
     * Completing an existing PayPal transaction
     */
    @GetMapping(value = "/complete")
    public ResponseEntity<?> completePayment(@RequestParam String email, @RequestParam String paymentId, @RequestParam String token, @RequestParam String PayerID){ 
    	
    	ResponseEntity<?> result = payPalService.completePayment(email, paymentId, token, PayerID);
        
        return result;
    }
    
	@GetMapping("/nesto")
	public String proba() {
		
		return "proba";
	}
}
