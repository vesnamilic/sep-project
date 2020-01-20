package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sep.project.model.Bank;

public interface BankRepository extends JpaRepository<Bank, Long> {
	
	Bank findByBankNumber(String s);

}
