package com.wkr.worker.mq;

import com.wkr.mq.document.DocumentMqConstants;
import com.wkr.mq.document.DocumentProcessMessage;
import com.wkr.worker.service.DocumentProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DocumentProcessConsumer {

    private final DocumentProcessService processService;
    private final DocumentResultProducer resultProducer;

    public DocumentProcessConsumer(
            DocumentProcessService processService,
            DocumentResultProducer resultProducer
    ) {
        this.processService = processService;
        this.resultProducer = resultProducer;
    }

    @RabbitListener(queues = DocumentMqConstants.PROCESS_QUEUE)
    public void onProcess(DocumentProcessMessage message) {
        Long documentId = message.getDocumentId();
        log.info("Received process task, documentId={}, taskId={}",
                documentId, message.getTaskId());
        try {
            String extractedText  = processService.process(
                    documentId,
                    message.getOwnerId(),
                    message.getStorageKey()
            );
            resultProducer.sendSuccess(documentId,
                                message.getTaskId(), extractedText);
        } catch (Exception e) {
            log.error("Process failed, documentId={}", documentId, e);
            resultProducer.sendFailed(
                    documentId, message.getTaskId(), e.getMessage());
            // 抛出异常 → 消息不会被确认，会重新入队重试
//            throw new RuntimeException("Document process failed, will retry", e);
        }
    }
}
