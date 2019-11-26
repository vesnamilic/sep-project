package sep.project.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( value = "/controller")
public class Controller {

	@PostMapping("/proba")
	public void probnaMetodaPost() {
		System.out.println("do something");
	}

	@GetMapping("/proba")
	public String probnaMetodaGet() {
		return "something";
	}
}
