package sep.project.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import sep.project.model.Subscription;
import sep.project.model.SubscriptionStatus;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, JpaSpecificationExecutor<Subscription>{

	Subscription findByUuid(String uuid);
	
	@Query("SELECT distinct o from Subscription as o WHERE o.subscriptionId = ?1 and o.seller.email = ?2 ")
	Subscription findClientSubscriptionBySubscriptionId(Long orderId, String email);

	@Query("SELECT distinct o from Subscription as o WHERE o.expirationDate < ?1 and o.subscriptionStatus = 'INITIATED'")
	List<Subscription> findExpiredSubscriptions(Date today);
	
	List<Subscription> findBySubscriptionStatusIn(List<SubscriptionStatus> list);
}
