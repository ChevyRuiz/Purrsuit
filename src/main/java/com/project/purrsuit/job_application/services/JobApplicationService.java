package com.project.purrsuit.job_application.services;

import com.project.purrsuit.enums.JobPortal;
import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.job_application.dtos.JobApplicationDTO;
import com.project.purrsuit.response.Response;
import org.springframework.data.domain.Page;


public interface JobApplicationService {

    Response<JobApplicationDTO> createApplication(JobApplicationDTO applicationDTO);
    Response<JobApplicationDTO> updateApplication(Long id, JobApplicationDTO applicationDTO);
    Response<JobApplicationDTO> getApplicationById(Long id);
    Response<?> deleteApplication(Long id);
    Response<Page<JobApplicationDTO>> getApplications(
            JobStatus status,
            JobPortal jobPortal,
            String search,
            int page,
            int size
    );
}
