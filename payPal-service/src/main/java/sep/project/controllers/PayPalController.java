package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sep.project.dto.ConfirmPaymentDTO;
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
        
    	boolean result = payPalService.createPayment(paymentDTO);
        
        return result ? ResponseEntity.status(200).build() : ResponseEntity.status(400).build();

    }
    
    /**
     * Completing an existing PayPal transaction
     */
    @PostMapping(value = "/complete")
    public ResponseEntity<?> completePayment(@RequestBody ConfirmPaymentDTO paymentDTO){   	
    	
    	boolean result = payPalService.completePayment(paymentDTO);
        
        return result ? ResponseEntity.status(200).build() : ResponseEntity.status(400).build();
    }
    
	@GetMapping("/nesto")
	public String proba() {
		
		return "proba";
	}
}
