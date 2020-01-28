package sep.project.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import sep.project.model.UserOrder;

public interface OrderRepository  extends JpaRepository<UserOrder, Long>, JpaSpecificationExecutor<UserOrder>{
	
	UserOrder findByUuid(String uuid);
	
	@Query("SELECT distinct o from UserOrder as o WHERE o.expirationDate < ?1 and o.orderStatus = 'CREATED' ")
	List<UserOrder> findExpiredOrders(Date today);
}
