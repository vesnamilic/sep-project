package sep.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import sep.project.utils.CryptoConverter;

@SpringBootApplication
@EnableScheduling
public class PccApplication {

	public static void main(String[] args) {
		SpringApplication.run(PccApplication.class, args);
	}

}
