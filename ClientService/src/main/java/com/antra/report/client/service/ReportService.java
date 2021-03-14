package com.antra.report.client.service;

import com.antra.report.client.pojo.type.FileType;
import com.antra.report.client.pojo.type.ReportStatus;
import com.antra.report.client.pojo.reponse.RestCallFeedback;
import com.antra.report.client.pojo.reponse.DataResponse;
import com.antra.report.client.pojo.reponse.ReportResponse;
import com.antra.report.client.pojo.request.ReportRequest;

import java.io.InputStream;
import java.util.List;

public interface ReportService {
    List<ReportResponse> getReportList(int userId);

    DataResponse getReport(String reqId);

    ReportResponse generateReportsSync(ReportRequest request, int userId);

    ReportResponse generateReportsAsync(ReportRequest request, int userId);

    ReportResponse updateReport(ReportRequest request, String reqId);

    ReportResponse deleteReport(String reqId);

    void updatePDFReport(RestCallFeedback feedback, ReportStatus successStatus, ReportStatus failureStatus);

    void updateExcelReport(RestCallFeedback feedback, ReportStatus successStatus, ReportStatus failureStatus);

    InputStream getFileBodyByReqId(String reqId, FileType type);
}
