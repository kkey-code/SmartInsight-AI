package com.wkr.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("document_info")
public class Document {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ownerId;

    private String title;

    private String description;

    private String fileName;

    private Long fileSize;

    private String fileType;

    private String storageKey;

    private Integer status;

    private String taskId;

    private String errorMessage;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}