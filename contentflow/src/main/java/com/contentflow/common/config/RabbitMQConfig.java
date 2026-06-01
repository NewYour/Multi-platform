// com.contentflow.common.config.RabbitMQConfig.java
package com.contentflow.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RabbitMQConfig {

    public static final String PUBLISH_QUEUE = "publish.queue";
    public static final String PUBLISH_EXCHANGE = "publish.exchange";
    public static final String PUBLISH_ROUTING_KEY = "publish.routingkey";
    public static final String DEAD_LETTER_QUEUE = "publish.deadletter.queue";
    public static final String DEAD_LETTER_EXCHANGE = "publish.deadletter.exchange";

    @Bean
    public Queue publishQueue() {
        return QueueBuilder.durable(PUBLISH_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PUBLISH_ROUTING_KEY)
                .withArgument("x-max-retries", 3)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public TopicExchange publishExchange() {
        return new TopicExchange(PUBLISH_EXCHANGE);
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Binding publishBinding() {
        return BindingBuilder.bind(publishQueue()).to(publishExchange()).with(PUBLISH_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(PUBLISH_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
