package com.project.purrsuit.job_application.repositories.specifications;

import com.project.purrsuit.enums.JobPortal;
import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.job_application.entity.JobApplication;
import org.springframework.data.jpa.domain.Specification;

public class JobApplicationSpecifications {

    public static Specification<JobApplication> hasUserId(Long userId){
        return (root, query, criteriaBuilder) -> {
            if (userId == null){
                return null;
            }
            return criteriaBuilder.equal(root.get("user").get("id"), userId);
        };
    }
    public static Specification<JobApplication> hasStatus(JobStatus status){
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("currentJobStatus"), status);
    }
    public static Specification<JobApplication> hasJobPortal(JobPortal jobPortal){
        return (root, query, criteriaBuilder) ->
                jobPortal == null ? null : criteriaBuilder.equal(root.get("jobPortal"), jobPortal);
    }
    public static Specification<JobApplication> containsInNameOrCompanyOrDescription(String search){
        return (root, query, criteriaBuilder) -> {
          if (search == null){
              return null;
          }
          String pattern = "%" + search.toLowerCase() + "%";
          return criteriaBuilder.or(
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), pattern),
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("jobDescription")), pattern)
          );
        };
    }
}
