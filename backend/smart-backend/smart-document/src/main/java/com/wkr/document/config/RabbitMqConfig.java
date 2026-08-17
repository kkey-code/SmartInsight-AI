package com.wkr.document.config;

import com.wkr.mq.document.DocumentMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public DirectExchange processExchange() {
        return new DirectExchange(DocumentMqConstants.PROCESS_EXCHANGE, true, false);
    }

    @Bean
    public Queue processQueue() {
        return new Queue(DocumentMqConstants.PROCESS_QUEUE, true);
    }

    @Bean
    public Binding processBinding(Queue processQueue, DirectExchange processExchange) {
        return BindingBuilder.bind(processQueue)
                .to(processExchange)
                .with(DocumentMqConstants.PROCESS_ROUTING_KEY);
    }

    @Bean
    public DirectExchange resultExchange() {
        return new DirectExchange(DocumentMqConstants.RESULT_EXCHANGE, true, false);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(DocumentMqConstants.RESULT_QUEUE, true);
    }

    @Bean
    public Binding resultBinding(Queue resultQueue, DirectExchange resultExchange) {
        return BindingBuilder.bind(resultQueue)
                .to(resultExchange)
                .with(DocumentMqConstants.RESULT_ROUTING_KEY);
    }

    @Bean
    public DirectExchange documentResultExchange(){

        return new DirectExchange(
                DocumentMqConstants.RESULT_EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue documentResultQueue(){

        return new Queue(
                DocumentMqConstants.RESULT_QUEUE,
                true
        );
    }

    @Bean
    public Binding documentResultBinding(
            Queue documentResultQueue,
            DirectExchange documentResultExchange
    ){
        return BindingBuilder
                .bind(documentResultQueue)
                .to(documentResultExchange)
                .with(
                        DocumentMqConstants.RESULT_ROUTING_KEY
                );

    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}