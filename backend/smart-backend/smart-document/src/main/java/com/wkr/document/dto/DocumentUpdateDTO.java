package com.wkr.document.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DocumentUpdateDTO {

    @NotNull(message = "文档ID不能为空")
    private Long id;

    @Size(max = 255, message = "文档标题不能超过255个字符")
    private String title;

    @Size(max = 1000, message = "文档描述不能超过1000个字符")
    private String description;
}