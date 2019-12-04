package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RefreshScope
@RequestMapping("/rest")
@RestController
public class Message {
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${message: Default Hello}")
    private String message;

    @GetMapping("/message")
    public String message() {
        return message;
    }
    
    @GetMapping("/proba")
    public String probnaMetodaGet() {
    	
    	System.out.println("KP probna GET metoda");  
    	
	    return restTemplate.exchange("https://localhost:9898/controller/proba", HttpMethod.GET, null, String.class).getBody();
    }
    
	@GetMapping("/proba2")
	public String probnaMetodaGet2() {

	    System.out.println("KP 2. probna GET metoda");
	    
	    return restTemplate.exchange("https://localhost:9897/controller/proba2", HttpMethod.GET, null, String.class).getBody();
	}
}
