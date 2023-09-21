package br.com.confchat.api.repositories;

import br.com.confchat.api.models.AttachmentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<AttachmentModel,Integer> {
}
