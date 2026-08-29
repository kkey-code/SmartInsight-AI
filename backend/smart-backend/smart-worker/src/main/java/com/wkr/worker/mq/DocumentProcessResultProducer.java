package com.wkr.worker.mq;


import com.wkr.mq.document.DocumentMqConstants;
import com.wkr.mq.document.DocumentProcessResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DocumentProcessResultProducer {


    private final RabbitTemplate rabbitTemplate;


    public void success(Long documentId, String taskId)
    {
        send(
                new DocumentProcessResultMessage(
                        documentId,
                        taskId,
                        DocumentProcessResultMessage.ProcessStatus.SUCCESS,
                        null,
                        null
                )
        );
    }

    public void failed(
            Long documentId,
            String taskId,
            String errorMessage
    ){
        send(
                new DocumentProcessResultMessage(
                        documentId,
                        taskId,
                        DocumentProcessResultMessage.ProcessStatus.FAILED,
                        errorMessage,
                        null
                )
        );
    }

    private void send(
            DocumentProcessResultMessage message
    ){
        rabbitTemplate.convertAndSend(
                DocumentMqConstants.RESULT_EXCHANGE,
                DocumentMqConstants.RESULT_ROUTING_KEY,
                message
        );
    }
}