package com.antra.report.client.entity;

import com.antra.report.client.pojo.type.RequestStatus;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity(name = "report_request")
public class ReportRequestEntity {
    @Id
    private String reqId;

    private String description;

    private RequestStatus status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "pdf_report_id")
    private PDFReportEntity pdfReport;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "excel_report_id")
    private ExcelReportEntity excelReport;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
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

    public PDFReportEntity getPdfReport() {
        return pdfReport;
    }

    public void setPdfReport(PDFReportEntity pdfReport) {
        this.pdfReport = pdfReport;
    }

    public ExcelReportEntity getExcelReport() {
        return excelReport;
    }

    public void setExcelReport(ExcelReportEntity excelReport) {
        this.excelReport = excelReport;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
