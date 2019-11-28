package sep.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.model.Client;
import sep.project.model.PaymentMethod;
import sep.project.model.Seller;
import sep.project.repositories.ClientRepository;
import sep.project.repositories.PaymentMethodRepository;
import sep.project.repositories.SellerRepository;
import sep.projects.dto.SellerDTO;

@Service
public class SellerService {
	
	@Autowired
	private SellerRepository sellerRepository;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	
	public SellerDTO save(SellerDTO sellerDTO) {
		if(sellerDTO.getId() == null && clientRepository.getOne(sellerDTO.getClientId()) != null) {
			System.out.println("CLIENT ID " + sellerDTO.getClientId());
			Client client = clientRepository.getOne(sellerDTO.getClientId());
			
			Seller newSeller = new Seller(sellerDTO);
			newSeller.setClient(client);
			
			Seller saved = sellerRepository.save(newSeller);
			
			sellerDTO.setId(saved.getId());
			
			return sellerDTO;
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
