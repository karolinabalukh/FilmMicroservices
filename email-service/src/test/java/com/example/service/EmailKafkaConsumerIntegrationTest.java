package com.example.service;
import com.example.dto.EmailRequest;
import com.example.model.EmailStatus;
import com.example.repository.EmailRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        properties = {
                "spring.kafka.listener.auto-startup=false"
        }
)
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
    void shouldConsumeKafkaMessageAndSaveEmailStatus() {
        EmailRequest request = new EmailRequest();
        request.setEmail("kafka@test.com");
        request.setSubject("Kafka test");
        request.setContent("Hello from Kafka");
        consumer.consume(request);
        EmailStatus saved = emailRepository.findAll().iterator().next();

        assertThat(saved.getRecipient()).isEqualTo("kafka@test.com");
        assertThat(saved.getSubject()).isEqualTo("Kafka test");
        assertThat(saved.getContent()).isEqualTo("Hello from Kafka");
        assertThat(saved.getStatus()).isEqualTo("PENDING");
        assertThat(saved.getLastAttemptTime()).isNotNull();

        verify(emailService, times(1)).sendEmail(any(EmailStatus.class));
    }
}
