package sep.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import sep.project.model.PaymentMethod;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long>, JpaSpecificationExecutor<PaymentMethod> {
	
	List<PaymentMethod> findByDeleted(Boolean deleted);
	
	PaymentMethod findByIdAndDeleted(Long id, Boolean deleted);
	
	PaymentMethod findByDeletedAndNameIgnoreCase(Boolean deleted, String name);
	
	List<PaymentMethod> findBySubscriptionAndDeleted(Boolean subscription, Boolean deleted);

}
