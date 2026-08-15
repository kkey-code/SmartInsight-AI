package com.wkr.document.dto;

import lombok.Data;

@Data
public class DocumentPageDTO {
    private long pageNo = 1;
    private long pageSize = 10;
    private String title;
}
