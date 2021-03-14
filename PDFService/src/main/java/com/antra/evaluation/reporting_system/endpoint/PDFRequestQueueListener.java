package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.model.PDFRequest;
import com.antra.evaluation.reporting_system.model.PDFResponse;
import com.antra.evaluation.reporting_system.model.PDFSNSRequest;
import com.antra.evaluation.reporting_system.entity.PDFFile;
import com.antra.evaluation.reporting_system.service.PDFService;

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class PDFRequestQueueListener {
    private final QueueMessagingTemplate queueMessagingTemplate;

    private final PDFService pdfService;

    public PDFRequestQueueListener(QueueMessagingTemplate queueMessagingTemplate, PDFService pdfService) {
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.pdfService = pdfService;
    }

    @SqsListener("PDF_Request_Queue")
    public void fanoutQueueListener(PDFSNSRequest pdfSNSRequest) {
        PDFRequest request = pdfSNSRequest.getPdfRequest();
        PDFResponse response = new PDFResponse();
        try {
            PDFFile file = pdfService.generateFile(request);
            response.setReqId(request.getReqId());
            response.setFileName(file.getFileName());
            response.setFileLocation(file.getFileLocation());
            response.setFileSize(file.getFileSize());
            response.setFailed(false);
        } catch (Exception e) {
            response.setReqId(request.getReqId());
            response.setFailed(true);
        }
        queueMessagingTemplate.convertAndSend("PDF_Response_Queue", response);
    }
}