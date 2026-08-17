package com.wkr.worker.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.wkr.mq.document.DocumentMqConstants;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue documentProcessQueue() {
        return new Queue(
                DocumentMqConstants.PROCESS_QUEUE,
                true
        );
    }

    /**
     * RabbitMQ JSON 消息转换器
     */
    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Listener 使用 JSON Converter
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
    @Bean
    public DirectExchange documentResultExchange() {

        return new DirectExchange(
                DocumentMqConstants.RESULT_EXCHANGE,
                true,
                false
        );
    }


}