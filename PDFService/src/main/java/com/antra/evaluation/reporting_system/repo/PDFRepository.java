package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.entity.PDFFile;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PDFRepository extends MongoRepository<PDFFile, String> {
}
