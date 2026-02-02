package com.example.service;

import com.example.dto.EmailRequest;
import com.example.model.EmailDocument;
import com.example.model.EmailStatusEnum;
import com.example.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailKafkaConsumer {

    private final EmailService emailService;
    private final EmailRepository repository;

    @KafkaListener(topics = "email-topic", groupId = "email-group-v3")
    public void consume(EmailRequest request) {
        log.info("Received message in consumer for: {}", request.getEmail());
        try {
            EmailDocument email = new EmailDocument();
            email.setRecipient(request.getEmail());
            email.setSubject(request.getSubject());
            email.setContent(request.getContent());
            email.setStatus(EmailStatusEnum.PENDING);
            email.setLastAttemptTime(Instant.now());

            EmailDocument saved = repository.save(email);
            emailService.sendEmail(saved);

        } catch (Exception e) {
            log.error("Consumer error: {}: {}", e.getClass().getName(), e.getMessage());
        }
    }
}