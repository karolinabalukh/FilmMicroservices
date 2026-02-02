package com.example.service;

import com.example.model.EmailDocument;
import com.example.model.EmailStatusEnum;
import com.example.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    public void sendEmail(EmailDocument email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email.getRecipient());
            message.setSubject(email.getSubject());
            message.setText(email.getContent());

            mailSender.send(message);

            email.setStatus(EmailStatusEnum.SENT);
            email.setErrorMessage(null);
            log.info("Email successfully sent to: {}", email.getRecipient());

        } catch (Exception e) {
            String fullErrorMessage = e.getClass().getName() + ": " + e.getMessage();
            email.setStatus(EmailStatusEnum.FAILED);
            email.setErrorMessage(fullErrorMessage);
            email.setAttemptsCount(email.getAttemptsCount() + 1);

            log.error("SMTP sending error: {}", fullErrorMessage);
        } finally {
            email.setLastAttemptTime(Instant.now());
            emailRepository.save(email);
        }
    }
}