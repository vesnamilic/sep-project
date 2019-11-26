package sep.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
public class MockBitcoinServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockBitcoinServiceApplication.class, args);
	}

    @Bean
    public RestTemplate getRestTemplate() {
    	return new RestTemplate();
    }

}
