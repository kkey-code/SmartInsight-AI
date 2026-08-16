package com.wkr.document.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkr.core.exception.BusinessException;
import com.wkr.document.dto.DocumentCreateDTO;
import com.wkr.document.dto.DocumentPageDTO;
import com.wkr.document.dto.DocumentUpdateDTO;
import com.wkr.document.entity.Document;
import com.wkr.document.enums.DocumentStatus;
import com.wkr.document.mapper.DocumentMapper;
import com.wkr.document.service.DocumentService;
import com.wkr.document.service.FileStorage;
import com.wkr.document.vo.DocumentDownloadVO;
import com.wkr.document.vo.DocumentVO;
import com.wkr.web.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class DocumentServiceImpl
        extends ServiceImpl<DocumentMapper, Document>
        implements DocumentService {

    private final FileStorage fileStorage;

    public DocumentServiceImpl(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    @Transactional
    public DocumentVO create(DocumentCreateDTO dto) {

        Long userId = requireUserId();

        Document document = new Document();
        document.setOwnerId(userId);
        document.setTitle(dto.getTitle());
        document.setDescription(dto.getDescription());
        document.setStatus(DocumentStatus.DRAFT.getCode());
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateTime(LocalDateTime.now());

        save(document);

        return toVO(document);
    }

    @Override
    public DocumentVO getDetail(Long id) {

        Document document = getById(id);

        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }

        checkPermission(document);

        return toVO(document);
    }

    @Override
    public Page<DocumentVO> page(DocumentPageDTO dto) {

        Long userId = requireUserId();

        Page<Document> page =
                new Page<>(
                        dto.getCurrent(),
                        dto.getSize()
                );

        if (UserContext.isAdmin()) {

            lambdaQuery()
                    .orderByDesc(Document::getCreateTime)
                    .page(page);

        } else {

            lambdaQuery()
                    .eq(Document::getOwnerId, userId)
                    .orderByDesc(Document::getCreateTime)
                    .page(page);
        }

        Page<DocumentVO> result =
                new Page<>(
                        page.getCurrent(),
                        page.getSize(),
                        page.getTotal()
                );

        result.setRecords(
                page.getRecords()
                        .stream()
                        .map(this::toVO)
                        .toList()
        );

        return result;
    }

    @Override
    @Transactional
    public DocumentVO update(DocumentUpdateDTO dto) {

        Document document = getById(dto.getId());

        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }

        checkPermission(document);

        if (dto.getTitle() != null) {
            document.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            document.setDescription(dto.getDescription());
        }

        document.setUpdateTime(LocalDateTime.now());
        updateById(document);

        return toVO(document);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Document document = getById(id);

        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }

        checkPermission(document);
        String storageKey = document.getStorageKey();

        removeById(id);

        if (storageKey != null && !storageKey.isBlank()) {
            fileStorage.delete(storageKey);
        }
    }

    @Override
    public DocumentVO upload(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }

        Long userId = UserContext.getUserId();

        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException(400, "文件名不能为空");
        }

        String fileName = originalFilename
                .replace("\\", "/");

        int lastSlash = fileName.lastIndexOf('/');

        if (lastSlash >= 0) {
            fileName = fileName.substring(lastSlash + 1);
        }

        if (fileName.isBlank() || ".".equals(fileName) || "..".equals(fileName)) {
            throw new BusinessException(400, "非法文件名");
        }

        Document document = new Document();

        document.setOwnerId(userId);
        document.setTitle(fileName);
        document.setFileName(fileName);
        document.setFileSize(file.getSize());
        document.setFileType(file.getContentType());
        document.setStatus(DocumentStatus.PROCESSING.getCode());
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateTime(LocalDateTime.now());

        save(document);

        String extension = "";

        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex);
        }

        String storedFileName =
                UUID.randomUUID() + extension;

        String relativePath =
                userId + "/" +
                        document.getId() + "/" +
                        storedFileName;

        String storageKey = null;
        try {

            storageKey =
                    fileStorage.upload(file, relativePath);

            document.setStorageKey(storageKey);
            document.setStatus(DocumentStatus.READY.getCode());
            document.setUpdateTime(LocalDateTime.now());

            updateById(document);

            return toVO(document);

        } catch (Exception e) {

            if (storageKey != null && !storageKey.isBlank()) {
                try {
                    fileStorage.delete(storageKey);
                } catch (Exception deleteException) {
                    log.error(
                            "上传失败后的文件清理失败, documentId={}, storageKey={}",
                            document.getId(),
                            storageKey,
                            deleteException
                    );
                }
            }

            document.setStatus(DocumentStatus.FAILED.getCode());
            document.setErrorMessage("文件上传失败");
            document.setUpdateTime(LocalDateTime.now());

            try {
                updateById(document);
            } catch (Exception updateException) {
                log.error(
                        "记录文档失败状态失败, documentId={}",
                        document.getId(),
                        updateException
                );
            }

            throw new BusinessException(500, "文件上传失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDownloadVO download(Long id) {

        Document document = getById(id);

        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }

        checkPermission(document);

        if (document.getStorageKey() == null
                || document.getStorageKey().isBlank()) {
            throw new BusinessException(404, "文件不存在");
        }

        if (document.getStatus() == null
                || document.getStatus() != DocumentStatus.READY.getCode()) {
            throw new BusinessException(400, "文档当前不可下载");
        }

        Resource resource =
                fileStorage.load(document.getStorageKey());

        return new DocumentDownloadVO(
                resource,
                document.getFileName(),
                document.getFileType()
        );
    }

    private void checkPermission(Document document) {

        Long userId = requireUserId();

        if (!UserContext.isAdmin()
                && !userId.equals(document.getOwnerId())) {

            throw new BusinessException(403, "无权操作该文档");
        }
    }

    private Long requireUserId() {

        Long userId = UserContext.getUserId();

        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }

        return userId;
    }

    private DocumentVO toVO(Document document) {

        DocumentVO vo = new DocumentVO();
        BeanUtils.copyProperties(document, vo);

        return vo;
    }
}