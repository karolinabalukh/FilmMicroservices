package com.example.service;

import com.example.model.EmailStatus;
import com.example.repository.EmailRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockitoBean
    private JavaMailSender mailSender;

    @Autowired
    private EmailRepository repository;

    @Test
    void testEmailStatusTransitions() {
        EmailStatus email = new EmailStatus();
        email.setRecipient("test@example.com");
        email.setSubject("Test Subject");
        email.setContent("Test Content");
        email.setStatus("PENDING");

        email = repository.save(email);
        String emailId = email.getId();
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(email);

        Optional<EmailStatus> sentEmailOpt = repository.findById(emailId);
        assertTrue(sentEmailOpt.isPresent());
        assertEquals("відправлений", sentEmailOpt.get().getStatus());

        doThrow(new RuntimeException("SMTP Server Unavailable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(sentEmailOpt.get());
        Optional<EmailStatus> failedEmailOpt = repository.findById(emailId);
        assertTrue(failedEmailOpt.isPresent());
        EmailStatus failedEmail = failedEmailOpt.get();

        assertEquals("помилковий", failedEmail.getStatus());
        assertNotNull(failedEmail.getErrorMessage());
        assertTrue(failedEmail.getErrorMessage().contains("SMTP Server Unavailable"));
        assertTrue(failedEmail.getAttemptsCount() > 0);
    }
}