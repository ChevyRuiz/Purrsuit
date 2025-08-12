package com.project.purrsuit.resume.services;

import com.project.purrsuit.aws.AWSS3Service;
import com.project.purrsuit.exceptions.BadRequestException;
import com.project.purrsuit.exceptions.NotFoundException;
import com.project.purrsuit.response.Response;
import com.project.purrsuit.resume.dtos.ResumeDTO;
import com.project.purrsuit.resume.entity.Resume;
import com.project.purrsuit.resume.repositories.ResumeRepository;
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
public class ResumeServiceImpl implements ResumeService{

    private final ResumeRepository resumeRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final AWSS3Service awss3Service;

    @Override
    public Response<ResumeDTO> createResume(ResumeDTO resumeDTO) {

        log.info("INSIDE createResume");
        User user = userService.getCurrentLoggedInUser();

        MultipartFile docFile = resumeDTO.getDocumentFile();

        if (docFile == null || docFile.isEmpty()){
            throw new BadRequestException("A document file is required");
        }
        if (resumeDTO.getName() == null || resumeDTO.getName().isEmpty()){
            throw new BadRequestException("A file name is required");
        }

        String docFileName = UUID.randomUUID().toString() + "_" + docFile.getOriginalFilename();
        URL newFileUrl = awss3Service.uploadFile("resume/" + docFileName, docFile);

        Resume resume = Resume.builder()
                .name(resumeDTO.getName())
                .documentUrl(newFileUrl.toString())
                .user(user)
                .build();

        Resume savedResume = resumeRepository.save(resume);

        return Response.<ResumeDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("resume saved successfully")
                .data(modelMapper.map(savedResume, ResumeDTO.class))
                .build();
    }

    @Override
    public Response<ResumeDTO> updateResume(Long id, ResumeDTO resumeDTO) {
        log.info("INSIDE updateResume");

        User user = userService.getCurrentLoggedInUser();
        Resume existingResume = resumeRepository.findById(id).orElseThrow(() -> new NotFoundException("resume not found"));

        if (!Objects.equals(existingResume.getUser().getId(), user.getId())){
            throw new AccessDeniedException("resume does not belong to the current user");
        }

        MultipartFile docFile = resumeDTO.getDocumentFile();

        // If a new file was provided
        if (docFile != null && !docFile.isEmpty()){
            // Delete existing file if there was one
            if (existingResume.getDocumentUrl() != null && !existingResume.getDocumentUrl().isEmpty()) {
                String keyName = existingResume.getDocumentUrl().substring(existingResume.getDocumentUrl().lastIndexOf("/") + 1);
                awss3Service.deleteFile("resume/" + keyName);
                log.info("Deleted resume from s3");
            }

            String docName = UUID.randomUUID().toString() + "_" + docFile.getOriginalFilename();
            URL newDocUrl = awss3Service.uploadFile("resume/" + docName, docFile);
            existingResume.setDocumentUrl(newDocUrl.toString());
        }

        if (resumeDTO.getName() != null && !resumeDTO.getName().isEmpty()) existingResume.setName(resumeDTO.getName());

        Resume updatedResume = resumeRepository.save(existingResume);

        return Response.<ResumeDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("resume updated successfully")
                .data(modelMapper.map(updatedResume, ResumeDTO.class))
                .build();
    }

    @Override
    public Response<?> deleteResume(Long id) {

        User user = userService.getCurrentLoggedInUser();
        Resume existingResume = resumeRepository.findById(id).orElseThrow(() -> new NotFoundException("resume not found"));

        if (!Objects.equals(existingResume.getUser().getId(), user.getId())){
            throw new AccessDeniedException("resume does not belong to the current user");
        }

        // Delete file from cloud
        String keyName = existingResume.getDocumentUrl().substring(existingResume.getDocumentUrl().lastIndexOf("/") + 1);
        awss3Service.deleteFile("resume/" + keyName);
        log.info("Deleted resume from s3");

        resumeRepository.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("resume deleted successfully")
                .build();
    }

    @Override
    public Response<ResumeDTO> getResumeById(Long id) {
        User user = userService.getCurrentLoggedInUser();
        Resume existingResume = resumeRepository.findById(id).orElseThrow(() -> new NotFoundException("resume not found"));

        if (!Objects.equals(existingResume.getUser().getId(), user.getId())){
            throw new AccessDeniedException("resume does not belong to the current user");
        }

        return Response.<ResumeDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("resume retrieved successfully")
                .data(modelMapper.map(existingResume, ResumeDTO.class))
                .build();
    }

    @Override
    public Response<List<ResumeDTO>> getResumesFromUser() {
        log.info("INSIDE getResumesFromUser");
        User user = userService.getCurrentLoggedInUser();
        List<Resume> resumes = resumeRepository.findByUserIdOrderByIdDesc(user.getId());
        List<ResumeDTO> resumeDTOS = resumes.stream()
                .map(r -> modelMapper.map(r, ResumeDTO.class))
                .toList();

        return Response.<List<ResumeDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All resumes retrieved successfully")
                .data(resumeDTOS)
                .build();
    }
}
