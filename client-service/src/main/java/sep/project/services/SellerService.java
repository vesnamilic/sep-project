package sep.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.PaymentMethod;
import sep.project.model.Seller;
import sep.project.repositories.PaymentMethodRepository;
import sep.project.repositories.SellerRepository;

@Service
public class SellerService {
	
	@Autowired
	private SellerRepository sellerRepository;
	
	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	
	public Seller save(Seller seller) {
		if(seller.getId() == null) {
			Seller saved = sellerRepository.save(seller);
						
			return saved;
		}
		return null;
	}

	
	public Seller addPayment(Long sellerId, List<Long> methods) {
		
		Seller seller = sellerRepository.getOne(sellerId);
		if(seller == null) {
			
			return null;
		}
		
		for(Long method : methods) {
			PaymentMethod pm = paymentMethodRepository.getOne(method);
			
			if(pm != null) {
				
				seller.getPaymentMethods().add(pm);
				
				sellerRepository.save(seller);
			}
		}
		
		return seller;
	}
}
