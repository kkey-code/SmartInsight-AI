package com.wkr.worker.mq;

import com.wkr.mq.document.DocumentMqConstants;
import com.wkr.mq.document.DocumentProcessResultMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class DocumentResultProducer {

    private final RabbitTemplate rabbitTemplate;

    public DocumentResultProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSuccess(Long documentId, String taskId, String content) {
        rabbitTemplate.convertAndSend(
                DocumentMqConstants.RESULT_EXCHANGE,
                DocumentMqConstants.RESULT_ROUTING_KEY,
                new DocumentProcessResultMessage(
                        documentId, taskId,
                        DocumentProcessResultMessage.ProcessStatus.SUCCESS,
                        null,
                        content
                )
        );
    }

    public void sendFailed(Long documentId, String taskId, String error) {
        rabbitTemplate.convertAndSend(
                DocumentMqConstants.RESULT_EXCHANGE,
                DocumentMqConstants.RESULT_ROUTING_KEY,
                new DocumentProcessResultMessage(
                        documentId, taskId,
                        DocumentProcessResultMessage.ProcessStatus.FAILED,
                        error, null
                )
        );
    }
}
