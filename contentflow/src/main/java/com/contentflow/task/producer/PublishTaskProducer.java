// com.contentflow.task.producer.PublishTaskProducer.java
package com.contentflow.task.producer;

import com.contentflow.common.config.RabbitMQConfig;
import com.contentflow.publish.dto.PublishTaskMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PublishTaskProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendPublishTask(PublishTaskMessage message) {
        log.info("发送发布任务到MQ: taskId={}", message.getTaskId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.PUBLISH_EXCHANGE,
                RabbitMQConfig.PUBLISH_ROUTING_KEY, message);
    }
}