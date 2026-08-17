package com.wkr.worker.consumer;


import com.wkr.mq.document.DocumentMqConstants;
import com.wkr.mq.document.DocumentProcessMessage;
import com.wkr.worker.mq.DocumentProcessResultProducer;
import com.wkr.worker.service.DocumentProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class DocumentProcessConsumer {


    private final DocumentProcessService documentProcessService;

    private final DocumentProcessResultProducer resultProducer;



    public DocumentProcessConsumer(
            DocumentProcessService documentProcessService,
            DocumentProcessResultProducer resultProducer
    ){

        this.documentProcessService = documentProcessService;
        this.resultProducer = resultProducer;

    }



    @RabbitListener(
            queues = DocumentMqConstants.PROCESS_QUEUE
    )
    public void consume(
            DocumentProcessMessage message
    ){

        Long documentId =
                message.getDocumentId();

        String taskId =
                message.getTaskId();

        log.info(
                "Receive document process message, documentId={}, taskId={}",
                documentId,
                taskId
        );
        try {
            documentProcessService.process(
                    documentId,
                    message.getOwnerId(),
                    message.getStorageKey()
            );
            resultProducer.success(
                    documentId,
                    taskId
            );

            log.info(
                    "Document process success, documentId={}",
                    documentId
            );
        } catch(Exception e){

            log.error(
                    "Document process failed, documentId={}",
                    documentId,
                    e
            );
            resultProducer.failed(
                    documentId,
                    taskId,
                    e.getMessage()
            );

        }
    }
}