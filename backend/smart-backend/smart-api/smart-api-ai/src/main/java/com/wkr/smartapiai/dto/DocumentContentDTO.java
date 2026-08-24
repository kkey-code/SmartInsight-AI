package com.wkr.smartapiai.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentContentDTO {
    private Long id;
    private Long documentId;
    private String content;
    private LocalDateTime createTime;
}