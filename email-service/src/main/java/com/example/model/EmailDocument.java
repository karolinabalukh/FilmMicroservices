package com.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Document(indexName = "emails", createIndex = true)
public class EmailDocument {

    @Id
    private String id;

    private String recipient;
    private String subject;
    private String content;

    private EmailStatusEnum status;

    private String errorMessage;

    private int attemptsCount;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "UTC"
    )
    private Instant lastAttemptTime = Instant.now();

    public EmailDocument() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public EmailStatusEnum getStatus() {
        return status;
    }

    public void setStatus(EmailStatusEnum status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getAttemptsCount() {
        return attemptsCount;
    }

    public void setAttemptsCount(int attemptsCount) {
        this.attemptsCount = attemptsCount;
    }

    public Instant getLastAttemptTime() {
        return lastAttemptTime;
    }

    public void setLastAttemptTime(Instant lastAttemptTime) {
        this.lastAttemptTime = lastAttemptTime;
    }
}