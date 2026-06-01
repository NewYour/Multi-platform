package com.contentflow.publish.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PublishRequest {
    @NotNull
    private Long contentId;
    @NotEmpty
    private List<String> platforms;
    private boolean simulate = false;   // 默认真实发布
}