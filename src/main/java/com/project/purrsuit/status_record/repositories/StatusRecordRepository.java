package com.project.purrsuit.status_record.repositories;

import com.project.purrsuit.status_record.entity.StatusRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRecordRepository extends JpaRepository<StatusRecord, Long> {
}
