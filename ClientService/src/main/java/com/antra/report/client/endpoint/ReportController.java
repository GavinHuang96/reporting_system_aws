package com.antra.report.client.endpoint;

import com.antra.report.client.pojo.type.FileType;
import com.antra.report.client.pojo.reponse.GeneralResponse;
import com.antra.report.client.pojo.request.ReportRequest;
import com.antra.report.client.security.UserPrincipal;
import com.antra.report.client.service.ReportService;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/report")
    public ResponseEntity<GeneralResponse> listReport(Authentication authentication) {
        log.info("{}: Got request to list all report", authentication.getName());
        return ResponseEntity.ok(new GeneralResponse(HttpStatus.OK, reportService.getReportList(((UserPrincipal) authentication.getPrincipal()).getId())));
    }

    @GetMapping("/report/{reqId}")
    public ResponseEntity<GeneralResponse> getReport(@PathVariable String reqId, Authentication authentication) {
        log.info("{}: Got request to get report", authentication.getName());
        return ResponseEntity.ok(new GeneralResponse(HttpStatus.OK, reportService.getReport(reqId)));
    }

    @PostMapping("/report/sync")
    public ResponseEntity<GeneralResponse> createReportDirectly(@RequestBody @Validated ReportRequest request, Authentication authentication) {
        log.info("{}: Got request to generate report - sync: {}", authentication.getName(), request);
        request.setDescription(String.join(" - ", "Sync", request.getDescription()));
        return ResponseEntity.ok(new GeneralResponse(HttpStatus.OK, reportService.generateReportsSync(request, ((UserPrincipal) authentication.getPrincipal()).getId())));
    }

    @PostMapping("/report/async")
    public ResponseEntity<GeneralResponse> createReportAsync(@RequestBody @Validated ReportRequest request, Authentication authentication) {
        log.info("{}: Got request to generate report - async: {}", authentication.getName(), request);
        request.setDescription(String.join(" - ", "Async", request.getDescription()));
        return ResponseEntity.ok(new GeneralResponse(HttpStatus.OK, reportService.generateReportsAsync(request, ((UserPrincipal) authentication.getPrincipal()).getId())));
    }

    @PutMapping("/report/{reqId}")
    public ResponseEntity<GeneralResponse> updateReportAsync(@PathVariable String reqId, @RequestBody @Validated ReportRequest request, Authentication authentication) {
        log.info("{}: Got request to update report - async: {}", authentication.getName(), reqId);
        request.setDescription(request.getDescription());
        return ResponseEntity.ok(new GeneralResponse(HttpStatus.OK, reportService.updateReport(request, reqId)));
    }

    @DeleteMapping("/report/{reqId}")
    public ResponseEntity<GeneralResponse> deleteReport(@PathVariable String reqId, Authentication authentication) {
        log.info("{}: Got request to delete report - reqid: {}", authentication.getName(), reqId);
        return ResponseEntity.ok(new GeneralResponse(HttpStatus.OK, reportService.deleteReport(reqId)));
    }

    @GetMapping("/report/content/{reqId}/{type}")
    public void downloadFile(@PathVariable String reqId, @PathVariable FileType type, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("{}: Got request to download file - type: {}, reqid: {}", authentication.getName(), type, reqId);
        InputStream fis = reportService.getFileBodyByReqId(reqId, type);
        String fileType = null;
        String fileName = null;
        if (type == FileType.PDF) {
            fileType = "application/pdf";
            fileName = "report.pdf";
        } else if (type == FileType.EXCEL) {
            fileType = "application/vnd.ms-excel";
            fileName = "report.xlsx";
        }
        response.setHeader("Content-Type", fileType);
        response.setHeader("File-Name", fileName);
        response.setHeader("Access-Control-Expose-Headers", "File-Name");
        if (fis != null) {
            FileCopyUtils.copy(fis, response.getOutputStream());
        } else {
            response.setStatus(500);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GeneralResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Input Data invalid: {}", e.getMessage());
        String errorFields = e.getBindingResult().getFieldErrors().stream().map(fe -> String.join(" ", fe.getField(), fe.getDefaultMessage())).collect(Collectors.joining(", "));
        return new ResponseEntity<>(new GeneralResponse(HttpStatus.BAD_REQUEST, errorFields), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralResponse> handleException(Exception e) {
        log.info("Internal error {}", e);
        return new ResponseEntity<>(new GeneralResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
