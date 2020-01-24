package sep.project.services;

import java.util.Date;
import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.dto.OrderInformationDTO;
import sep.project.model.OrderStatus;
import sep.project.model.Seller;
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
	
	
	public UserOrder findOrderByUUID(String uuid) {
		return this.orderRepository.findByUuid(uuid);
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
	
	/**
	 * Metoda za kreiranje narudžbina korisnika
	 * @param order informacije o narudžbini
	 * @param seller prodavac
	 * @return kreirana narudžbina
	 * @see OrderInformationDTO
	 * @see Seller
	 * @see UserOrder
	 */
	public UserOrder createOrder(OrderInformationDTO order, Seller seller) {
		Date dt = new Date();
		DateTime dtOrg = new DateTime(dt);
		DateTime dtPlusOne = dtOrg.plusDays(1);
		UUID uuid = UUID.randomUUID();
		
		return new UserOrder(uuid.toString(),dtPlusOne.toDate(), OrderStatus.CREATED, order.getPaymentAmount(), order.getPaymentCurrency(), order.getSuccessUrl(), order.getErrorUrl(), order.getFailedUrl(), seller);
	}
	
	public Boolean isExpired(Date date) {
		return date.before(new Date());
	}
}
