package sep.project.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.OrderStatusInformationDTO;
import sep.project.model.Subscription;
import sep.project.model.SubscriptionStatus;
import sep.project.repositories.SubscriptionRepository;

@Service
public class SubscriptionService {
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("https://localhost:8762/api/")
	private String paymentMethodsURL;
	
	public Subscription save(Subscription subscription) {
		
		return subscriptionRepository.save(subscription);
	}

	public Subscription findByUuid(String uuid) {
		
		return subscriptionRepository.findByUuid(uuid);
	}
	
	public Subscription findClientSubscriptionBySubscriptionId(String email, Long orderId) {
		return this.subscriptionRepository.findClientSubscriptionBySubscriptionId(orderId, email);
	}
	
	public Boolean isExpired(Date date) {
		return date.before(new Date());
	}
	
	/**
	 * Checking INITIATED and CREATED subscriptions
	 */
	//@Scheduled(initialDelay = 10000, fixedRate = 3600000)
	@Scheduled(initialDelay = 10000, fixedRate = 6000000)
	public void checkOrdersStatus() {
		
		//find all expired subscriptinos
		List<Subscription> subscriptions = this.subscriptionRepository.findExpiredSubscriptions(new Date());
		
		//set their status to EXPIRED
		for (Subscription subscription : subscriptions) {
			subscription.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
			save(subscription);
		}

		//find all created and completed transactions
		List<SubscriptionStatus> list = new ArrayList<SubscriptionStatus>();
		list.add(SubscriptionStatus.COMPLETED);
		list.add(SubscriptionStatus.CREATED);
		
		subscriptions = subscriptionRepository.findBySubscriptionStatusIn(list);

		//send a request to find their status
		for (Subscription subscription : subscriptions) {
			ResponseEntity<OrderStatusInformationDTO> response = null;
			try {
				response = restTemplate.getForEntity(this.paymentMethodsURL+ subscription.getPaymentMethod().toLowerCase() + "/subscription?subscriptionId=" + subscription.getSubscriptionId() + "&email=" + subscription.getSeller().getEmail(), OrderStatusInformationDTO.class);
			} 
			catch (RestClientException e) {

				return;
			}

			SubscriptionStatus subscriptionStatus = SubscriptionStatus.valueOf(response.getBody().getStatus());

			//set the new subscription status
			if (subscriptionStatus != null && !subscriptionStatus.equals(subscription.getSubscriptionStatus())) {
				subscription.setSubscriptionStatus(subscriptionStatus);
				subscriptionRepository.save(subscription);
			}

			return;
		}

	}
}
