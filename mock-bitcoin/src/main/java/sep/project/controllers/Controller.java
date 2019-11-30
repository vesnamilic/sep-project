package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping( value = "/controller")
public class Controller {
	
	@Autowired
	RestTemplate restTemplate;

	@GetMapping("/proba")
	public String probnaMetodaGet() {
		
		System.out.println("Placanje probna GET metoda");
		
		return "Dolazim iz bitcoin aplikacije";
	}
	
	@GetMapping("/proba2")
	public String probnaMetodaGet2() {

	    System.out.println("Placanje 2. probna GET metoda");
	    
	    return restTemplate.exchange("http://localhost:8762/api/bitcoin/rest/proba2", HttpMethod.GET, null, String.class).getBody();
	}
}
