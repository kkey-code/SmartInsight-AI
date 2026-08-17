package com.wkr.mq.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentProcessResultMessage {


    private Long documentId;

    private String taskId;

    private ProcessStatus status;

    private String errorMessage;

    private String contentReference;


    public enum ProcessStatus {

        SUCCESS,

        FAILED
    }

}