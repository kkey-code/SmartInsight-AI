package com.wkr.document.mq.consumer;


import com.wkr.document.entity.Document;
import com.wkr.document.enums.DocumentStatus;
import com.wkr.document.mapper.DocumentMapper;
import com.wkr.mq.document.DocumentProcessResultMessage;
import com.wkr.mq.document.DocumentMqConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;



@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentProcessResultConsumer {

    private final DocumentMapper documentMapper;

    @RabbitListener(
            queues = DocumentMqConstants.RESULT_QUEUE
    )
    public void consume(
            DocumentProcessResultMessage message
    ){
        log.info(
                "Receive document process result, documentId={}, taskId={}, status={}",
                message.getDocumentId(),
                message.getTaskId(),
                message.getStatus()
        );

        Document document =
                documentMapper.selectById(
                        message.getDocumentId()
                );

        if(document == null){

            log.warn(
                    "Document not found, id={}",
                    message.getDocumentId()
            );

            return;
        }

        if(message.getStatus() ==
                DocumentProcessResultMessage.ProcessStatus.SUCCESS
        ){
            document.setStatus(
                    DocumentStatus.READY.getCode()
            );
            document.setErrorMessage(null);

        }else{
            document.setStatus(
                    DocumentStatus.FAILED.getCode()
            );

            document.setErrorMessage(
                    message.getErrorMessage()
            );
        }

        documentMapper.updateById(document);

        log.info(
                "Document status updated, documentId={}, status={}",
                document.getId(),
                document.getStatus()
        );
    }
}