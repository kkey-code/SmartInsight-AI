package com.wkr.document.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentContentVO {

    private Long id;

    private Long documentId;

    private String content;

    private LocalDateTime createTime;
}