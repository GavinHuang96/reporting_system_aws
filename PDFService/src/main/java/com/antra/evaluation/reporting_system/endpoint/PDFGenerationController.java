package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.model.PDFRequest;
import com.antra.evaluation.reporting_system.model.PDFResponse;
import com.antra.evaluation.reporting_system.entity.PDFFile;
import com.antra.evaluation.reporting_system.service.PDFService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PDFGenerationController {
    private static final Logger log = LoggerFactory.getLogger(PDFGenerationController.class);

    private PDFService pdfService;

    @Autowired
    public PDFGenerationController(PDFService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/pdf")
    public ResponseEntity<PDFResponse> createPDF(@RequestBody @Validated PDFRequest request) {
        log.info("Got request to create file:{}", request);
        PDFResponse response = new PDFResponse();
        try {
            PDFFile file = pdfService.generateFile(request);
            response.setReqId(request.getReqId());
            response.setFileName(file.getFileName());
            response.setFileLocation(file.getFileLocation());
            response.setFileSize(file.getFileSize());
            response.setFailed(false);
            log.info("Successed in generating pdf {}", request.getReqId());
        } catch (Exception e) {
            response.setReqId(request.getReqId());
            response.setFailed(true);
            log.info("Error in generating pdf {}: {}", request.getReqId(), e);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/pdf/{id}")
    public ResponseEntity<PDFResponse> createExcel(@PathVariable String id, @RequestBody @Validated PDFRequest request) {
        log.info("Got request to update file:{}", request);
        PDFResponse response = new PDFResponse();
        try {
            PDFFile file = pdfService.updateFile(request, id);
            response.setReqId(id);
            response.setFileName(file.getFileName());
            response.setFileLocation(file.getFileLocation());
            response.setFileSize(file.getFileSize());
            response.setFailed(false);
            log.info("Successed in updating pdf {}", id);
        } catch (Exception e) {
            response.setReqId(id);
            response.setFailed(true);
            log.info("Error in updating pdf {}: {}", request.getReqId(), e);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/pdf/{id}")
    public ResponseEntity<PDFResponse> deleteExcel(@PathVariable String id) {
        log.info("Got request to delete file:{}", id);
        PDFResponse response = new PDFResponse();
        try {
            PDFFile file = pdfService.deleteFile(id);
            response.setReqId(id);
            response.setFileName(file.getFileName());
            response.setFileLocation(file.getFileLocation());
            response.setFileSize(file.getFileSize());
            response.setFailed(false);
            log.info("Successed in deleting pdf {}", id);
        } catch (Exception e) {
            response.setReqId(id);
            response.setFailed(true);
            log.info("Error in generating pdf {}: {}", id, e);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
