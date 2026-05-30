// com.contentflow.publish.service.PublishService.java
package com.contentflow.publish.service;

import cn.hutool.core.util.IdUtil;
import com.contentflow.common.exception.BusinessException;
import com.contentflow.content.entity.Content;
import com.contentflow.content.mapper.ContentMapper;
import com.contentflow.publish.adapter.PlatformAdapter;
import com.contentflow.publish.dto.PublishRequest;
import com.contentflow.publish.dto.PublishResult;
import com.contentflow.publish.dto.PublishTaskMessage;
import com.contentflow.publish.entity.PublishTask;
import com.contentflow.publish.mapper.PublishTaskMapper;
import com.contentflow.publish.engine.AdaptEngine;
import com.contentflow.task.producer.PublishTaskProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishService {

    private final PublishTaskMapper publishTaskMapper;
    private final ContentMapper contentMapper;
    private final PublishTaskProducer taskProducer;
    private final AdaptEngine adaptEngine;
    private final Map<String, PlatformAdapter> adapterMap = new ConcurrentHashMap<>();

    public PublishService(List<PlatformAdapter> adapters) {
        for (PlatformAdapter adapter : adapters) {
            adapterMap.put(adapter.getPlatform(), adapter);
        }
    }

    public String publish(PublishRequest request) {
        // 验证内容
        Content content = contentMapper.selectById(request.getContentId());
        if (content == null) {
            throw new BusinessException("内容不存在");
        }
        // 创建发布任务
        String taskId = IdUtil.fastSimpleUUID();
        PublishTask task = new PublishTask();
        task.setTaskId(taskId);
        task.setUserId(content.getUserId());
        task.setContentId(request.getContentId());
        task.setPlatforms(request.getPlatforms());
        task.setSimulate(request.isSimulate() ? 1 : 0);
        task.setStatus("PENDING");
        publishTaskMapper.insert(task);

        // 发送MQ消息
        PublishTaskMessage message = new PublishTaskMessage(
                taskId, content.getUserId(), request.getContentId(),
                request.getPlatforms(), request.isSimulate()
        );
        taskProducer.sendPublishTask(message);

        return taskId;
    }
}
