package sep.project.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.PaymentMethod;
import sep.project.repositories.PaymentMethodRepository;

@Service
public class PaymentMethodService {

	@Autowired
	private PaymentMethodRepository paymentMethodRepository;

	public PaymentMethod addPaymentMethod(PaymentMethod paymentMethod) {
		if (paymentMethod.getId() == null) {
			PaymentMethod saved = paymentMethodRepository.save(paymentMethod);

			return saved;
		}
		return null;
	}

	public List<PaymentMethod> getAll() {

		return paymentMethodRepository.findByDeleted(false);
	}

	/**
	 * Metoda za preuzimanje nacina placanja sa odgovarajucim nazivom
	 * 
	 * @param name naziv nacina placanja
	 * @return pronađeni način plaćanja
	 * @see PaymentMethod
	 */
	public PaymentMethod getByName(String name) {
		return this.paymentMethodRepository.findByDeletedAndNameIgnoreCase(false, name);

	}
	
	public List<PaymentMethod> findPaymentMethodsWithSubscription(){
		
		return paymentMethodRepository.findBySubscriptionAndDeleted(true, false);
	}

}
