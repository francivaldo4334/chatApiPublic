package br.com.confchat.api.services;

import br.com.confchat.api.models.Log;
import br.com.confchat.api.repositories.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private LogRepository logRepository;
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        // Lógica de tratamento de exceções genéricas
        var log = new Log();
        log.setContent(ex.getMessage());
        logRepository.save(log);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocorreu uma exceção: " + ex.getMessage());
    }
}
