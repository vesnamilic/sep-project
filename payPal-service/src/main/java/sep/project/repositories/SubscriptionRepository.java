package sep.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import sep.project.model.Client;
import sep.project.model.Subscription;
import sep.project.model.SubscriptionStatus;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, JpaSpecificationExecutor<Subscription>{
	
	List<Subscription> findByClientAndStatusIn(Client client, List<SubscriptionStatus> subscriptionStatuses);
	
	@Query("SELECT distinct sub FROM Subscription sub inner join sub.client as m WHERE sub.subscriptionId = ?1 and m.email = ?2 ")
	public Subscription findMerchantSubscriptionBasedOnId(Long id, String email);
}
