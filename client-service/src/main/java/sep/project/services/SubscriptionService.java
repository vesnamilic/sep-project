package sep.project.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.Subscription;
import sep.project.repositories.SubscriptionRepository;

@Service
public class SubscriptionService {
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
	public Subscription save(Subscription subscription) {
		
		return subscriptionRepository.save(subscription);
	}

	public Subscription findByUuid(String uuid) {
		
		return subscriptionRepository.findByUuid(uuid);
	}
	
	public Boolean isExpired(Date date) {
		return date.before(new Date());
	}
}
