package com.antra.report.client.service;

import com.amazonaws.services.s3.AmazonS3;

import com.antra.report.client.entity.DataEntity;
import com.antra.report.client.entity.ExcelReportEntity;
import com.antra.report.client.entity.PDFReportEntity;
import com.antra.report.client.entity.ReportRequestEntity;
import com.antra.report.client.entity.User;
import com.antra.report.client.exception.DataNotFoundException;
import com.antra.report.client.exception.RequestNotFoundException;
import com.antra.report.client.exception.UserNotFoundException;
import com.antra.report.client.pojo.type.FileType;
import com.antra.report.client.pojo.type.ReportStatus;
import com.antra.report.client.pojo.type.RequestStatus;
import com.antra.report.client.pojo.reponse.RestCallFeedback;
import com.antra.report.client.pojo.reponse.DataResponse;
import com.antra.report.client.pojo.reponse.ReportResponse;
import com.antra.report.client.pojo.request.ReportRequest;
import com.antra.report.client.repository.DataRepo;
import com.antra.report.client.repository.ReportRequestRepo;
import com.antra.report.client.repository.UserRepo;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRequestRepo reportRequestRepo;

    private final UserRepo userRepo;

    private final DataRepo dataRepo;

    private final RestCallService restCallService;

    private final SNSService snsService;

    private final AmazonS3 s3Client;

    private final ExecutorService threadPool;

    @Value("${pdf.service}")
    private String pdfService;

    @Value("${excel.service}")
    private String excelService;

    @Autowired
    public ReportServiceImpl(ReportRequestRepo reportRequestRepo, UserRepo userRepo, DataRepo dataRepo, RestCallService restCallService, SNSService snsService, AmazonS3 s3Client, ExecutorService threadPool) {
        this.reportRequestRepo = reportRequestRepo;
        this.userRepo = userRepo;
        this.dataRepo = dataRepo;
        this.restCallService = restCallService;
        this.snsService = snsService;
        this.s3Client = s3Client;
        this.threadPool = threadPool;
    }

    private ReportRequestEntity createReportRequest(ReportRequest request, int userId) {
        request.setReqId("Req-" + UUID.randomUUID().toString());

        ReportRequestEntity entity = new ReportRequestEntity();
        entity.setReqId(request.getReqId());
        entity.setDescription(request.getDescription());
        entity.setStatus(RequestStatus.ACTIVE);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());

        PDFReportEntity pdfReport = new PDFReportEntity();
        pdfReport.setStatus(ReportStatus.GENERATE_PENDING);
        pdfReport.setCreatedTime(LocalDateTime.now());
        pdfReport.setUpdatedTime(LocalDateTime.now());
        entity.setPdfReport(pdfReport);

        ExcelReportEntity excelReport = new ExcelReportEntity();
        BeanUtils.copyProperties(pdfReport, excelReport);
        entity.setExcelReport(excelReport);

        User user = userRepo.findById(userId).orElseThrow(UserNotFoundException::new);
        entity.setUser(user);

        return reportRequestRepo.save(entity);
    }

    private ReportRequestEntity updateReportRequest(ReportRequest request, String reqId) {
        ReportRequestEntity entity = reportRequestRepo.findById(reqId).orElseThrow(RequestNotFoundException::new);
        entity.setDescription(request.getDescription());
        entity.setStatus(RequestStatus.ACTIVE);
        entity.setUpdatedTime(LocalDateTime.now());
        return reportRequestRepo.save(entity);
    }

    private void createData(ReportRequest request) {
        DataEntity data = new DataEntity();
        data.setId(request.getReqId());
        data.setDescription(request.getDescription());
        data.setHeaders(request.getHeaders());
        data.setData(request.getData());
        dataRepo.save(data);
    }

    private void updateData(ReportRequest request, String reqId) {
        DataEntity data = dataRepo.findById(reqId).orElseThrow(DataNotFoundException::new);
        data.setDescription(request.getDescription());
        data.setHeaders(request.getHeaders());
        data.setData(request.getData());
        dataRepo.save(data);
    }

    private RestCallFeedback sendRequest(String url, HttpMethod httpMethod, ReportRequest request) {
        RestCallFeedback feedback = null;
        try {
            feedback = (RestCallFeedback) restCallService.makeRestCall(url, httpMethod, request, RestCallFeedback.class).getBody();
        } catch (Exception e) {
            feedback = new RestCallFeedback();
            feedback.setReqId(request.getReqId());
            feedback.setFailed(true);
        }
        return feedback;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportList(int userId) {
        return reportRequestRepo.findByUserIdAndStatusOrderByCreatedTimeAsc(userId, RequestStatus.ACTIVE).stream().map(ReportResponse::new).collect(Collectors.toList());
    }

    @Override
    public DataResponse getReport(String reqId) {
        return new DataResponse(dataRepo.findById(reqId).orElseThrow(DataNotFoundException::new));
    }

    @Override
    public ReportResponse generateReportsSync(ReportRequest request, int userId) {
        createReportRequest(request, userId);
        createData(request);
        Future<?> f1 = threadPool.submit(() -> {RestCallFeedback feedback = sendRequest(excelService + "/excel", HttpMethod.POST, request); updateExcelReport(feedback, ReportStatus.GENERATED, ReportStatus.GENERATE_FAILED);});
        Future<?> f2 = threadPool.submit(() -> {RestCallFeedback feedback = sendRequest(pdfService + "/pdf", HttpMethod.POST, request); updatePDFReport(feedback, ReportStatus.GENERATED, ReportStatus.GENERATE_FAILED);});
        while (!f1.isDone() || !f2.isDone()) {
        }

        // because the entity has been updated. you need to get the updated one.
        return new ReportResponse(reportRequestRepo.findById(request.getReqId()).orElseThrow(RequestNotFoundException::new));
    }

    @Override
    public ReportResponse generateReportsAsync(ReportRequest request, int userId) {
        ReportRequestEntity entity = createReportRequest(request, userId);
        createData(request);
        snsService.sendReportNotification(request);
        return new ReportResponse(entity);
    }

    @Override
    public ReportResponse updateReport(ReportRequest request, String reqId) {
        Future<?> f1 = threadPool.submit(() -> {RestCallFeedback feedback = sendRequest(excelService + "/excel/" + reqId, HttpMethod.PUT, request); updateExcelReport(feedback, ReportStatus.UPDATED, ReportStatus.UPDATE_FAILED);});
        Future<?> f2 = threadPool.submit(() -> {RestCallFeedback feedback = sendRequest(pdfService + "/pdf/" + reqId, HttpMethod.PUT, request); updatePDFReport(feedback, ReportStatus.UPDATED, ReportStatus.UPDATE_FAILED);});
        while (!f1.isDone() || !f2.isDone()) {
        }

        ReportRequestEntity entity = updateReportRequest(request, reqId);
        updateData(request, reqId);
        return new ReportResponse(entity);
    }

    @Override
    public ReportResponse deleteReport(String reqId) {
        Future<?> f1 = threadPool.submit(() -> {RestCallFeedback feedback = sendRequest(excelService + "/excel/" + reqId, HttpMethod.DELETE, null); updateExcelReport(feedback, ReportStatus.DELETED, ReportStatus.DELETE_FAILED);});
        Future<?> f2 = threadPool.submit(() -> {RestCallFeedback feedback = sendRequest(pdfService + "/pdf/" + reqId, HttpMethod.DELETE, null); updatePDFReport(feedback, ReportStatus.DELETED, ReportStatus.DELETE_FAILED);});
        while (!f1.isDone() || !f2.isDone()) {
        }

        ReportRequestEntity entity = reportRequestRepo.findById(reqId).orElseThrow(RequestNotFoundException::new);
        entity.setStatus(RequestStatus.DELETED);
        entity.setUpdatedTime(LocalDateTime.now());
        reportRequestRepo.save(entity);
        return new ReportResponse(entity);
    }

    @Override
    public InputStream getFileBodyByReqId(String reqId, FileType type) {
        ReportRequestEntity entity = reportRequestRepo.findById(reqId).orElseThrow(RequestNotFoundException::new);
        String fileLocation = null;
        if (type == FileType.EXCEL) {
            fileLocation = entity.getExcelReport().getFileLocation();
        } else {
            fileLocation = entity.getPdfReport().getFileLocation();
        }
        String bucket = fileLocation.split("/")[0];
        String key = fileLocation.split("/")[1];
        return s3Client.getObject(bucket, key).getObjectContent();
    }

    @Override
    @Transactional
    public void updatePDFReport(RestCallFeedback feedback, ReportStatus successStatus, ReportStatus failureStatus) {
        ReportRequestEntity entity = reportRequestRepo.findById(feedback.getReqId()).orElseThrow(RequestNotFoundException::new);
        PDFReportEntity pdfReport = entity.getPdfReport();
        if (feedback.isFailed()) {
            pdfReport.setStatus(failureStatus);
            pdfReport.setUpdatedTime(LocalDateTime.now());
        } else {
            pdfReport.setFileName(feedback.getFileName());
            pdfReport.setFileLocation(feedback.getFileLocation());
            pdfReport.setFileSize(feedback.getFileSize());
            pdfReport.setStatus(successStatus);
            pdfReport.setUpdatedTime(LocalDateTime.now());
        }
        entity.setUpdatedTime(LocalDateTime.now());
        reportRequestRepo.save(entity);
    }

    @Override
    @Transactional
    public void updateExcelReport(RestCallFeedback feedback, ReportStatus successStatus, ReportStatus failureStatus) {
        ReportRequestEntity entity = reportRequestRepo.findById(feedback.getReqId()).orElseThrow(RequestNotFoundException::new);
        ExcelReportEntity excelReport = entity.getExcelReport();
        if (feedback.isFailed()) {
            excelReport.setStatus(failureStatus);
            excelReport.setUpdatedTime(LocalDateTime.now());
        } else {
            excelReport.setFileName(feedback.getFileName());
            excelReport.setFileLocation(feedback.getFileLocation());
            excelReport.setFileSize(feedback.getFileSize());
            excelReport.setStatus(successStatus);
            excelReport.setUpdatedTime(LocalDateTime.now());
        }
        entity.setUpdatedTime(LocalDateTime.now());
        reportRequestRepo.save(entity);
    }
}
