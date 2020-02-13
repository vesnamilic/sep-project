package sep.project.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import sep.project.model.OrderStatus;
import sep.project.model.UserOrder;

public interface OrderRepository  extends JpaRepository<UserOrder, Long>, JpaSpecificationExecutor<UserOrder>{
	
	UserOrder findByUuid(String uuid);
	
	@Query("SELECT distinct o from UserOrder as o WHERE o.expirationDate < ?1 and o.orderStatus = 'INITIATED' ")
	List<UserOrder> findExpiredOrders(Date today);
	
	List<UserOrder> findByOrderStatus(OrderStatus orderStatus);
	
	@Query("SELECT distinct o from UserOrder as o WHERE o.orderId = ?1 and o.seller.email = ?2 ")
	UserOrder findClientOrderByOrderId(Long orderId, String email);
}
