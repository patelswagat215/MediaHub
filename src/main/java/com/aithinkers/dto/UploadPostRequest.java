package com.aithinkers.dto;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UploadPostRequest {
    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "Caption is required")
    private String caption;

    @NotBlank(message = "Media type is required")
    @Pattern(regexp = "^(IMAGE|VIDEO|AUDIO)$", message = "Media type must be IMAGE, VIDEO, or AUDIO")
    private String mediaType;

    @NotNull(message = "File is required")
    private MultipartFile file;
} 