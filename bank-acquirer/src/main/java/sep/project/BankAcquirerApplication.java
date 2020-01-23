package sep.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BankAcquirerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankAcquirerApplication.class, args);
	}

}
