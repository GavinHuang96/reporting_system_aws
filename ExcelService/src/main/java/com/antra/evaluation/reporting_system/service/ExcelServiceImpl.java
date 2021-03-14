package com.antra.evaluation.reporting_system.service;

import com.amazonaws.services.s3.AmazonS3;
import com.antra.evaluation.reporting_system.model.ExcelRequest;
import com.antra.evaluation.reporting_system.model.FileStatus;
import com.antra.evaluation.reporting_system.entity.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExcelServiceImpl implements ExcelService {
    private ExcelGenerationService excelGenerationService;

    ExcelRepository excelRepository;

    private final AmazonS3 s3Client;

    @Value("${s3.bucket}")
    private String s3Bucket;

    @Autowired
    public ExcelServiceImpl(ExcelGenerationService excelGenerationService, ExcelRepository excelRepository, AmazonS3 s3Client) {
        this.excelGenerationService = excelGenerationService;
        this.excelRepository = excelRepository;
        this.s3Client = s3Client;
    }

    @Override
    public ExcelFile generateFile(ExcelRequest request) {
        File temp = new File(UUID.randomUUID().toString() + ".xlsx");

        // generate file
        excelGenerationService.generate(request, temp);

        // upload file
        s3Client.putObject(s3Bucket, temp.getName(), temp);

        // save entity to database
        ExcelFile file = new ExcelFile();
        file.setId(request.getReqId());
        file.setFileName(temp.getName());
        file.setFileLocation(String.join("/", s3Bucket, file.getFileName()));
        file.setFileSize(temp.length());
        file.setSubmitter(request.getSubmitter());
        file.setDescription(request.getDescription());
        file.setStatus(FileStatus.AVAILABLE);
        file.setGeneratedTime(LocalDateTime.now());
        file.setUpdatedTime(LocalDateTime.now());
        excelRepository.save(file);

        // delete file
        temp.delete();

        return file;
    }

    @Override
    public ExcelFile updateFile(ExcelRequest request, String id) {
        ExcelFile file = excelRepository.findById(id).orElseThrow();

        // delete old file
        s3Client.deleteObject(s3Bucket, file.getFileName());

        // generate new file
        File temp = new File(file.getFileName());
        excelGenerationService.generate(request, temp);

        // upload new file
        s3Client.putObject(s3Bucket, temp.getName(), temp);

        // update entity
        file.setFileSize(temp.length());
        file.setDescription(request.getDescription());
        file.setStatus(FileStatus.AVAILABLE);
        file.setUpdatedTime(LocalDateTime.now());
        excelRepository.save(file);

        // delete file
        temp.delete();

        return file;
    }

    @Override
    public ExcelFile deleteFile(String id) {
        // update entity
        ExcelFile file = excelRepository.findById(id).orElseThrow();
        file.setStatus(FileStatus.DELETED);
        file.setUpdatedTime(LocalDateTime.now());
        excelRepository.save(file);

        // delete file
        s3Client.deleteObject(s3Bucket, file.getFileName());
        return file;
    }
}
