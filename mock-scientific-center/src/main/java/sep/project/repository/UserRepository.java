package sep.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sep.project.model.RegisteredUser;

@Repository
public interface UserRepository extends  JpaRepository<RegisteredUser, Long> {

	RegisteredUser findByUsername(String username);

}
