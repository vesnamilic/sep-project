package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.UserSubscription;
import sep.project.repository.UserSubscriptionRepository;

@Service
public class UserSubscriptionService {
	
	@Autowired
	UserSubscriptionRepository userSubscriptionRepository;
	
	public UserSubscription save(UserSubscription subscription) {
		UserSubscription saved = null;
		
		try {
			saved = this.userSubscriptionRepository.save(subscription);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return saved;
	}
}
