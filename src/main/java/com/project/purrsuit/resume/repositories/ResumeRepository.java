package com.project.purrsuit.resume.repositories;

import com.project.purrsuit.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserIdOrderByIdDesc(Long userId);
}
