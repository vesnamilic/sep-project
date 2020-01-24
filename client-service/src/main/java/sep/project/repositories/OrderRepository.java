package sep.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import sep.project.model.UserOrder;

public interface OrderRepository  extends JpaRepository<UserOrder, Long>, JpaSpecificationExecutor<UserOrder>{
	
	UserOrder findByUuid(String uuid);
}
