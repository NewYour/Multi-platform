package com.contentflow.publish.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.core.context.SecurityContextHolder;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.contentflow.common.exception.BusinessException;
import com.contentflow.content.entity.Content;
import com.contentflow.content.mapper.ContentMapper;
import com.contentflow.publish.adapter.PlatformAdapter;
import com.contentflow.publish.dto.PublishRequest;
import com.contentflow.publish.dto.PublishResult;
import com.contentflow.publish.entity.PublishRecord;
import com.contentflow.publish.entity.PublishTask;
import com.contentflow.publish.mapper.PublishRecordMapper;
import com.contentflow.publish.mapper.PublishTaskMapper;
import com.contentflow.publish.engine.AdaptEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishService {

    private final PublishTaskMapper publishTaskMapper;
    private final PublishRecordMapper publishRecordMapper;
    private final ContentMapper contentMapper;
    private final AdaptEngine adaptEngine;
    private final List<PlatformAdapter> adapters;

    private Map<String, PlatformAdapter> adapterMap;

    @PostConstruct
    public void init() {
        adapterMap = new HashMap<>();
        for (PlatformAdapter adapter : adapters) {
            adapterMap.put(adapter.getPlatform(), adapter);
        }
    }

    public String publish(PublishRequest request) {
        Content content = contentMapper.selectById(request.getContentId());
        if (content == null) {
            throw new BusinessException("内容不存在");
        }

        String taskId = IdUtil.fastSimpleUUID();
        PublishTask task = new PublishTask();
        task.setTaskId(taskId);
        task.setUserId(content.getUserId());
        task.setContentId(request.getContentId());
        task.setPlatforms(JSON.toJSONString(request.getPlatforms()));
        task.setSimulate(request.isSimulate() ? 1 : 0);
        task.setStatus("PROCESSING");
        publishTaskMapper.insert(task);

        // 同步执行发布（绕过 MQ）
        executePublish(taskId, content, request.getPlatforms(), request.isSimulate());

        return taskId;
    }

    private void executePublish(String taskId, Content content, List<String> platforms, boolean simulate) {
        Map<String, Object> resultMap = new HashMap<>();
        boolean allSuccess = true;

        for (String platform : platforms) {
            PlatformAdapter adapter = adapterMap.get(platform);
            if (adapter == null) {
                log.warn("不支持的平台: {}", platform);
                continue;
            }

            String adaptedContent = adaptEngine.adaptForPlatform(content.getContentMd(), platform);
            PublishResult publishResult = adapter.publish(content, null, simulate);

            PublishRecord record = new PublishRecord();
            record.setTaskId(taskId);
            record.setPlatform(platform);
            record.setStatus(publishResult.isSuccess() ? "SUCCESS" : "FAILED");
            record.setExternalId(publishResult.getExternalId());
            record.setErrorMsg(publishResult.getErrorMessage());
            publishRecordMapper.insert(record);

            resultMap.put(platform, publishResult);
            if (!publishResult.isSuccess()) {
                allSuccess = false;
            }
        }

        PublishTask task = publishTaskMapper.selectByTaskId(taskId);
        task.setStatus(allSuccess ? "SUCCESS" : "PARTIAL_SUCCESS");
        task.setResult(JSON.toJSONString(resultMap));
        publishTaskMapper.updateById(task);
    }

    // 在 PublishService 中添加
    public Page<PublishTask> getPublishHistory(int page, int size) {
        Long userId = getCurrentUserId();
        Page<PublishTask> pageParam = new Page<>(page, size);
        return publishTaskMapper.selectPage(pageParam,
                new LambdaQueryWrapper<PublishTask>()
                        .eq(PublishTask::getUserId, userId)
                        .orderByDesc(PublishTask::getCreatedAt));
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 实际应该从数据库查询用户ID，简化处理返回1
        // 建议实现一个 UserService 来根据用户名获取ID
        return 1L;
    }
    // 在 PublishService 类中添加
    public PublishTask getTaskById(String taskId) {
        PublishTask task = publishTaskMapper.selectByTaskId(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        return task;
    }


}