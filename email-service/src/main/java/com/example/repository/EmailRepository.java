package com.example.repository;
import com.example.model.EmailStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;
public interface EmailRepository extends ElasticsearchRepository<EmailStatus, String> {
    List<EmailStatus> findByStatus(String status);
    List<EmailStatus> findByRecipient(String recipient);
}