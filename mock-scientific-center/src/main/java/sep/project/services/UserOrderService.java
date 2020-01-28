package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.UserOrder;
import sep.project.repository.UserOrderRepository;

@Service
public class UserOrderService {

	@Autowired
	private UserOrderRepository userOrderRepository;
	
	public UserOrder save(UserOrder order) {
		UserOrder saved = null;
		
		try {
			saved = this.userOrderRepository.save(order);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return saved;
	}
	
	public UserOrder getUserOrder(String uuid) {
		return this.userOrderRepository.findByUuid(uuid);
	}
	
	public UserOrder getUserOrder(Long id) {
		return this.userOrderRepository.getOne(id);
	}
}
