package sep.project.services;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		
	public Seller getSeller(String email) {
		
		return sellerRepository.findByEmailAndDeleted(email, false);
	}
	
	public Seller getSeller(Long id) {
		
		return sellerRepository.findByIdAndDeleted(id, false);
	}
	
	public Seller addSeller(Seller seller) {
		if(getSeller(seller.getEmail()) == null && seller.getId() == null) {
			Seller saved = sellerRepository.save(seller);
						
			return saved;
		}
		return null;
	}
	
	public Set<PaymentMethod> getPayments(String email) {
		
		Seller seller = getSeller(email);
		if(seller == null) {
			
			return null;
		}
		
		return seller.getPaymentMethods();
	}

	public Seller addPayment(Long sellerId, PaymentMethod paymentMethod) {
		
		Seller seller = getSeller(sellerId);
		PaymentMethod pm = paymentMethodRepository.findByIdAndDeleted(paymentMethod.getId(),false);
		
		if(seller == null || pm == null) {
			
			return null;
		}
		
		seller.getPaymentMethods().add(pm);
		
		sellerRepository.save(seller);
		
		return seller;
	}
}
