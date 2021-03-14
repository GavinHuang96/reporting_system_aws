package com.antra.report.client.repository;

import com.antra.report.client.entity.ReportRequestEntity;
import com.antra.report.client.pojo.type.RequestStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRequestRepo extends JpaRepository<ReportRequestEntity, String> {
    List<ReportRequestEntity> findByUserIdAndStatusOrderByCreatedTimeAsc(int userId, RequestStatus status);
}
