package sep.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.OrderInformationDTO;
import sep.project.dto.OrderResponseDTO;
import sep.project.dto.RedirectDTO;
import sep.project.dto.SubscriptionInformationDTO;
import sep.project.model.Magazine;
import sep.project.model.OrderStatus;
import sep.project.model.UserSubscription;
import sep.project.services.MagazineService;
import sep.project.services.UserSubscriptionService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RequestMapping(value = "/subscription", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubscriptionsController {
	
	@Autowired
	private MagazineService magazineService;

	@Autowired
	private UserSubscriptionService userSubscriptionService;

	@Autowired
	private RestTemplate restTemplate;

	@Value("https://localhost:4203/success")
	private String successUrl;

	@Value("https://localhost:4203/cancel")
	private String failedUrl;

	@Value("https://localhost:4203/error")
	private String errorUrl;

	@Value("https://localhost:8762/api/client/subscription/create")
	private String kpUrl;

	@PostMapping("/create")
	public ResponseEntity<?> createSubscription(@RequestBody SubscriptionInformationDTO subscriptionInformationDTO) {
		// Kada budemo imali login moci cemo da dobijemo korisnika koji je ulogovan i
		// stavimo da on vrsi placanje za sad preskacemo

		Magazine magazine = this.magazineService.findByEmail(subscriptionInformationDTO.getEmail());

		if (magazine == null) {
			return ResponseEntity.status(400).build();
		}

		UserSubscription userSubscription = new UserSubscription();
		userSubscription.setMagazine(magazine);
		userSubscription.setSubscriptionStatus(OrderStatus.CREATED);

		userSubscription = this.userSubscriptionService.save(userSubscription);

		if (userSubscription == null) {
			return ResponseEntity.status(500).build();
		}

		subscriptionInformationDTO.setErrorUrl(this.errorUrl);
		subscriptionInformationDTO.setFailedUrl(this.failedUrl);
		subscriptionInformationDTO.setSuccessUrl(this.successUrl);

		HttpEntity<SubscriptionInformationDTO> request = new HttpEntity<>(subscriptionInformationDTO);

		ResponseEntity<OrderResponseDTO> response = null;
		try {
			response = restTemplate.exchange(this.kpUrl, HttpMethod.POST, request, OrderResponseDTO.class);
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			userSubscription.setSubscriptionStatus(OrderStatus.ERROR);
			return ResponseEntity.status(400)
					.body("An error occurred while trying to contact the payment microservice!");
		}
		
		userSubscription.setUuid(response.getBody().getUuid());
		this.userSubscriptionService.save(userSubscription);
		System.out.println("VEKICA");
		RedirectDTO redirectDTO = new RedirectDTO();
		redirectDTO.setUrl(response.getBody().getRedirectUrl());
		return ResponseEntity.ok(redirectDTO);
	}
}
