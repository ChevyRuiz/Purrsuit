package com.project.purrsuit.cover_letter.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoverLetterDTO {
    private Long id;

    private Long userId;

    private String name;

    private String documentUrl;

    private MultipartFile documentFile;
}
