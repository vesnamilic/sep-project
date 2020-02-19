package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.model.Crypto;

public interface CryptoRepository extends JpaRepository<Crypto, Long> {
	
	Crypto findByText(String text);

}
