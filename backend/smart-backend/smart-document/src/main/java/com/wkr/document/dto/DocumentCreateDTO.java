package com.wkr.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DocumentCreateDTO {

    @NotBlank(message = "文档标题不能为空")
    @Size(max = 255, message = "文档标题不能超过255个字符")
    private String title;

    @Size(max = 1000, message = "文档描述不能超过1000个字符")
    private String description;
}