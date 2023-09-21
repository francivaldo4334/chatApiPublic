package br.com.confchat.api.infra;

import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import java.awt.image.BufferedImage;

@Configuration
public class AuthenticationCodeConfig {
    @Bean
    public QRCodeWriter grCodeWriter() {
        return new QRCodeWriter();
    }

    @Bean
    public HttpMessageConverter<BufferedImage> imageConverter() {
        return new BufferedImageHttpMessageConverter();
    }
}
