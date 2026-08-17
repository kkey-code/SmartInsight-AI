package com.wkr.document.mq;

import com.wkr.mq.document.DocumentMqConstants;
import com.wkr.mq.document.DocumentProcessMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentProcessProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送文档处理任务
     */
    public String send(
            Long documentId,
            Long ownerId,
            String storageKey
    ) {

        String taskId = UUID.randomUUID().toString();

        DocumentProcessMessage message =
                new DocumentProcessMessage(
                        documentId,
                        ownerId,
                        storageKey,
                        taskId
                );

        rabbitTemplate.convertAndSend(
                DocumentMqConstants.PROCESS_EXCHANGE,
                DocumentMqConstants.PROCESS_ROUTING_KEY,
                message
        );

        log.info(
                "Document process message sent, taskId={}, documentId={}, ownerId={}, storageKey={}",
                taskId,
                documentId,
                ownerId,
                storageKey
        );

        return taskId;
    }
}