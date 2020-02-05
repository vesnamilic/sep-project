package sep.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.Seller;
import sep.project.model.SubscriptionPlan;
import sep.project.repositories.SubscriptionPlanRepository;

@Service
public class SubscriptionPlanService {
	
	@Autowired
	SubscriptionPlanRepository subscriptionPlanRepository;

	public List<SubscriptionPlan> findBySeller(Seller seller) {
		
		return subscriptionPlanRepository.findBySeller(seller);
	}
	
	public SubscriptionPlan save(SubscriptionPlan subscriptionPlan) {
		
		return subscriptionPlanRepository.save(subscriptionPlan);
	}
	
	public SubscriptionPlan getOne(Long id) {
		
		return subscriptionPlanRepository.getOne(id);
	}
}
