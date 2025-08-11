package com.project.purrsuit.job_application.repositories;

import com.project.purrsuit.job_application.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>, JpaSpecificationExecutor<JobApplication> {
    Page<JobApplication> findByUserId(Long userId, Pageable pageable);
    // add method to count how many applications of a user are in a given status
}
