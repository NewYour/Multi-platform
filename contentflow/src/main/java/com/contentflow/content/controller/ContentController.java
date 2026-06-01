package com.contentflow.content.controller;

import com.contentflow.common.response.ApiResponse;
import com.contentflow.content.dto.ContentCreateRequest;
import com.contentflow.content.entity.Content;
import com.contentflow.content.service.ContentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PostMapping("/create")
    public ApiResponse<Content> createContent(@Valid @RequestBody ContentCreateRequest request) {
        Content content = contentService.createContent(request);
        return ApiResponse.success(content);
    }

    @GetMapping("/list")
    public ApiResponse<?> listContents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(contentService.listUserContents(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Content> getContent(@PathVariable Long id) {
        Content content = contentService.getContent(id);
        return ApiResponse.success(content);
    }

    @PutMapping("/{id}")
    public ApiResponse<Content> updateContent(@PathVariable Long id, 
                                               @Valid @RequestBody ContentCreateRequest request) {
        Content content = contentService.updateContent(id, request);
        return ApiResponse.success(content);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return ApiResponse.success(null);
    }
}