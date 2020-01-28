package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.model.UserSubscription;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long>{

}
