package com.project.purrsuit.status_record.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.job_application.entity.JobApplication;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusRecordDTO {
    private Long id;

    private Long applicationId;

    @Enumerated(value = EnumType.STRING)
    private JobStatus jobStatus;

    private LocalDateTime assignedAt;
}
