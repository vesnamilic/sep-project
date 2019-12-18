package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
	
	Card findByPan(String pan);

}
