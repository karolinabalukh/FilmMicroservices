package com.example.service;

import com.example.dto.EmailRequest;
import com.example.model.EmailDocument;
import com.example.model.EmailStatusEnum;
import com.example.repository.EmailRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {"spring.kafka.listener.auto-startup=false"})
@ActiveProfiles("test")
class EmailKafkaConsumerIntegrationTest {

    @Autowired
    private EmailKafkaConsumer consumer;

    @Autowired
    private EmailRepository emailRepository;

    @MockitoBean
    private EmailService emailService;

    @AfterEach
    void cleanUp() {
        emailRepository.deleteAll();
    }

    @Test
    void shouldConsumeKafkaMessageAndSaveEmailDocument() {
        EmailRequest request = new EmailRequest();
        request.setEmail("kafka@test.com");
        request.setSubject("Kafka test");
        request.setContent("Hello");

        consumer.consume(request);

        EmailDocument saved = emailRepository.findAll().iterator().next();

        assertThat(saved.getRecipient()).isEqualTo("kafka@test.com");
        assertThat(saved.getStatus()).isEqualTo(EmailStatusEnum.PENDING);
        verify(emailService).sendEmail(any(EmailDocument.class));
    }
}