package com.project.purrsuit.resume.repositories;

import com.project.purrsuit.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Book;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
