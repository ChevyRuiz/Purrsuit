package com.project.purrsuit.cover_letter.controller;

import com.project.purrsuit.cover_letter.dtos.CoverLetterDTO;
import com.project.purrsuit.cover_letter.services.CoverLetterService;
import com.project.purrsuit.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cover-letters")
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<CoverLetterDTO>> createCoverLetter (
            @ModelAttribute CoverLetterDTO coverLetterDTO,
            @RequestPart(value = "docFile", required = true) MultipartFile docFile
    ){
        coverLetterDTO.setDocumentFile(docFile);
        return ResponseEntity.ok(coverLetterService.createCoverLetter(coverLetterDTO));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<CoverLetterDTO>> updateCoverLetter (
            @PathVariable Long id,
            @ModelAttribute CoverLetterDTO coverLetterDTO,
            @RequestPart(value = "docFile", required = false) MultipartFile docFile
    ){
        coverLetterDTO.setDocumentFile(docFile);
        return ResponseEntity.ok(coverLetterService.updateCoverLetter(id, coverLetterDTO));
    }

    @GetMapping
    public ResponseEntity<Response<List<CoverLetterDTO>>> getCoverLetters() {
        return ResponseEntity.ok(coverLetterService.getCoverLettersFromUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<CoverLetterDTO>> getCoverLetterById(@PathVariable Long id){
        return ResponseEntity.ok(coverLetterService.getCoverLetterById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteCoverLetter(@PathVariable Long id){
        return ResponseEntity.ok(coverLetterService.deleteCoverLetter(id));
    }
}