package br.com.confchat.api.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class AppConfig {
    @Value("${spring.messages.basename}")
    private String message;

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        // Defina o nome base dos arquivos de mensagens (sem extensão)
        messageSource.setBasename(message);

        // Defina o charset usado para ler os arquivos de mensagens
        String defaultEncode = "UTF-8";
        messageSource.setDefaultEncoding(defaultEncode);

        return messageSource;
    }
}
