// com.contentflow.content.dto.ContentCreateRequest.java
package com.contentflow.content.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContentCreateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String contentMd;
    private String summary;
    private String coverImage;
    private String status; // DRAFT, PUBLISHED
}