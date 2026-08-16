package com.wkr.document.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentVO {

    private Long id;

    private Long ownerId;

    private String title;

    private String description;

    private String fileName;

    private Long fileSize;

    private String fileType;

    private String storageKey;

    private Integer status;

    private String errorMessage;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}