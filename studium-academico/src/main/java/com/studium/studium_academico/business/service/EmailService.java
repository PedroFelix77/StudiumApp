package com.studium.studium_academico.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    public void sendActivationEmail(String email, String activationLink, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(email);
            message.setSubject("Ative sua conta");
            message.setText("Olá " + name + ",\n\n" +
                    "Sua conta foi criada com sucesso!\n" +
                    "Para ativá-la e definir sua senha, clique no link abaixo:\n\n" +
                    activationLink + "\n\n" +
                    "Se você não solicitou essa criação de conta, ignore esta mensagem.");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Erro ao tentar enviar email para {}", email, e);
        }
    }

    public void sendAccountActivatedConfirmation(String email, String name){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(email);
            message.setSubject("Conta ativada com sucesso!");

            message.setText(
                    "Olá " + name + ",\n\n" +
                            "Sua conta foi ativada com sucesso e já está pronta para uso.\n\n" +
                            "Agora você já pode fazer login e começar a utilizar o sistema.\n\n" +
                            "Bem-vindo(a) e bons estudos!"
            );

            mailSender.send(message);

            log.info("[EMAIL] Email de confirmação de ativação enviado para {}", email);

        } catch (Exception e) {
            log.error("[EMAIL] Erro ao tentar enviar confirmação de ativação para {}", email, e);
        }
    }

    public void sendPasswordResetEmail(String email, String resetLink, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(email);
            message.setSubject("Redefinição de senha - Studium");

            message.setText(
                    "Olá " + name + ",\n\n" +
                            "Recebemos uma solicitação para redefinir sua senha.\n" +
                            "Para continuar, clique no link abaixo:\n\n" +
                            resetLink + "\n\n" +
                            "Este link expira em 30 minutos.\n" +
                            "Se você não solicitou a redefinição, ignore este e-mail."
            );

            mailSender.send(message);

            log.info("[EMAIL] E-mail de redefinição de senha enviado para {}", email);

        } catch (Exception e) {
            log.error("[EMAIL] Erro ao tentar enviar e-mail de redefinição de senha para {}", email, e);
        }
    }

}
