package com.wkr.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Service
public class DocumentProcessService {

    private final Path storagePath;

    private final DocumentContentService documentContentService;


    public DocumentProcessService(
            @Value("${smart.file.storage.path:./storage}") String storagePath,
            DocumentContentService documentContentService
    ) {
        this.storagePath = Path.of(storagePath)
                .toAbsolutePath()
                .normalize();
        this.documentContentService = documentContentService;

        log.info(
                "Worker file storage initialized, path={}",
                this.storagePath
        );
    }

    /**
     * 处理文档
     */
    public String process(Long documentId, Long ownerId, String storageKey)
    {
        log.info("Start processing document, documentId={}, ownerId={}, storageKey={}",
                documentId, ownerId, storageKey
        );

        if (storageKey == null || storageKey.isBlank()) {
            throw new IllegalArgumentException("storageKey不能为空");
        }
        Path filePath = storagePath
                .resolve(storageKey)
                .normalize();

        if (!filePath.startsWith(storagePath)) {

            throw new IllegalArgumentException(
                    "非法storageKey: " + storageKey
            );
        }
        File file = filePath.toFile();

        if (!file.exists()) {
            throw new IllegalStateException("文件不存在: " + filePath);
        }
        if (!file.isFile()) {
            throw new IllegalStateException("目标不是文件: " + filePath);
        }
        log.info(
                "Document file found, documentId={}, path={}, size={}",
                documentId,
                filePath,
                file.length()
        );
        return extractPdfText(documentId, file);
    }

    /**
     * 提取 PDF 文字并保存
     */
    private String extractPdfText(Long documentId, File file) {
        try (PDDocument document = Loader.loadPDF(file)) {
            // 1. 提取文字
            PDFTextStripper stripper = new PDFTextStripper();
            String extractedText = stripper.getText(document);
            String safeText = StringUtils.defaultString(extractedText);

            // 2. 保存到数据库
            documentContentService.save(documentId, safeText);

            // 3. 记录日志
            log.info("PDF processed successfully, documentId={}, pages={}, textLength={}",
                    documentId, document.getNumberOfPages(), safeText.length());

            // 4. 打印预览（前100字符）
            logPreview(documentId, safeText);

            return safeText;

        } catch (IOException e) {
            log.error("PDF processing failed, documentId={}, file={}",
                    documentId, file.getAbsolutePath(), e);
            throw new IllegalStateException("PDF解析失败: " + documentId, e);
        }
    }

    /**
     * 打印 PDF 文字预览（前100字符）
     */
    private void logPreview(Long documentId, String text) {
        if (StringUtils.isBlank(text)) {
            return;
        }

        // 压缩空白字符，取前100字符
        String preview = text.replaceAll("\\s+", " ").trim();
        preview = StringUtils.abbreviate(preview, 100);

        log.info("PDF text preview, documentId={}, text={}",
                documentId, preview
        );
    }
}