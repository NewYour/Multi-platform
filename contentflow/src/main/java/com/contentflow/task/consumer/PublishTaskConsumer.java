// com.contentflow.task.consumer.PublishTaskConsumer.java
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class PublishTaskConsumer {

    private final PublishTaskMapper publishTaskMapper;
    private final PublishRecordMapper publishRecordMapper;
    private final ContentMapper contentMapper;
    private final AdaptEngine adaptEngine;
    private final Map<String, PlatformAdapter> adapterMap = new ConcurrentHashMap<>();

    public PublishTaskConsumer(List<PlatformAdapter> adapters) {
        for (PlatformAdapter adapter : adapters) {
            adapterMap.put(adapter.getPlatform(), adapter);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PUBLISH_QUEUE)
    public void handlePublishTask(PublishTaskMessage message, Channel channel, Message amqpMessage) throws IOException {
        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();
        try {
            log.info("消费发布任务: taskId={}", message.getTaskId());
            // 更新任务状态为处理中
            PublishTask task = publishTaskMapper.selectById(message.getTaskId());
            if (task == null) {
                log.error("任务不存在: {}", message.getTaskId());
                channel.basicAck(deliveryTag, false);
                return;
            }
            task.setStatus("PROCESSING");
            publishTaskMapper.updateById(task);

            // 获取内容
            Content content = contentMapper.selectById(message.getContentId());
            if (content == null) {
                throw new RuntimeException("内容不存在");
            }

            // 执行发布到各平台
            Map<String, Object> resultMap = new HashMap<>();
            List<String> platforms = message.getPlatforms();
            boolean allSuccess = true;
            for (String platform : platforms) {
                PlatformAdapter adapter = adapterMap.get(platform);
                if (adapter == null) {
                    log.warn("不支持的平台: {}", platform);
                    continue;
                }
                // 内容适配
                String adaptedContent = adaptEngine.adaptForPlatform(content.getContentMd(), platform);
                String rewritten = adaptEngine.rewriteWithAi(adaptedContent, "小红书风格");
                // 发布
                PublishResult publishResult = adapter.publish(content, null, message.isSimulate());
                // 保存发布记录
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

            // 更新任务最终状态
            task.setStatus(allSuccess ? "SUCCESS" : "PARTIAL_SUCCESS");
            task.setResult(JSON.toJSONString(resultMap));
            publishTaskMapper.updateById(task);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理发布任务失败: {}", message.getTaskId(), e);
            // 重试
            boolean requeue = amqpMessage.getMessageProperties().getRedelivered() == false;
            channel.basicNack(deliveryTag, false, requeue);
        }
    }
}