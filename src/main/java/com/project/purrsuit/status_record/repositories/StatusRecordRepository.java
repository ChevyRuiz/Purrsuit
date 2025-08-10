package com.project.purrsuit.status_record.repositories;

import com.project.purrsuit.status_record.entity.StatusRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRecordRepository extends JpaRepository<StatusRecord, Long> {
    List<StatusRecord> findByApplicationIdOrderByAssignedAtDesc(Long applicationId);
}
