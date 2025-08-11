package com.project.purrsuit.status_record.services;

import com.project.purrsuit.response.Response;
import com.project.purrsuit.status_record.dtos.StatusRecordDTO;
import com.project.purrsuit.status_record.entity.StatusRecord;

import java.util.List;

public interface StatusRecordService {
    List<StatusRecordDTO> findAllByApplicationId(Long applicationId);
}
