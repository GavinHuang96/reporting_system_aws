package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.model.ExcelRequest;
import com.antra.evaluation.reporting_system.model.ExcelResponse;
import com.antra.evaluation.reporting_system.model.ExcelSNSRequest;
import com.antra.evaluation.reporting_system.entity.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class ExcelRequestQueueListener {
    private final QueueMessagingTemplate queueMessagingTemplate;

    private final ExcelService excelService;

    public ExcelRequestQueueListener(QueueMessagingTemplate queueMessagingTemplate, ExcelService excelService) {
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.excelService = excelService;
    }

    @SqsListener("Excel_Request_Queue")
    public void fanoutQueueListener(ExcelSNSRequest excelSNSRequest) {
        ExcelRequest request = excelSNSRequest.getExcelRequest();
        ExcelResponse response = new ExcelResponse();
        try {
            ExcelFile file = excelService.generateFile(request);
            response.setReqId(request.getReqId());
            response.setFileName(file.getFileName());
            response.setFileLocation(file.getFileLocation());
            response.setFileSize(file.getFileSize());
            response.setFailed(false);
        } catch (Exception e) {
            response.setReqId(request.getReqId());
            response.setFailed(true);
        }
        queueMessagingTemplate.convertAndSend("Excel_Response_Queue", response);
    }
}
