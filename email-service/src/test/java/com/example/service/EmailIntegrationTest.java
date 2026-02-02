package com.example.service;

import com.example.model.EmailDocument;
import com.example.model.EmailStatusEnum;
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

@SpringBootTest(properties = {"spring.kafka.listener.auto-startup=false"})
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
        EmailDocument email = new EmailDocument();
        email.setRecipient("test@gmail.com");
        email.setSubject("Test");
        email.setContent("Hello");
        email.setStatus(EmailStatusEnum.PENDING);

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(email);

        EmailDocument saved = emailRepository.findAll().iterator().next();

        assertThat(saved.getStatus()).isEqualTo(EmailStatusEnum.SENT);
        assertThat(saved.getErrorMessage()).isNull();
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldHandleEmailSendFailure() {
        EmailDocument email = new EmailDocument();
        email.setRecipient("fail@gmail.com");
        email.setStatus(EmailStatusEnum.PENDING);

        doThrow(new MailSendException("SMTP error"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        emailService.sendEmail(email);

        EmailDocument saved = emailRepository.findAll().iterator().next();

        assertThat(saved.getStatus()).isEqualTo(EmailStatusEnum.FAILED);
        assertThat(saved.getErrorMessage()).contains("org.springframework.mail.MailSendException");
        assertThat(saved.getAttemptsCount()).isEqualTo(1);
    }
}