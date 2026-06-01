package com.contentflow.publish.controller;

import com.contentflow.common.response.ApiResponse;
import com.contentflow.publish.dto.PublishRequest;
import com.contentflow.publish.entity.PublishTask;
import com.contentflow.publish.service.PublishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publish")
@RequiredArgsConstructor
public class PublishController {

    private final PublishService publishService;

    @PostMapping("/task")
    public ApiResponse<String> publish(@Valid @RequestBody PublishRequest request) {
        String taskId = publishService.publish(request);
        return ApiResponse.success(taskId);
    }

    @GetMapping("/task/{taskId}")
    public ApiResponse<PublishTask> getTaskStatus(@PathVariable String taskId) {
        PublishTask task = publishService.getTaskById(taskId);
        return ApiResponse.success(task);
    }

    @GetMapping("/history")
    public ApiResponse<?> getHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(publishService.getPublishHistory(page, size));
    }
}