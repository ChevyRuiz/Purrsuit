package com.project.purrsuit.cover_letter.repositories;

import com.project.purrsuit.cover_letter.entity.CoverLetter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {
    List<CoverLetter> findByUserIdOrderByIdDesc(Long userId);
}
