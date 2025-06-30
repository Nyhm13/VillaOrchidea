package it.piscinaOrchidea.VillaOrchidea.repository;

import it.piscinaOrchidea.VillaOrchidea.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsernameAndEmail(String username, String email);
//    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
