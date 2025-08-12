package com.project.purrsuit.resume.controller;

import com.project.purrsuit.response.Response;
import com.project.purrsuit.resume.dtos.ResumeDTO;
import com.project.purrsuit.resume.services.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<ResumeDTO>> createResume (
            @ModelAttribute ResumeDTO resumeDTO,
            @RequestPart(value = "docFile", required = true) MultipartFile docFile
    ){
        resumeDTO.setDocumentFile(docFile);
        return ResponseEntity.ok(resumeService.createResume(resumeDTO));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<ResumeDTO>> updateResume (
            @PathVariable Long id,
            @ModelAttribute ResumeDTO resumeDTO,
            @RequestPart(value = "docFile", required = false) MultipartFile docFile
    ){
        resumeDTO.setDocumentFile(docFile);
        return ResponseEntity.ok(resumeService.updateResume(id, resumeDTO));
    }

    @GetMapping
    public ResponseEntity<Response<List<ResumeDTO>>> getResumes() {
        return ResponseEntity.ok(resumeService.getResumesFromUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ResumeDTO>> getResumeById(@PathVariable Long id){
        return ResponseEntity.ok(resumeService.getResumeById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteResume(@PathVariable Long id){
        return ResponseEntity.ok(resumeService.deleteResume(id));
    }

}
