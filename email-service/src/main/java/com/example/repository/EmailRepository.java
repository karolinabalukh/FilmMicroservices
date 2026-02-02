package com.example.repository;

import com.example.model.EmailDocument;
import com.example.model.EmailStatusEnum;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface EmailRepository
        extends ElasticsearchRepository<EmailDocument, String> {

    List<EmailDocument> findByStatus(EmailStatusEnum status);
}