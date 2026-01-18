package com.example.service;

import com.example.model.EmailStatus;
import com.example.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    public EmailService(JavaMailSender mailSender, EmailRepository emailRepository) {
        this.mailSender = mailSender;
        this.emailRepository = emailRepository;
    }

    public void sendEmail(EmailStatus emailStatus) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailStatus.getRecipient());
            message.setSubject(emailStatus.getSubject());
            message.setText(emailStatus.getContent());
            mailSender.send(message);
            emailStatus.setStatus("SENT");
            emailStatus.setErrorMessage(null);
            System.out.println("ЛИСТ УСПІШНО ВІДПРАВЛЕНО НА: " + emailStatus.getRecipient());
        } catch (Exception e) {
            emailStatus.setStatus("ERROR");
            emailStatus.setErrorMessage(e.getMessage());
            emailStatus.setAttemptsCount(emailStatus.getAttemptsCount() + 1);
            System.err.println("ПОМИЛКА ВІДПРАВКИ SMTP: " + e.getMessage());
        } finally {
            emailStatus.setLastAttemptTime(Instant.now());
            emailRepository.save(emailStatus);
            System.out.println("СТАТУС ОНОВЛЕНО В ELASTICSEARCH");
        }
    }
}