package com.example.service;

import com.example.model.EmailStatus;
import com.example.repository.EmailRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        properties = {
                "spring.kafka.listener.auto-startup=false"
        }
)
@ActiveProfiles("test")
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    @AfterEach
    void cleanUp() {
        emailRepository.deleteAll();
    }

    @Test
    void shouldSendEmailSuccessfully() {
        EmailStatus email = new EmailStatus();
        email.setRecipient("test@gmail.com");
        email.setSubject("Test subject");
        email.setContent("Hello!");
        email.setAttemptsCount(0);
        email.setStatus("PENDING");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(email);
        EmailStatus saved = emailRepository.findAll().iterator().next();

        assertThat(saved.getStatus()).isEqualTo("SENT");
        assertThat(saved.getErrorMessage()).isNull();
        assertThat(saved.getAttemptsCount()).isEqualTo(0);
        assertThat(saved.getLastAttemptTime()).isNotNull();

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldHandleEmailSendFailure() {
        EmailStatus email = new EmailStatus();
        email.setRecipient("fail@gmail.com");
        email.setSubject("Fail test");
        email.setContent("Boom!");
        email.setAttemptsCount(0);
        email.setStatus("PENDING");

        doThrow(new MailSendException("SMTP error"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        emailService.sendEmail(email);
        EmailStatus saved = emailRepository.findAll().iterator().next();

        assertThat(saved.getStatus()).isEqualTo("ERROR");
        assertThat(saved.getErrorMessage()).contains("SMTP");
        assertThat(saved.getAttemptsCount()).isEqualTo(1);
        assertThat(saved.getLastAttemptTime()).isNotNull();
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
