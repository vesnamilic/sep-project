package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.model.CardOwner;

public interface CardOwnerRepository extends JpaRepository<CardOwner, Long> {
	
	CardOwner findByMerchantID(String id);

	CardOwner findByCardPan(String pan);

}
