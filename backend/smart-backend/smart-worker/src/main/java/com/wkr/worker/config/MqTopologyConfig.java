package com.wkr.worker.config;

import com.wkr.mq.document.DocumentMqConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqTopologyConfig {

    @Bean
    public DirectExchange processExchange() {
        return new DirectExchange(DocumentMqConstants.PROCESS_EXCHANGE, true, false);
    }

    @Bean
    public Queue processQueue() {
        // durable + 手动 ack 由 yaml 里 listener 配置控制
        return QueueBuilder.durable(DocumentMqConstants.PROCESS_QUEUE).build();
    }

    @Bean
    public Binding processBinding() {
        return BindingBuilder.bind(processQueue())
                .to(processExchange())
                .with(DocumentMqConstants.PROCESS_ROUTING_KEY);
    }

    @Bean
    public DirectExchange resultExchange() {
        return new DirectExchange(DocumentMqConstants.RESULT_EXCHANGE, true, false);
    }

    @Bean
    public Queue resultQueue() {
        return QueueBuilder.durable(DocumentMqConstants.RESULT_QUEUE).build();
    }

    @Bean
    public Binding resultBinding() {
        return BindingBuilder.bind(resultQueue())
                .to(resultExchange())
                .with(DocumentMqConstants.RESULT_ROUTING_KEY);
    }
}
