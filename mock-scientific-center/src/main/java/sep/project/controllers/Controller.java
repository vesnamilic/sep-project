package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
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

	    System.out.println("NC probna GET metoda");
	    
	    return restTemplate.exchange("https://localhost:8762/api/bitcoin/rest/proba", HttpMethod.GET, null, String.class).getBody();
	}
	
	@GetMapping("/proba2")
	public String probnaMetodaGet2() {
		
		System.out.println("NC 2. probna GET metoda");
		
		return "Dolazim iz naucne centrale";
	}
	
}
