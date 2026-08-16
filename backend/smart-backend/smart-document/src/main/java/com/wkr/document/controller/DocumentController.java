package com.wkr.document.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wkr.core.result.Result;
import com.wkr.document.dto.DocumentCreateDTO;
import com.wkr.document.dto.DocumentPageDTO;
import com.wkr.document.dto.DocumentUpdateDTO;
import com.wkr.document.service.DocumentService;
import com.wkr.document.vo.DocumentDownloadVO;
import com.wkr.document.vo.DocumentVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public Result<DocumentVO> create(
            @Valid @RequestBody DocumentCreateDTO dto) {

        return Result.success(documentService.create(dto));
    }

    @GetMapping("/{id}")
    public Result<DocumentVO> getDetail(@PathVariable("id") Long id) {

        return Result.success(documentService.getDetail(id));
    }

    @GetMapping("/page")
    public Result<Page<DocumentVO>> page(@Valid DocumentPageDTO dto) {

        return Result.success(documentService.page(dto));
    }

    @PutMapping
    public Result<DocumentVO> update(
            @Valid @RequestBody DocumentUpdateDTO dto) {

        return Result.success(documentService.update(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id) {

        documentService.delete(id);

        return Result.success(null);
    }
    @PostMapping("/upload")
    public Result<DocumentVO> upload(
            @RequestParam("file") MultipartFile file
    ) {
        return Result.success(
                documentService.upload(file)
        );
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {

        DocumentDownloadVO download =
                documentService.download(id);

        String fileName =
                sanitizeFileName(download.getFileName());

        MediaType mediaType =
                resolveMediaType(download.getFileType());

        String encodedFileName =
                URLEncoder
                        .encode(fileName, StandardCharsets.UTF_8)
                        .replace("+", "%20");

        String contentDisposition =
                "attachment; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition
                )
                .body(download.getResource());
    }

    private String sanitizeFileName(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return "download";
        }

        String sanitized = fileName
                .replaceAll(
                        "[^a-zA-Z0-9._\\-\\u4e00-\\u9fff]",
                        "_"
                );

        return sanitized.isBlank()
                ? "download"
                : sanitized;
    }

    private MediaType resolveMediaType(String fileType) {

        if (fileType == null || fileType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(fileType);
        } catch (IllegalArgumentException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}