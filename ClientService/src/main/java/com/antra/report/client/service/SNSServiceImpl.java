package com.antra.report.client.service;

import com.antra.report.client.pojo.request.ReportRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SNSServiceImpl implements SNSService {
    private final NotificationMessagingTemplate notificationMessagingTemplate;

    @Value("${app.aws.sns.topic}")
    private String snsTopic;

    @Autowired
    public SNSServiceImpl(NotificationMessagingTemplate notificationMessagingTemplate) {
        this.notificationMessagingTemplate = notificationMessagingTemplate;
    }

    @Override
    public void sendReportNotification(ReportRequest request) {
        this.notificationMessagingTemplate.sendNotification(snsTopic, request, null);
    }
}
