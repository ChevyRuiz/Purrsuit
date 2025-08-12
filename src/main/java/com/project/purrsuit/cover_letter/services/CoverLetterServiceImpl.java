package com.project.purrsuit.cover_letter.services;

import com.project.purrsuit.aws.AWSS3Service;
import com.project.purrsuit.cover_letter.dtos.CoverLetterDTO;
import com.project.purrsuit.cover_letter.entity.CoverLetter;
import com.project.purrsuit.cover_letter.repositories.CoverLetterRepository;
import com.project.purrsuit.exceptions.BadRequestException;
import com.project.purrsuit.exceptions.NotFoundException;
import com.project.purrsuit.response.Response;
import com.project.purrsuit.user.entity.User;
import com.project.purrsuit.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoverLetterServiceImpl implements CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final AWSS3Service awss3Service;

    @Override
    public Response<CoverLetterDTO> createCoverLetter(CoverLetterDTO coverLetterDTO) {

        log.info("INSIDE createCoverLetter");
        User user = userService.getCurrentLoggedInUser();

        MultipartFile docFile = coverLetterDTO.getDocumentFile();

        if (docFile == null || docFile.isEmpty()){
            throw new BadRequestException("A document file is required");
        }
        if (coverLetterDTO.getName() == null || coverLetterDTO.getName().isEmpty()){
            throw new BadRequestException("A file name is required");
        }

        String docFileName = UUID.randomUUID().toString() + "_" + docFile.getOriginalFilename();
        URL newFileUrl = awss3Service.uploadFile("cover-letter/" + docFileName, docFile);

        CoverLetter coverLetter = CoverLetter.builder()
                .name(coverLetterDTO.getName())
                .documentUrl(newFileUrl.toString())
                .user(user)
                .build();

        CoverLetter savedCoverLetter = coverLetterRepository.save(coverLetter);

        return Response.<CoverLetterDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("cover letter saved successfully")
                .data(modelMapper.map(savedCoverLetter, CoverLetterDTO.class))
                .build();
    }

    @Override
    public Response<CoverLetterDTO> updateCoverLetter(Long id, CoverLetterDTO coverLetterDTO) {
        log.info("INSIDE updateCoverLetter");

        User user = userService.getCurrentLoggedInUser();
        CoverLetter existingCoverLetter = coverLetterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("cover letter not found"));

        if (!Objects.equals(existingCoverLetter.getUser().getId(), user.getId())){
            throw new AccessDeniedException("cover letter does not belong to the current user");
        }

        MultipartFile docFile = coverLetterDTO.getDocumentFile();

        // If a new file was provided
        if (docFile != null && !docFile.isEmpty()){
            // Delete existing file if there was one
            if (existingCoverLetter.getDocumentUrl() != null && !existingCoverLetter.getDocumentUrl().isEmpty()) {
                String keyName = existingCoverLetter.getDocumentUrl()
                        .substring(existingCoverLetter.getDocumentUrl().lastIndexOf("/") + 1);
                awss3Service.deleteFile("cover-letter/" + keyName);
                log.info("Deleted cover letter from s3");
            }

            String docName = UUID.randomUUID().toString() + "_" + docFile.getOriginalFilename();
            URL newDocUrl = awss3Service.uploadFile("cover-letter/" + docName, docFile);
            existingCoverLetter.setDocumentUrl(newDocUrl.toString());
        }

        if (coverLetterDTO.getName() != null && !coverLetterDTO.getName().isEmpty()) {
            existingCoverLetter.setName(coverLetterDTO.getName());
        }

        CoverLetter updatedCoverLetter = coverLetterRepository.save(existingCoverLetter);

        return Response.<CoverLetterDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("cover letter updated successfully")
                .data(modelMapper.map(updatedCoverLetter, CoverLetterDTO.class))
                .build();
    }

    @Override
    public Response<?> deleteCoverLetter(Long id) {

        User user = userService.getCurrentLoggedInUser();
        CoverLetter existingCoverLetter = coverLetterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("cover letter not found"));

        if (!Objects.equals(existingCoverLetter.getUser().getId(), user.getId())){
            throw new AccessDeniedException("cover letter does not belong to the current user");
        }

        // Delete file from cloud
        String keyName = existingCoverLetter.getDocumentUrl()
                .substring(existingCoverLetter.getDocumentUrl().lastIndexOf("/") + 1);
        awss3Service.deleteFile("cover-letter/" + keyName);
        log.info("Deleted cover letter from s3");

        coverLetterRepository.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("cover letter deleted successfully")
                .build();
    }

    @Override
    public Response<CoverLetterDTO> getCoverLetterById(Long id) {
        User user = userService.getCurrentLoggedInUser();
        CoverLetter existingCoverLetter = coverLetterRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("cover letter not found"));

        if (!Objects.equals(existingCoverLetter.getUser().getId(), user.getId())){
            throw new AccessDeniedException("cover letter does not belong to the current user");
        }

        return Response.<CoverLetterDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("cover letter retrieved successfully")
                .data(modelMapper.map(existingCoverLetter, CoverLetterDTO.class))
                .build();
    }

    @Override
    public Response<List<CoverLetterDTO>> getCoverLettersFromUser() {
        log.info("INSIDE getCoverLettersFromUser");
        User user = userService.getCurrentLoggedInUser();
        List<CoverLetter> coverLetters = coverLetterRepository.findByUserIdOrderByIdDesc(user.getId());
        List<CoverLetterDTO> coverLetterDTOS = coverLetters.stream()
                .map(r -> modelMapper.map(r, CoverLetterDTO.class))
                .toList();

        return Response.<List<CoverLetterDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All cover letters retrieved successfully")
                .data(coverLetterDTOS)
                .build();
    }
}
