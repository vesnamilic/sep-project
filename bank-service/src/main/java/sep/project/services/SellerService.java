package sep.project.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sep.project.DTOs.FieldDTO;
import sep.project.enums.FieldType;
import sep.project.model.Seller;
import sep.project.repository.SellerRepository;

@Service
public class SellerService {
	
	@Autowired
	SellerRepository sellerRepository;
		
	public Seller getSeller(String email) {
		
		return sellerRepository.findByEmail(email);
	}
	
	public Seller save(Seller seller) {
		
		if(getSeller(seller.getEmail()) == null && seller.getId() == null) {
			Seller saved = sellerRepository.save(seller);	
			return saved;
		}
				
		return null;
	}

	public List<FieldDTO> getFields(){
		
		List<FieldDTO> list = new ArrayList<FieldDTO>();
		
		list.add(new FieldDTO("merchantID", "Merchant ID", FieldType.TEXT, true));
		list.add(new FieldDTO("merchantPass", "Merchant password", FieldType.PASSWORD, true));
		
		return list;
	}
}
