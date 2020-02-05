package sep.project.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import sep.project.dto.OrderInformationDTO;
import sep.project.model.OrderStatus;
import sep.project.model.Seller;
import sep.project.model.UserOrder;
import sep.project.repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private RestTemplate restTemplate;	

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
	 * Metoda za pronalazak narudžbine putem uuid-a
	 * @param uuid
	 * @return UserOrder
	 * @see UserOrder
	 */
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
	
	/**
	 * Metoda za proveru da li je narudžbina istekla
	 * @param date datum isteka narudžbine
	 * @return true- ukoliko je istekla, false- ukoliko je i dalje aktivna
	 */
	public Boolean isExpired(Date date) {
		return date.before(new Date());
	}
	
	
	/**
	 * Metoda za periodicnu proveru narudžina sa statusom CREATED
	 * Sinhronizacija sa stanjem transakcija na CoinGate-u
	 */
	@Scheduled(initialDelay = 10000, fixedRate = 60000)
	public void checkOrdersStatus() {
		List<UserOrder> orders = this.orderRepository.findExpiredOrders(new Date());
		
		for(UserOrder order : orders) {
			try {
				System.out.println(order.getFailedUrl());
				restTemplate.exchange("https://localhost:9897/orders/", HttpMethod.GET, null, Void.class);
			} catch (RestClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (Exception e2) {
				e2.printStackTrace();
				return;
			}
			order.setOrderStatus(OrderStatus.EXPIRED);
			this.saveOrder(order);
		}

	}
}
