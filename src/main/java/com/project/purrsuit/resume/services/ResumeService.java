package com.project.purrsuit.resume.services;

import com.project.purrsuit.response.Response;
import com.project.purrsuit.resume.dtos.ResumeDTO;

import java.util.List;

public interface ResumeService {
    Response<ResumeDTO> createResume(ResumeDTO resumeDTO);
    Response<ResumeDTO> updateResume(Long id, ResumeDTO resumeDTO);
    Response<?> deleteResume(Long id);
    Response<ResumeDTO> getResumeById(Long id);
    Response<List<ResumeDTO>> getResumesFromUser();
}
