package com.antra.evaluation.reporting_system.service;

import com.amazonaws.services.s3.AmazonS3;
import com.antra.evaluation.reporting_system.model.FileStatus;
import com.antra.evaluation.reporting_system.model.PDFRequest;
import com.antra.evaluation.reporting_system.entity.PDFFile;
import com.antra.evaluation.reporting_system.repo.PDFRepository;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PDFServiceImpl implements PDFService {
    private final PDFGenerationService generationService;

    private final PDFRepository repository;

    private final AmazonS3 s3Client;

    @Value("${s3.bucket}")
    private String s3Bucket;

    public PDFServiceImpl(PDFGenerationService generationService, PDFRepository repository, AmazonS3 s3Client) {
        this.generationService = generationService;
        this.repository = repository;
        this.s3Client = s3Client;
    }

    @Override
    public PDFFile generateFile(final PDFRequest request) {
        File temp = new File(UUID.randomUUID().toString() + ".pdf");

        // generate file
        generationService.generate(request, temp);

        // upload file
        s3Client.putObject(s3Bucket, temp.getName(), temp);

        // save entity to database
        PDFFile file = new PDFFile();
        file.setId(request.getReqId());
        file.setFileName(temp.getName());
        file.setFileLocation(String.join("/", s3Bucket, file.getFileName()));
        file.setFileSize(temp.length());
        file.setSubmitter(request.getSubmitter());
        file.setDescription(request.getDescription());
        file.setStatus(FileStatus.AVAILABLE);
        file.setGeneratedTime(LocalDateTime.now());
        file.setUpdatedTime(LocalDateTime.now());
        repository.save(file);

        // delete file
        temp.delete();

        return file;
    }

    @Override
    public PDFFile updateFile(PDFRequest request, String id) {
        PDFFile file = repository.findById(id).orElseThrow();

        // delete old file
        s3Client.deleteObject(s3Bucket, file.getFileName());

        // generate new file
        File temp = new File(file.getFileName());
        generationService.generate(request, temp);

        // upload new file
        s3Client.putObject(s3Bucket, temp.getName(), temp);

        // update entity
        file.setFileSize(temp.length());
        file.setDescription(request.getDescription());
        file.setStatus(FileStatus.AVAILABLE);
        file.setUpdatedTime(LocalDateTime.now());
        repository.save(file);

        // delete file
        temp.delete();

        return file;
    }

    @Override
    public PDFFile deleteFile(String id) {
        // update entity
        PDFFile file = repository.findById(id).orElseThrow();
        file.setStatus(FileStatus.DELETED);
        file.setUpdatedTime(LocalDateTime.now());
        repository.save(file);

        // delete file
        s3Client.deleteObject(s3Bucket, file.getFileName());
        return file;
    }
}