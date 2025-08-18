package com.project.purrsuit.job_application.services;

import com.project.purrsuit.enums.JobPortal;
import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.exceptions.BadRequestException;
import com.project.purrsuit.exceptions.NotFoundException;
import com.project.purrsuit.job_application.dtos.JobApplicationDTO;
import com.project.purrsuit.job_application.entity.JobApplication;
import com.project.purrsuit.job_application.repositories.JobApplicationRepository;
import com.project.purrsuit.job_application.repositories.specifications.JobApplicationSpecifications;
import com.project.purrsuit.response.Response;
import com.project.purrsuit.status_record.dtos.StatusRecordDTO;
import com.project.purrsuit.status_record.entity.StatusRecord;
import com.project.purrsuit.status_record.repositories.StatusRecordRepository;
import com.project.purrsuit.user.entity.User;
import com.project.purrsuit.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobApplicationServiceImpl implements JobApplicationService{

    private final JobApplicationRepository jobApplicationRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response<JobApplicationDTO> createApplication(JobApplicationDTO applicationDTO) {

        log.info("Inside createApplication");
        // Get the current user
        User user = userService.getCurrentLoggedInUser();

        // If no status was given, set it to Applied
        JobStatus applicationStatus;
        if(applicationDTO.getCurrentJobStatus() == null){
            applicationStatus = JobStatus.APPLIED;
        } else {
            applicationStatus = applicationDTO.getCurrentJobStatus();
        }

        // Create the first record
        StatusRecord firstRecord = StatusRecord.builder()
                .jobStatus(applicationStatus)
                .assignedAt(LocalDateTime.now())
                .build();

        // Build the application
        JobApplication jobApplication = JobApplication.builder()
                .user(user)
                .name(applicationDTO.getName())
                .companyName(applicationDTO.getCompanyName())
                .postingUrl(applicationDTO.getPostingUrl())
                .jobPortal(applicationDTO.getJobPortal())
                .jobDescription(applicationDTO.getJobDescription())
                .additionalNotes(applicationDTO.getAdditionalNotes())
                .currentJobStatus(applicationStatus)
                .statusHistory(new ArrayList<>())
                .build();

        // Manually link both sides
        firstRecord.setApplication(jobApplication);
        jobApplication.getStatusHistory().add(firstRecord);

        JobApplication savedApplication = jobApplicationRepository.save(jobApplication);
        JobApplicationDTO savedApplicationDTO = modelMapper.map(savedApplication, JobApplicationDTO.class);

        return Response.<JobApplicationDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Application saved succesfully")
                .data(savedApplicationDTO)
                .build();
    }

    @Override
    public Response<JobApplicationDTO> updateApplication(Long id, JobApplicationDTO applicationDTO) {
        log.info("Inside updateApplication");

        User user = userService.getCurrentLoggedInUser();
        // Get existing application
        JobApplication existingApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application was not found"));

        if (!Objects.equals(existingApplication.getUser().getId(), user.getId())){
            throw new AccessDeniedException("application doesn't belong to the current user");
        }
        // Update the given fields
        if (applicationDTO.getName() != null && !applicationDTO.getName().isBlank())
            existingApplication.setName(applicationDTO.getName());
        if (applicationDTO.getCompanyName() != null && !applicationDTO.getCompanyName().isBlank())
            existingApplication.setCompanyName(applicationDTO.getCompanyName());
        if (applicationDTO.getPostingUrl() != null && !applicationDTO.getPostingUrl().isBlank())
            existingApplication.setPostingUrl(applicationDTO.getPostingUrl());
        if (applicationDTO.getJobPortal() != null)
            existingApplication.setJobPortal(applicationDTO.getJobPortal());
        if (applicationDTO.getJobDescription() != null && !applicationDTO.getJobDescription().isBlank())
            existingApplication.setJobDescription(applicationDTO.getJobDescription());
        if (applicationDTO.getAdditionalNotes() != null && !applicationDTO.getAdditionalNotes().isBlank())
            existingApplication.setAdditionalNotes(applicationDTO.getAdditionalNotes());

        // Set status and update history
        if (applicationDTO.getCurrentJobStatus() != null && applicationDTO.getCurrentJobStatus() != existingApplication.getCurrentJobStatus()){
            existingApplication.setCurrentJobStatus(applicationDTO.getCurrentJobStatus());
            StatusRecord latestRecord = StatusRecord.builder()
                    .jobStatus(applicationDTO.getCurrentJobStatus())
                    .application(existingApplication)
                    .assignedAt(LocalDateTime.now())
                    .build();

            existingApplication.getStatusHistory().add(latestRecord);
        }

        JobApplication updatedApplication = jobApplicationRepository.save(existingApplication);

        return Response.<JobApplicationDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Application updated successfully")
                .data(modelMapper.map(updatedApplication, JobApplicationDTO.class))
                .build();
    }

    @Override
    public Response<JobApplicationDTO> getApplicationById(Long id) {
        log.info("Inside getApplicationById");

        User user = userService.getCurrentLoggedInUser();

        JobApplication jobApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("application not found"));

        if (!Objects.equals(jobApplication.getUser().getId(), user.getId())){
            throw new AccessDeniedException("application doesn't belong to the current user");
        }
        JobApplicationDTO jobApplicationDTO = modelMapper.map(jobApplication, JobApplicationDTO.class);

        if (jobApplicationDTO.getStatusHistory() != null)
            jobApplicationDTO.getStatusHistory().sort(Comparator.comparing(StatusRecordDTO::getAssignedAt).reversed());

        return Response.<JobApplicationDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Application retrieved successfully")
                .data(jobApplicationDTO)
                .build();
    }

    @Override
    public Response<?> deleteApplication(Long id) {
        log.info("Inside deleteApplication");
        User user = userService.getCurrentLoggedInUser();

        JobApplication jobApplication = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("application not found"));

        if (!Objects.equals(jobApplication.getUser().getId(), user.getId())){
            throw new AccessDeniedException("application doesn't belong to the current user");
        }
        jobApplicationRepository.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Application deleted successfully")
                .build();
    }

    @Override
    public Response<Page<JobApplicationDTO>> getApplications(
            JobStatus status,
            JobPortal jobPortal,
            String search,
            int page,
            int size
    ) {

        log.info("getApplications");

        User user = userService.getCurrentLoggedInUser();

        Specification<JobApplication> spec = null;
        log.info(search);
        if (user.getId() != null) {
            spec = JobApplicationSpecifications.hasUserId(user.getId());
        }

        if (status != null) {
            spec = (spec == null) ? JobApplicationSpecifications.hasStatus(status) : spec.and(JobApplicationSpecifications.hasStatus(status));
        }

        if (jobPortal != null) {
            spec = (spec == null) ? JobApplicationSpecifications.hasJobPortal(jobPortal) : spec.and(JobApplicationSpecifications.hasJobPortal(jobPortal));
        }

        if (search != null && !search.isBlank()) {
            spec = (spec == null) ? JobApplicationSpecifications.containsInNameOrCompanyOrDescription(search) : spec.and(JobApplicationSpecifications.containsInNameOrCompanyOrDescription(search));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<JobApplication> applicationPage = jobApplicationRepository.findAll(spec, pageable);

        Page<JobApplicationDTO> dtoPage = applicationPage.map(app -> modelMapper.map(app, JobApplicationDTO.class));

        log.info(search);
        return Response.<Page<JobApplicationDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Applications retrieved successfully")
                .data(dtoPage)
                .build();
    }


}

// Add pagination