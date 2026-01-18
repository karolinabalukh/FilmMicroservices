package com.example.service;

import com.example.dto.EmailRequest;
import com.example.model.EmailStatus;
import com.example.repository.EmailRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EmailKafkaConsumer {
    private final EmailService emailService;
    private final EmailRepository repository;

    public EmailKafkaConsumer(@Lazy EmailService emailService, EmailRepository repository) {
        this.emailService = emailService;
        this.repository = repository;
    }

    @KafkaListener(topics = "email-topic", groupId = "email-group-v3")
    public void consume(EmailRequest request) {
        System.out.println("(!) ОТРИМАНО ПОВІДОМЛЕННЯ В CONSUMER: " + request.getEmail());
        try {
            EmailStatus emailStatus = new EmailStatus();
            emailStatus.setRecipient(request.getEmail());
            emailStatus.setSubject(request.getSubject());
            emailStatus.setContent(request.getContent());
            emailStatus.setStatus("PENDING");
            emailStatus.setLastAttemptTime(Instant.now());
            EmailStatus saved = repository.save(emailStatus);
            System.out.println("ПОВІДОМЛЕННЯ ЗБЕРЕЖЕНО В ELASTICSEARCH, ID: " + saved.getId());
            emailService.sendEmail(saved);
        } catch (Exception e) {
            System.err.println("ПОМИЛКА В CONSUMER: " + e.getMessage());
            e.printStackTrace();
        }
    }
}