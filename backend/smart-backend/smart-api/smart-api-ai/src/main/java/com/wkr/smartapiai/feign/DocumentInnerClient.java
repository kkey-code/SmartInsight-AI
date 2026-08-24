package com.wkr.smartapiai.feign;

import com.wkr.core.result.Result;
import com.wkr.smartapiai.dto.DocumentContentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "smart-document-service", path = "/inner/document")
public interface DocumentInnerClient {

    @GetMapping("/{documentId}/content")
    Result<DocumentContentDTO> getDocumentContent(
            @PathVariable("documentId") Long documentId
    );
}