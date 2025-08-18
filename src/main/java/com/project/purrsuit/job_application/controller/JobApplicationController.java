package com.project.purrsuit.job_application.controller;

import com.project.purrsuit.enums.JobPortal;
import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.job_application.dtos.JobApplicationDTO;
import com.project.purrsuit.job_application.services.JobApplicationService;
import com.project.purrsuit.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/applications")
@Slf4j
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping
    public ResponseEntity<Response<JobApplicationDTO>> createApplication(
            @RequestBody @Valid JobApplicationDTO jobApplicationDTO
    ) {
        return ResponseEntity.ok(jobApplicationService.createApplication(jobApplicationDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<JobApplicationDTO>> updateApplication(
            @PathVariable Long id,
            @RequestBody @Valid JobApplicationDTO jobApplicationDTO
    ){
        return ResponseEntity.ok(jobApplicationService.updateApplication(id, jobApplicationDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<JobApplicationDTO>> getApplicationById(@PathVariable Long id){
        return ResponseEntity.ok(jobApplicationService.getApplicationById(id));
    }

    @GetMapping
    public ResponseEntity<Response<Page<JobApplicationDTO>>> getApplications(
            @RequestParam(required = false) JobStatus jobStatus,
            @RequestParam(required = false) JobPortal jobPortal,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") Integer size
    ) {
        log.info("jobStatus = {}, jobPortal = {}, search = {}", jobStatus, jobPortal, search);
        return ResponseEntity.ok(jobApplicationService.getApplications (
                    jobStatus,
                    jobPortal,
                    search,
                    page,
                    size
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteApplicationById(@PathVariable Long id){
        return ResponseEntity.ok(jobApplicationService.deleteApplication(id));
    }
}
