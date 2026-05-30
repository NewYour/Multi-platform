// com.contentflow.publish.dto.PublishTaskMessage.java
package com.contentflow.publish.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishTaskMessage {
    private String taskId;
    private Long userId;
    private Long contentId;
    private List<String> platforms;
    private boolean simulate;
}