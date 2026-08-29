package com.wkr.document.mq;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wkr.document.entity.DocumentContent;
import com.wkr.document.entity.Document;
import com.wkr.document.enums.DocumentStatus;
import com.wkr.document.mapper.DocumentContentMapper;
import com.wkr.document.mapper.DocumentMapper;
import com.wkr.mq.document.DocumentMqConstants;
import com.wkr.mq.document.DocumentProcessResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentResultConsumer {

    private final DocumentMapper documentMapper;
    private final DocumentContentMapper documentContentMapper;

    @RabbitListener(queues = DocumentMqConstants.RESULT_QUEUE)
    @Transactional
    public void onResult(DocumentProcessResultMessage msg) {

        log.info("Received process result, documentId={}, status={}, taskId={}",
                msg.getDocumentId(), msg.getStatus(), msg.getTaskId());

        Document doc = documentMapper.selectById(msg.getDocumentId());
        if (doc == null) {
            log.warn("Document not found, ignore. id={}", msg.getDocumentId());
            return; // 直接 ack，消息无意义
        }

        LocalDateTime now = LocalDateTime.now();

        if (msg.getStatus()
                == DocumentProcessResultMessage.ProcessStatus.SUCCESS) {
            // 幂等保护：已 READY 就不重复写内容
            if ((!Objects.equals(
                    DocumentStatus.PROCESSING.getCode(),
                    doc.getStatus()))
            ) {
                log.warn("Document {} not in PROCESSING ({}), skip",
                        doc.getId(), doc.getStatus());
                return;
            }
            // 写正文（upsert）
            DocumentContent existing = documentContentMapper.selectOne(
                    Wrappers.<DocumentContent>lambdaQuery()
                            .eq(DocumentContent::getDocumentId, doc.getId())
            );
            if (existing == null) {
                DocumentContent content = new DocumentContent();
                content.setDocumentId(doc.getId());
                content.setContent(msg.getContentReference()); // 见下方说明
                documentContentMapper.insert(content);
            } else {
                existing.setContent(msg.getContentReference());
                documentContentMapper.updateById(existing);
            }

            doc.setStatus(DocumentStatus.READY.getCode());
            doc.setErrorMessage(null);
        } else {
            doc.setStatus(DocumentStatus.FAILED.getCode());
            doc.setErrorMessage(truncate(msg.getErrorMessage()));
        }

        doc.setUpdateTime(now);
        documentMapper.updateById(doc);
    }

    private String truncate(String s) {
        if (s == null) return "文档解析失败";
        return s.length() > 500 ? s.substring(0, 500) : s;
    }
}
