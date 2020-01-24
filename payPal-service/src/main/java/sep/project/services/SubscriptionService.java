package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.Subscription;
import sep.project.repositories.SubscriptionRepository;

@Service
public class SubscriptionService {

	@Autowired
	SubscriptionRepository subscriptionRepository;
	
	public Subscription save(Subscription subscription) {
		
		return subscriptionRepository.save(subscription);
	}
	
	public Subscription findByToken(String token) {
		
		return subscriptionRepository.findByToken(token);
	}
}
