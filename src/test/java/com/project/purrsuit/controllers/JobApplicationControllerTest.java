package com.project.purrsuit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.purrsuit.enums.JobPortal;
import com.project.purrsuit.enums.JobStatus;
import com.project.purrsuit.job_application.controller.JobApplicationController;
import com.project.purrsuit.job_application.dtos.JobApplicationDTO;
import com.project.purrsuit.job_application.entity.JobApplication;
import com.project.purrsuit.job_application.services.JobApplicationService;
import com.project.purrsuit.response.Response;
import com.project.purrsuit.user.entity.User;
import com.project.purrsuit.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobApplicationService jobApplicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    private Response<JobApplicationDTO> sampleResponse;
    private JobApplicationDTO sampleDTO;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        if (!userRepository.existsByUsername("testuser")) {
            userRepository.save(User.builder()
                    .username("testuser")
                    .password("encodedPassword") // or encode using your PasswordEncoder if needed
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        JobApplication sampleApplication = JobApplication.builder()
                .id(1L)
                .name("Sample Name")
                .companyName("Sample Company")
                .postingUrl("https://careers.openai.com/jobs/backend-developer")
                .jobPortal(JobPortal.EXTERNAL_POSTING)
                .jobDescription("Responsible for designing and implementing backend services in Java/Spring Boot.")
                .additionalNotes("Reached out to recruiter via LinkedIn before applying.")
                .currentJobStatus(JobStatus.APPLIED)
                .build();

        sampleDTO = modelMapper.map(sampleApplication, JobApplicationDTO.class);

        sampleResponse = Response.<JobApplicationDTO>builder()
                .statusCode(200)
                .message("Success")
                .data(sampleDTO)
                .build();

        when(jobApplicationService.createApplication(any(JobApplicationDTO.class)))
                .thenReturn(sampleResponse);
    }

    @WithMockUser(username = "testuser")
    @Test
    void createApplication_shouldReturnCreatedApplication() throws Exception {
        when(jobApplicationService.createApplication(any(JobApplicationDTO.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.name").value("Sample Name"));
    }

    @WithMockUser(username = "testuser")
    @Test
    void updateApplication_shouldReturnUpdatedApplication() throws Exception {
        when(jobApplicationService.updateApplication(eq(1L), any(JobApplicationDTO.class))).thenAnswer(invocation -> {
            JobApplicationDTO inputDto = invocation.getArgument(1);
            // Change the companyName to something else (simulate update)
            inputDto.setCompanyName("Updated Company Name");
            return Response.<JobApplicationDTO>builder()
                    .statusCode(200)
                    .message("Success")
                    .data(inputDto)
                    .build();
        });

        mockMvc.perform(put("/api/applications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.companyName").value("Updated Company Name"));
    }

    @WithMockUser(username = "testuser")
    @Test
    void getApplicationById_shouldReturnApplication() throws Exception {
        when(jobApplicationService.getApplicationById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/applications/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Sample Name"));
    }

    @WithMockUser(username = "testuser")
    @Test
    void getApplications_shouldReturnPagedResults() throws Exception {
        Page<JobApplicationDTO> page = new PageImpl<>(List.of(sampleDTO));
        Response<Page<JobApplicationDTO>> pagedResponse = Response.<Page<JobApplicationDTO>>builder()
                .statusCode(200)
                .message("Success")
                .data(page)
                .build();

        when(jobApplicationService.getApplications(
                any(), any(), anyString(), anyInt(), anyInt()))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/applications")
                        .param("search", "sample")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Sample Name"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @WithMockUser(username = "testuser")
    @Test
    void deleteApplicationById_shouldReturnSuccess() throws Exception {
        Response<?> deleteResponse = Response.builder()
                .statusCode(200)
                .message("Deleted successfully")
                .build();

        when(jobApplicationService.deleteApplication(1L))
                .thenReturn((Response) deleteResponse);

        mockMvc.perform(delete("/api/applications/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deleted successfully"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}


