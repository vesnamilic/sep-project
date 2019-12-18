package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import sep.project.DTOs.PCCRequestDTO;
import sep.project.services.IssuerService;

@RestController
public class IssuerController {

	@Autowired
    private IssuerService issuerService;

	@PostMapping(value = "/paymentRequest")
	public void request(@RequestBody PCCRequestDTO request) {
		issuerService.checkPayment(request);
	}

}
