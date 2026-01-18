package com.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(indexName = "emails", createIndex = true)
@Getter
@Setter
public class EmailStatus {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAttemptsCount() {
        return attemptsCount;
    }

    public void setAttemptsCount(int attemptsCount) {
        this.attemptsCount = attemptsCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Id
    private String id;
    private String recipient;
    private String subject;
    private String content;
    private String status;
    private String errorMessage;
    private int attemptsCount;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private Instant lastAttemptTime = Instant.now();

    public Instant getLastAttemptTime() {
        return lastAttemptTime;
    }

    public void setLastAttemptTime(Instant lastAttemptTime) {
        this.lastAttemptTime = lastAttemptTime;
    }
}
