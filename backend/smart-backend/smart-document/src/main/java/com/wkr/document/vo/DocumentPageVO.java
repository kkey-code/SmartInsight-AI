package com.wkr.document.vo;

import lombok.Data;

import java.util.List;

@Data
public class DocumentPageVO {
    private long pageNo;
    private long pageSize;
    private long total;
    private List<DocumentVO> records;
}
