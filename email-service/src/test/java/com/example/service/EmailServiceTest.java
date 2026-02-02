package com.example.service;

import com.example.model.EmailDocument;
import com.example.model.EmailStatusEnum;
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
        EmailDocument email = new EmailDocument();
        email.setRecipient("test@example.com");
        email.setSubject("Test Subject");
        email.setContent("Test Content");
        email.setStatus(EmailStatusEnum.PENDING);

        email = repository.save(email);
        String emailId = email.getId();

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        emailService.sendEmail(email);

        EmailDocument sentEmail = repository.findById(emailId).orElseThrow();
        assertEquals(EmailStatusEnum.SENT, sentEmail.getStatus());
        assertNull(sentEmail.getErrorMessage());

        // 2. Помилка відправки
        doThrow(new RuntimeException("SMTP Server Unavailable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(sentEmail);

        EmailDocument failedEmail = repository.findById(emailId).orElseThrow();
        assertEquals(EmailStatusEnum.FAILED, failedEmail.getStatus());
        assertNotNull(failedEmail.getErrorMessage());
        assertTrue(failedEmail.getErrorMessage().contains("java.lang.RuntimeException"));
        assertTrue(failedEmail.getAttemptsCount() > 0);
    }
}