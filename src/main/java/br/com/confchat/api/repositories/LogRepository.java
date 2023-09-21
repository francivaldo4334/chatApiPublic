package br.com.confchat.api.repositories;

import br.com.confchat.api.models.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log,Integer> {
}
