package com.antra.report.client.endpoint;

import com.antra.report.client.entity.ReportRequestEntity;
import com.antra.report.client.exception.RequestNotFoundException;
import com.antra.report.client.pojo.type.EmailType;
import com.antra.report.client.pojo.type.ReportStatus;
import com.antra.report.client.pojo.reponse.RestCallFeedback;
import com.antra.report.client.repository.ReportRequestRepo;
import com.antra.report.client.service.EmailService;
import com.antra.report.client.service.ReportService;

import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class ReportSQSListener {
    private ReportRequestRepo reportRequestRepo;

    private ReportService reportService;

    private EmailService emailService;

    public ReportSQSListener(ReportService reportService, EmailService emailService, ReportRequestRepo reportRequestRepo) {
        this.reportRequestRepo = reportRequestRepo;
        this.reportService = reportService;
        this.emailService = emailService;
    }

    private String findUser(String reqId) {
        ReportRequestEntity entity = reportRequestRepo.findById(reqId).orElseThrow(RequestNotFoundException::new);
        return entity.getUser().getEmail();
    }

    @SqsListener("Excel_Response_Queue")
    public void responseQueueListenerExcel(RestCallFeedback feedback) {
        reportService.updateExcelReport(feedback, ReportStatus.GENERATED, ReportStatus.GENERATE_FAILED);
        emailService.sendEmail(findUser(feedback.getReqId()), EmailType.SUCCESS, "excel");
    }

    @SqsListener("PDF_Response_Queue")
    public void responseQueueListenerPdf(RestCallFeedback feedback) {
        reportService.updatePDFReport(feedback, ReportStatus.GENERATED, ReportStatus.GENERATE_FAILED);
        emailService.sendEmail(findUser(feedback.getReqId()), EmailType.SUCCESS, "pdf");
    }
}
