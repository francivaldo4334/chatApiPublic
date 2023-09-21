package br.com.confchat.api.repositories;

import br.com.confchat.api.models.ContactModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<ContactModel,Integer> {
    @Query(value = "SELECT * FROM contact_tb WHERE user_id = :v0 AND user_contact_id = :v1",nativeQuery = true)
    Optional<ContactModel> findByUserIdAndUserContactId(@Param("v0") UUID id, @Param("v1")UUID fromUserId);
    @Query(value = "SELECT * FROM contact_tb WHERE user_id = :v0",nativeQuery = true)
    List<ContactModel> findByUserId(@Param("v0") UUID id);
    Page<ContactModel> findByUserId(@Param("v0") UUID id, Pageable pageable);
    @Query(value = "SELECT * FROM contact_tb WHERE chat_id = :v0",nativeQuery = true)
    List<ContactModel> findByIdChatId(@Param("v0") int chatId);
}
