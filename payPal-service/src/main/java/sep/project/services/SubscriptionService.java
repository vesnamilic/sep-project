package sep.project.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.Client;
import sep.project.model.Subscription;
import sep.project.model.SubscriptionStatus;
import sep.project.repositories.SubscriptionRepository;

@Service
public class SubscriptionService {

	@Autowired
	SubscriptionRepository subscriptionRepository;
	
	public Subscription save(Subscription subscription) {
		
		return subscriptionRepository.save(subscription);
	}
	
	public Subscription getOne(Long id) {
		
		return subscriptionRepository.getOne(id);
	}
	
	public List<Subscription> getSubscriptionForSynchronizing(Client client) {
		
		List<SubscriptionStatus> list = new ArrayList<SubscriptionStatus>();
		list.add(SubscriptionStatus.COMPLETED);
		
		return subscriptionRepository.findByClientAndStatusIn(client, list);
	}
}
