package br.com.confchat.api.services;

import br.com.confchat.api.enums.EmailTemplet;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;
    public void sendEmail(String to,String subject,String content) throws MessagingException{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content);
        helper.setFrom(from);
        javaMailSender.send(message);
    }
    public String getTemplat(EmailTemplet templet){
        switch (templet){
            case Welcome -> {
                return "Bem vindo";
            }
            case NewDeviceConected -> {
                return "Novo dispositivo conectado";
            }
            case ResetPassword -> {
                return "Codigo pra resetar a senha: {code}";
            }
            case AuthCode -> {
                return "Codigo de autentificacao: {code}";
            }
            case SuspiciousActivity -> {
                return "Identificamos uma atividade suspeita considere alterar sua senha e almentar sua seguranca com autentificacao em duas etapas caso nao tenha ativado!";
            }
            default -> {
                return "";
            }
        }
    }
}
