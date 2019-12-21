package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.model.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long> {
	
}
