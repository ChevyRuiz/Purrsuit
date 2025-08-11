package com.project.purrsuit.status_record.services;

import com.project.purrsuit.exceptions.NotFoundException;
import com.project.purrsuit.job_application.entity.JobApplication;
import com.project.purrsuit.job_application.repositories.JobApplicationRepository;
import com.project.purrsuit.status_record.dtos.StatusRecordDTO;
import com.project.purrsuit.status_record.entity.StatusRecord;
import com.project.purrsuit.status_record.repositories.StatusRecordRepository;
import com.project.purrsuit.user.entity.User;
import com.project.purrsuit.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusRecordServiceImpl implements StatusRecordService{

    private final UserService userService;
    private final JobApplicationRepository jobApplicationRepository;
    private final StatusRecordRepository statusRecordRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<StatusRecordDTO> findAllByApplicationId(Long applicationId) {

        User currentUser = userService.getCurrentLoggedInUser();

        JobApplication jobApplication = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("application not found"));

        if (!Objects.equals(jobApplication.getUser().getId(), currentUser.getId()))
            throw new AccessDeniedException("This application doesnt belong to the current user");

        List<StatusRecord> statusHistory = statusRecordRepository.findByApplicationIdOrderByAssignedAtDesc(applicationId);

        return statusHistory.stream()
                .map(record -> modelMapper.map(record, StatusRecordDTO.class))
                .toList();
    }
}
