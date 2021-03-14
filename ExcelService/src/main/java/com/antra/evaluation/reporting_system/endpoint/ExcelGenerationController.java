package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.model.ExcelRequest;
import com.antra.evaluation.reporting_system.model.ExcelResponse;
import com.antra.evaluation.reporting_system.entity.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExcelGenerationController {
    private static final Logger log = LoggerFactory.getLogger(ExcelGenerationController.class);

    ExcelService excelService;

    @Autowired
    public ExcelGenerationController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/excel")
    public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated ExcelRequest request) {
        log.info("Got request to create file:{}", request);
        ExcelResponse response = new ExcelResponse();
        try {
            ExcelFile file = excelService.generateFile(request);
            response.setReqId(request.getReqId());
            response.setFileName(file.getFileName());
            response.setFileLocation(file.getFileLocation());
            response.setFileSize(file.getFileSize());
            response.setFailed(false);
            log.info("Successed in generating excel {}", request.getReqId());
        } catch (Exception e) {
            response.setReqId(request.getReqId());
            response.setFailed(true);
            log.info("Error in generating excel {}: {}", request.getReqId(), e);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/excel/{id}")
    public ResponseEntity<ExcelResponse> createExcel(@PathVariable String id, @RequestBody @Validated ExcelRequest request) {
        log.info("Got request to update file:{}", request);
        ExcelResponse response = new ExcelResponse();
        try {
            ExcelFile file = excelService.updateFile(request, id);
            response.setReqId(id);
            response.setFileName(file.getFileName());
            response.setFileLocation(file.getFileLocation());
            response.setFileSize(file.getFileSize());
            response.setFailed(false);
            log.info("Successed in updating excel {}", id);
        } catch (Exception e) {
            response.setReqId(id);
            response.setFailed(true);
            log.info("Error in updating excel {}: {}", request.getReqId(), e);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/excel/{id}")
    public ResponseEntity<ExcelResponse> deleteExcel(@PathVariable String id) {
        log.info("Got request to delete file:{}", id);
        ExcelResponse response = new ExcelResponse();
        try {
            ExcelFile file = excelService.deleteFile(id);
            response.setReqId(id);
            response.setFileName(file.getFileName());
            response.setFileLocation(file.getFileLocation());
            response.setFileSize(file.getFileSize());
            response.setFailed(false);
            log.info("Successed in deleting excel {}", id);
        } catch (Exception e) {
            response.setReqId(id);
            response.setFailed(true);
            log.info("Error in generating excel {}: {}", id, e);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
