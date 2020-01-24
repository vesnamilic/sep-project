package sep.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.enums.Status;
import sep.project.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {

	Request findByAcquirerOrderID(Long id);

	Request findByMerchantOrderId(Long id);
	
	List<Request> findAllByStatus(Status s);
	
	Request findByPaymentId(String paymentId);

}
