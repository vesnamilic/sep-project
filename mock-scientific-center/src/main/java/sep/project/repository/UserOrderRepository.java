package sep.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sep.project.model.OrderStatus;
import sep.project.model.UserOrder;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {

	
	@Query("SELECT distinct o from UserOrder as o WHERE o.orderStatus = ?1 or o.orderStatus = ?2 ")
	List<UserOrder> findOrdersBasedOnOrderStatused(OrderStatus orderStatus1, OrderStatus orderStatus2);
}


