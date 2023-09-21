package br.com.confchat.api.repositories;

import br.com.confchat.api.models.MessageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageModel,Integer> {
    @Query(value = "SELECT * FROM message_tb WHERE user_id = :v0 LIMIT 1",nativeQuery = true)
    Optional<MessageModel> findByUserId(@Param("v0") UUID id);
    @Query(value = "SELECT * FROM message_tb WHERE user_id = :v0",nativeQuery = true)
    Page<MessageModel> findByUserId(@Param("v0") UUID id, Pageable pageable);
    @Query(value = "SELECT * FROM message_tb WHERE chat_id = :v0",nativeQuery = true)
    Page<MessageModel> findByChatId(@Param("v0") int chatId, PageRequest pageable);
}
