package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sep.project.model.UserOrder;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {

	UserOrder findByUuid(String uuid);

}


