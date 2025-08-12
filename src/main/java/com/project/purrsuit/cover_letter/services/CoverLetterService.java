package com.project.purrsuit.cover_letter.services;

import com.project.purrsuit.cover_letter.dtos.CoverLetterDTO;
import com.project.purrsuit.response.Response;

import java.util.List;

public interface CoverLetterService {
    Response<CoverLetterDTO> createCoverLetter(CoverLetterDTO coverLetterDTO);
    Response<CoverLetterDTO> updateCoverLetter(Long id, CoverLetterDTO coverLetterDTO);
    Response<?> deleteCoverLetter(Long id);
    Response<CoverLetterDTO> getCoverLetterById(Long id);
    Response<List<CoverLetterDTO>> getCoverLettersFromUser();
}
