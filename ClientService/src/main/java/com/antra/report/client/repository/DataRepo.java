package com.antra.report.client.repository;

import com.antra.report.client.entity.DataEntity;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataRepo extends MongoRepository<DataEntity, String> {
}