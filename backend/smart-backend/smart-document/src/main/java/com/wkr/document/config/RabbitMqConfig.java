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

    // ========== 处理流程 ==========
    @Bean
    public DirectExchange processExchange() {
        return createDirectExchange(DocumentMqConstants.PROCESS_EXCHANGE);
    }

    @Bean
    public Queue processQueue() {
        return createQueue(DocumentMqConstants.PROCESS_QUEUE);
    }

    @Bean
    public Binding processBinding(Queue processQueue, DirectExchange processExchange)
    {
        return createBinding(
                    processQueue,
                    processExchange,
                    DocumentMqConstants.PROCESS_ROUTING_KEY
        );
    }

    // ========== 结果流程 ==========
    @Bean
    public DirectExchange resultExchange() {
        return createDirectExchange(DocumentMqConstants.RESULT_EXCHANGE);
    }

    @Bean
    public Queue resultQueue() {
        return createQueue(DocumentMqConstants.RESULT_QUEUE);
    }

    @Bean
    public Binding resultBinding(Queue resultQueue, DirectExchange resultExchange) {
        return createBinding(
                    resultQueue,
                    resultExchange,
                    DocumentMqConstants.RESULT_ROUTING_KEY
        );
    }

    //把消息体转换成 JSON 格式
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ========== 抽取的公共方法 ==========
    private DirectExchange createDirectExchange(String name)
    {
        return new DirectExchange(name, true, false);
    }

    private Queue createQueue(String name)
    {
        return new Queue(name, true);
    }

    private Binding createBinding(Queue queue, DirectExchange exchange, String routingKey)
    {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey);
    }
}