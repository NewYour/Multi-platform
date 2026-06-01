package com.contentflow.task.consumer;

import com.alibaba.fastjson2.JSON;
import com.contentflow.common.config.RabbitMQConfig;
import com.contentflow.content.entity.Content;
import com.contentflow.content.mapper.ContentMapper;
import com.contentflow.publish.adapter.PlatformAdapter;
import com.contentflow.publish.dto.PublishResult;
import com.contentflow.publish.dto.PublishTaskMessage;
import com.contentflow.publish.entity.PublishRecord;
import com.contentflow.publish.entity.PublishTask;
import com.contentflow.publish.engine.AdaptEngine;
import com.contentflow.publish.mapper.PublishRecordMapper;
import com.contentflow.publish.mapper.PublishTaskMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
@RequiredArgsConstructor  // 生成包含所有 final 字段的构造器
@Slf4j
public class PublishTaskConsumer {

    private final PublishTaskMapper publishTaskMapper;
    private final PublishRecordMapper publishRecordMapper;
    private final ContentMapper contentMapper;
    private final AdaptEngine adaptEngine;
    private final List<PlatformAdapter> adapters;  // Spring 自动注入所有 PlatformAdapter 实现

    private Map<String, PlatformAdapter> adapterMap;

    @PostConstruct
    public void init() {
        adapterMap = new HashMap<>();
        for (PlatformAdapter adapter : adapters) {
            adapterMap.put(adapter.getPlatform(), adapter);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PUBLISH_QUEUE)
    public void handlePublishTask(PublishTaskMessage message, Channel channel, Message amqpMessage) throws IOException {
        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();
        try {
            log.info("消费发布任务: taskId={}", message.getTaskId());
            PublishTask task = publishTaskMapper.selectByTaskId(message.getTaskId());
            if (task == null) {
                log.error("任务不存在: {}", message.getTaskId());
                channel.basicAck(deliveryTag, false);
                return;
            }
            task.setStatus("PROCESSING");
            publishTaskMapper.updateById(task);

            Content content = contentMapper.selectById(message.getContentId());
            if (content == null) {
                throw new RuntimeException("内容不存在");
            }

            Map<String, Object> resultMap = new HashMap<>();
            List<String> platforms = message.getPlatforms();
            boolean allSuccess = true;

            for (String platform : platforms) {
                PlatformAdapter adapter = adapterMap.get(platform);
                if (adapter == null) {
                    log.warn("不支持的平台: {}", platform);
                    continue;
                }
                String adaptedContent = adaptEngine.adaptForPlatform(content.getContentMd(), platform);
                // 这里可以调用 AI 重写，示例中省略
                PublishResult publishResult = adapter.publish(content, null, message.isSimulate());

                PublishRecord record = new PublishRecord();
                record.setTaskId(message.getTaskId());
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

            task.setStatus(allSuccess ? "SUCCESS" : "PARTIAL_SUCCESS");
            task.setResult(JSON.toJSONString(resultMap));
            publishTaskMapper.updateById(task);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理发布任务失败: {}", message.getTaskId(), e);
            boolean requeue = !amqpMessage.getMessageProperties().getRedelivered();
            channel.basicNack(deliveryTag, false, requeue);
        }
    }
}