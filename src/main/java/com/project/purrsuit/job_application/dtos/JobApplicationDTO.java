package com.project.purrsuit.job_application.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.purrsuit.enums.JobPortal;
import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.status_record.dtos.StatusRecordDTO;
import com.project.purrsuit.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobApplicationDTO {

    private Long id;
    private String name;
    private String companyName;

    private String postingUrl;

    @Enumerated(EnumType.STRING)
    private JobPortal jobPortal;

    private String jobDescription;

    private String additionalNotes;

    @Enumerated(EnumType.STRING)
    private JobStatus currentJobStatus;

    private List<StatusRecordDTO> statusHistory;

}
