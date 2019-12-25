package sep.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.UserOrder;
import sep.project.repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	/**
	 * Metoda za vraćanje narudžbine sa odgovarajućim identifikatorom
	 * 
	 * @param id identifikator narudžbine
	 * @return narudžbina ukoliko je pronađena u suprotnom null
	 * @see UserOrder
	 */
	public UserOrder getOrder(Long id) {
		return this.orderRepository.getOne(id);
	}

	/**
	 * Metoda za čuvanje narudžbina korisnika
	 * 
	 * @param order informacija o narudžbini
	 * @return sačuvana narudžbina ukoliko je čuvanje uspešno u suprotnom null
	 * @see UserOrder
	 */
	public UserOrder saveOrder(UserOrder order) {
		UserOrder saved = null;

		try {
			saved = this.orderRepository.save(order);
		} catch (Exception e) {
			return null;
		}

		return saved;

	}

}
