package com.example.service;

import com.example.model.EmailDocument;
import com.example.model.EmailStatusEnum;
import com.example.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailRetryService {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(EmailRetryService.class);
    private final EmailRepository emailRepository;
    private final EmailService emailService;
    private static final int MAX_ATTEMPTS = 3;

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void retryFailedEmails() {
        List<EmailDocument> failedEmails = emailRepository.findByStatus(EmailStatusEnum.FAILED);

        if (failedEmails.isEmpty()) {
            return;
        }

        log.info("Retrying {} failed emails", failedEmails.size());

        for (EmailDocument email : failedEmails) {
            if (email.getAttemptsCount() >= MAX_ATTEMPTS) {
                log.warn("Email ID {} reached max attempts. Setting to DEAD.", email.getId());
                email.setStatus(EmailStatusEnum.DEAD);
                emailRepository.save(email);
                continue;
            }
            emailService.sendEmail(email);
        }
    }
}