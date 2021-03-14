package com.antra.report.client.service;

import com.antra.report.client.pojo.type.EmailType;

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final QueueMessagingTemplate queueMessagingTemplate;

    public EmailServiceImpl(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @Override
    public void sendEmail(String to, EmailType success, String type) {
        Email email = new Email();
        email.setTo(to);
        email.setSubject("We did it!");
        email.setBody(success.content.replace("%TYPE%", type));
        email.setToken("12345");
        queueMessagingTemplate.convertAndSend("Email_Queue", email);
    }
}

class Email {
    private String to;
    private String subject;
    private String body;
    private String token;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
