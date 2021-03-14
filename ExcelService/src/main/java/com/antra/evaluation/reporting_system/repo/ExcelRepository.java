package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.entity.ExcelFile;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExcelRepository extends MongoRepository<ExcelFile, String> {
}
