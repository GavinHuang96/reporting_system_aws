package com.antra.report.client.service;

import com.antra.report.client.pojo.type.EmailType;

public interface EmailService {
    void sendEmail(String to, EmailType success, String submitter);
}
