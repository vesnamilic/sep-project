package sep.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.PaymentMethod;
import sep.project.repositories.PaymentMethodRepository;

@Service
public class PaymentMethodService {
	
	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	
	public PaymentMethod save(PaymentMethod paymentMethod) {
		if(paymentMethod.getId() == null) {
			PaymentMethod saved = paymentMethodRepository.save(paymentMethod);
			
			return saved;
		}
		return null;
	}
	
	public List<PaymentMethod> getAll() {
		
		return paymentMethodRepository.findByDeleted(false);
	}

}
