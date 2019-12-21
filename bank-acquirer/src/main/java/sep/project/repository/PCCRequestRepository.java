package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.model.PCCRequest;

public interface PCCRequestRepository extends JpaRepository<PCCRequest, Long> {
	
	PCCRequest findByMerchantOrderID(Long id);

}
