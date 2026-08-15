package com.wkr.document.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DocumentStatusUpdateDTO {
    @NotNull(message = "目标状态不能为空")
    @Pattern(regexp = "DRAFT|PROCESSING|READY|FAILED|ARCHIVED", message = "文档状态不合法")
    private String status;
}
