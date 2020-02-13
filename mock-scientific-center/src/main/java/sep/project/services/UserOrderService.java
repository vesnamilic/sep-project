package sep.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.OrderStatusInformationDTO;
import sep.project.model.OrderStatus;
import sep.project.model.UserOrder;
import sep.project.repository.UserOrderRepository;

@Service
public class UserOrderService {

	@Autowired
	private UserOrderRepository userOrderRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Value("https://localhost:8762/api/client/orders/status")
	private String kpUrl;

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

	public UserOrder getUserOrder(Long id) {
		return this.userOrderRepository.getOne(id);
	}

	/**
	 * Metoda za periodicnu proveru narudžina sa statusima INITIATED i CREATED
	 */
	@Scheduled(initialDelay = 10000, fixedRate = 60000)
	public void checkOrdersStatus() {

		List<UserOrder> orders = this.userOrderRepository.findOrdersBasedOnOrderStatused(OrderStatus.INITIATED,
				OrderStatus.CREATED);
		System.out.println(orders);
		for (UserOrder order : orders) {
			ResponseEntity<OrderStatusInformationDTO> response = null;
			try {
				System.out.println(this.kpUrl + "?orderId=" + order.getId() + "&email=" + order.getMagazine().getEmail());
				response = restTemplate.getForEntity(
						this.kpUrl + "?orderId=" + order.getId() + "&email=" + order.getMagazine().getEmail(),
						OrderStatusInformationDTO.class);
			} catch (RestClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			OrderStatus status = OrderStatus.valueOf(response.getBody().getStatus());
			
			if(status != null && !status.equals(order.getOrderStatus())) {
				order.setOrderStatus(status);
				this.save(order);
			}
		}
	}
}
