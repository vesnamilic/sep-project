package sep.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import sep.project.model.PaymentMethod;
import sep.project.model.Seller;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long>, JpaSpecificationExecutor<PaymentMethod> {
	
	List<PaymentMethod> findByDeleted(Boolean deleted);
	
	PaymentMethod findByIdAndDeleted(Long id, Boolean deleted);
	
	PaymentMethod findByDeletedAndNameIgnoreCase(Boolean deleted, String name);
	
	List<PaymentMethod> findBySubscriptionAndDeleted(Boolean subscription, Boolean deleted);
	@Query("select  pm from Seller as sel inner join sel.paymentMethods as pm"
		     + " where sel = ?1 and pm.subscription = ?2 and pm.deleted = ?3")
	List<PaymentMethod> findBySellerAndSubscriptionAndDeleted(Seller seller, Boolean subscription, Boolean deleted);

}
