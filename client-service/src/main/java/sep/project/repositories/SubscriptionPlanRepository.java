package sep.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import sep.project.model.Seller;
import sep.project.model.SubscriptionPlan;

public interface SubscriptionPlanRepository  extends JpaRepository<SubscriptionPlan, Long>, JpaSpecificationExecutor<SubscriptionPlan>{
	
	public List<SubscriptionPlan> findBySeller(Seller seller);
}
