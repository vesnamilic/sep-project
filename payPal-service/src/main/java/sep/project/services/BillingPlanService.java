package sep.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.BillingPlan;
import sep.project.repositories.BillingPlanRepository;

@Service
public class BillingPlanService {
	
	@Autowired
	BillingPlanRepository billingPlanRepository;
	
	public List<BillingPlan> findAll(){
		
		return billingPlanRepository.findAll();
	}
	
	public BillingPlan save(BillingPlan billingPlan) {
		
		return billingPlanRepository.save(billingPlan);
	}
	
	public BillingPlan getOne(Long id) {
		
		return billingPlanRepository.getOne(id);
	}

}
