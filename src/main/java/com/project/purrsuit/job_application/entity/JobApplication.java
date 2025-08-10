package com.project.purrsuit.job_application.entity;

import com.project.purrsuit.enums.JobPortal;
import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.status_record.entity.StatusRecord;
import com.project.purrsuit.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "job_applications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "companyName is required")
    private String companyName;

    private String postingUrl;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "jobPortal is required")
    private JobPortal jobPortal;

    @Lob
    @NotBlank(message = "jobDescription is required")
    private String jobDescription;

    @Lob
    private String additionalNotes;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "currentJobStatus is required")
    private JobStatus currentJobStatus;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusRecord> statusHistory = new ArrayList<>();
}
