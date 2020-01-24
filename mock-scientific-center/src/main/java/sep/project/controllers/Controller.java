package sep.project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import sep.project.model.Magazine;
import sep.project.services.MagazineService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
public class Controller {
	
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	MagazineService magazineService;

	@GetMapping("/magazines")
	public ResponseEntity<List<Magazine>> getMagazines() {
	    System.out.println("getMagazines");
	    return magazineService.getMagazines();
	}
	
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
