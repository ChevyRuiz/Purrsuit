package com.project.purrsuit.services;

import com.project.purrsuit.enums.JobPortal;
import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.exceptions.NotFoundException;
import com.project.purrsuit.job_application.dtos.JobApplicationDTO;
import com.project.purrsuit.job_application.entity.JobApplication;
import com.project.purrsuit.job_application.repositories.JobApplicationRepository;
import com.project.purrsuit.job_application.services.JobApplicationService;
import com.project.purrsuit.response.Response;
import com.project.purrsuit.user.entity.User;
import com.project.purrsuit.user.repositories.UserRepository;
import com.project.purrsuit.user.services.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

// Comment out h2 db

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JobApplicationServiceImplTest {

    @Autowired
    private JobApplicationService jobApplicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @MockitoBean
    private UserService userService;  // mock only this to control logged in user

    private User testUser;

    @BeforeEach
    void setupUser() {
        jobApplicationRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password"); // encode if needed
        userRepository.save(testUser);
    }

    @Test
    void createApplication_shouldPersistAndReturnCorrectDto() {
        // Arrange
        JobApplicationDTO inputDto = JobApplicationDTO.builder()
                .name("Backend Developer")
                .companyName("OpenAI")
                .jobPortal(JobPortal.EXTERNAL_POSTING)
                .jobDescription("Backend work")
                .currentJobStatus(JobStatus.APPLIED)
                .build();

        Mockito.when(userService.getCurrentLoggedInUser()).thenReturn(testUser);

        // Act
        Response<JobApplicationDTO> response = jobApplicationService.createApplication(inputDto);
        System.out.println("Response: " + response);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData().getId()); // persisted ID assigned
        assertEquals("OpenAI", response.getData().getCompanyName());

        // Check DB directly
        JobApplication savedEntity = jobApplicationRepository.findById(response.getData().getId()).orElseThrow();
        assertEquals(testUser.getId(), savedEntity.getUser().getId());
        assertFalse(savedEntity.getStatusHistory().isEmpty());
        assertEquals(JobStatus.APPLIED, savedEntity.getCurrentJobStatus());
    }

    @Test
    void updateApplication_shouldPersistAndReturnUpdatedApplication_withTwoStatusHistoryRecords() {
        // Save first
        JobApplicationDTO inputDto = JobApplicationDTO.builder()
                .name("Backend Developer")
                .companyName("OpenAI")
                .jobPortal(JobPortal.EXTERNAL_POSTING)
                .jobDescription("Backend work")
                .currentJobStatus(JobStatus.APPLIED)
                .build();

        Mockito.when(userService.getCurrentLoggedInUser()).thenReturn(testUser);

        // Act
        JobApplicationDTO originalApplicationDTO = jobApplicationService.createApplication(inputDto).getData();

        originalApplicationDTO.setName("Updated Name");
        originalApplicationDTO.setCompanyName("Updated company name");
        originalApplicationDTO.setJobPortal(JobPortal.COOP_BOARD);
        originalApplicationDTO.setJobDescription("updated description");
        originalApplicationDTO.setCurrentJobStatus(JobStatus.INTERVIEW);

        Response<JobApplicationDTO> response = jobApplicationService.updateApplication(originalApplicationDTO.getId(), originalApplicationDTO);

        System.out.println("Response: " + response);

        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getData());
        assertEquals(originalApplicationDTO.getId(), response.getData().getId());
        assertEquals("Updated Name", response.getData().getName());
        assertEquals("Updated company name", response.getData().getCompanyName());
        assertEquals("updated description", response.getData().getJobDescription());
        assertEquals(originalApplicationDTO.getCurrentJobStatus(), response.getData().getCurrentJobStatus());
        assertEquals(originalApplicationDTO.getJobPortal(), response.getData().getJobPortal());
        assertEquals(2, response.getData().getStatusHistory().size());
    }

    @Test
    void getApplication_shouldReturnApplicationById() {
        // Save first
        JobApplicationDTO inputDto = JobApplicationDTO.builder()
                .name("Backend Developer")
                .companyName("OpenAI")
                .jobPortal(JobPortal.EXTERNAL_POSTING)
                .jobDescription("Backend work")
                .currentJobStatus(JobStatus.APPLIED)
                .build();

        Mockito.when(userService.getCurrentLoggedInUser()).thenReturn(testUser);

        // Act
        JobApplicationDTO originalApplicationDTO = jobApplicationService.createApplication(inputDto).getData();
        Response<JobApplicationDTO>  response = jobApplicationService.getApplicationById(originalApplicationDTO.getId());

        assertEquals(200, response.getStatusCode());
        System.out.println("Response: " + response);
    }

    @Test
    void deleteApplicationById_shouldReturnNotFound() {
        // Save first
        JobApplicationDTO inputDto = JobApplicationDTO.builder()
                .name("Backend Developer")
                .companyName("OpenAI")
                .jobPortal(JobPortal.EXTERNAL_POSTING)
                .jobDescription("Backend work")
                .currentJobStatus(JobStatus.APPLIED)
                .build();

        Mockito.when(userService.getCurrentLoggedInUser()).thenReturn(testUser);

        // Act
        JobApplicationDTO originalApplicationDTO = jobApplicationService.createApplication(inputDto).getData();
        Response<?>  response = jobApplicationService.deleteApplication(originalApplicationDTO.getId());

        assertThrows(NotFoundException.class, () -> {
            jobApplicationService.getApplicationById(originalApplicationDTO.getId());
        });
    }

}


