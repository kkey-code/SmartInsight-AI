package com.wkr.mq.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentProcessMessage {

    private Long documentId;

    private Long ownerId;

    private String storageKey;

    private String taskId;
}