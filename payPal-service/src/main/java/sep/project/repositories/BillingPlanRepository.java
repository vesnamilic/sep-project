package sep.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import sep.project.model.BillingPlan;

public interface BillingPlanRepository extends JpaRepository<BillingPlan, Long>, JpaSpecificationExecutor<BillingPlan> {

}
