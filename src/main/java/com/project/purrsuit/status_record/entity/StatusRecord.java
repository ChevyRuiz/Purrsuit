package com.project.purrsuit.status_record.entity;

import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.job_application.entity.JobApplication;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "status_records")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id")
    @NotNull(message = "jobApplication is required")
    private JobApplication application;

    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "jobStatus is required")
    private JobStatus jobStatus;

    @NotNull(message = "assignedAt is required")
    private LocalDateTime assignedAt;
}
