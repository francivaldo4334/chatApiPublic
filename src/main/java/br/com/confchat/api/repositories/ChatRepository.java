package br.com.confchat.api.repositories;

import br.com.confchat.api.models.ChatModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatModel,Integer> {
}
