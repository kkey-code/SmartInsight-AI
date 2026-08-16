package com.wkr.document.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
public class DocumentDownloadVO {

    private Resource resource;

    private String fileName;

    private String fileType;
}