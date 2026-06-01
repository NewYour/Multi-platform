package com.contentflow.publish.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishResult {
    private boolean success;
    private String externalId;
    private String errorMessage;

    public static PublishResult success(String externalId) {
        return new PublishResult(true, externalId, null);
    }

    public static PublishResult failure(String errorMessage) {
        return new PublishResult(false, null, errorMessage);
    }
}