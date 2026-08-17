package com.wkr.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("document_content")
public class DocumentContent {


    @TableId(type = IdType.AUTO)
    private Long id;


    private Long documentId;


    private String content;


    private LocalDateTime createTime;


    private LocalDateTime updateTime;

}