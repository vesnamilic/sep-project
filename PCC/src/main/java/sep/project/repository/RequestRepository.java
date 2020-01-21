package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {

	Request findByAcquirerOrderID(Long id);

	Request findByMerchantOrderId(Long id);

}
