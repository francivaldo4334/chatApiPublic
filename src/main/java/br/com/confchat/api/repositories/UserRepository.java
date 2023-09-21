package br.com.confchat.api.repositories;

import br.com.confchat.api.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@Repository
//@EnableJpaRepositories
public interface UserRepository extends JpaRepository<UserModel, UUID> {
    Optional<UserModel> findByLogin(String userName);
    List<UserModel> findAll();
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findById(UUID Id);
}
