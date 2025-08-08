package com.project.purrsuit.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @NotBlank(message = "password is required")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<JobApplication> jobApplications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Resume> resumes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CoverLetter> coverLetters;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
