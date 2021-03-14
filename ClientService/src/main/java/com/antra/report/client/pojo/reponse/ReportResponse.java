package com.antra.report.client.pojo.reponse;

import com.antra.report.client.entity.ReportRequestEntity;
import com.antra.report.client.pojo.type.ReportStatus;

import java.time.LocalDateTime;

public class ReportResponse {
    private String reqId;

    private String description;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private ReportStatus pdfReportStatus;

    private ReportStatus excelReportStatus;

    public ReportResponse() {
    }

    public ReportResponse(ReportRequestEntity entity) {
        this.reqId = entity.getReqId();
        this.description = entity.getDescription();
        this.createdTime = entity.getCreatedTime();
        this.updatedTime = entity.getUpdatedTime();
        this.pdfReportStatus = entity.getPdfReport().getStatus();
        this.excelReportStatus = entity.getExcelReport().getStatus();
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public ReportStatus getPdfReportStatus() {
        return pdfReportStatus;
    }

    public void setPdfReportStatus(ReportStatus pdfReportStatus) {
        this.pdfReportStatus = pdfReportStatus;
    }

    public ReportStatus getExcelReportStatus() {
        return excelReportStatus;
    }

    public void setExcelReportStatus(ReportStatus excelReportStatus) {
        this.excelReportStatus = excelReportStatus;
    }
}
