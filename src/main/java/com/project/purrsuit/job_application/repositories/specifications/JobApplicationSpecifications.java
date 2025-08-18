package com.project.purrsuit.job_application.repositories.specifications;

import com.project.purrsuit.enums.JobPortal;
import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.job_application.entity.JobApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;

@Slf4j
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
    public static Specification<JobApplication> containsInNameOrCompanyOrDescription(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) {
                log.debug("Search term is null or blank -> returning null predicate");
                return null;
            }

            log.debug("Search term: '{}'", search);

            HibernateCriteriaBuilder hcb = (HibernateCriteriaBuilder) criteriaBuilder;
            String pattern = "%" + search + "%";
            log.debug("SQL LIKE pattern: {}", pattern);

            return criteriaBuilder.or(
                    hcb.ilike(root.get("name"), pattern),
                    hcb.ilike(root.get("companyName"), pattern),
                    hcb.ilike(root.get("jobDescription").as(String.class), pattern)
            );
        };
    }
}
