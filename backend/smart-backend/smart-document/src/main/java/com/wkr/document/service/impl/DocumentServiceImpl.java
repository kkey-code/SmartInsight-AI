package com.wkr.document.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wkr.core.exception.BusinessException;
import com.wkr.document.dto.DocumentCreateDTO;
import com.wkr.document.dto.DocumentPageDTO;
import com.wkr.document.dto.DocumentUpdateDTO;
import com.wkr.document.entity.Document;
import com.wkr.document.entity.DocumentContent;
import com.wkr.document.enums.DocumentStatus;
import com.wkr.document.mapper.DocumentContentMapper;
import com.wkr.document.mapper.DocumentMapper;
import com.wkr.document.mq.DocumentProcessProducer;
import com.wkr.document.service.DocumentService;
import com.wkr.document.service.FileStorage;
import com.wkr.document.vo.DocumentContentVO;
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
    private final DocumentContentMapper documentContentMapper;
    private final DocumentProcessProducer documentProcessProducer;

    public DocumentServiceImpl(
            FileStorage fileStorage,
            DocumentContentMapper documentContentMapper,
            DocumentProcessProducer documentProcessProducer
    ) {
        this.fileStorage = fileStorage;
        this.documentContentMapper = documentContentMapper;
        this.documentProcessProducer = documentProcessProducer;
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
                new Page<>(dto.getCurrent(), dto.getSize());

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
        Long userId = requireUserId();

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException(400, "文件名不能为空");
        }

        // ==============================
        // 2. 清理原始文件名
        // ==============================
        String fileName =
                originalFilename.replace("\\", "/");

        int lastSlash = fileName.lastIndexOf('/');

        if (lastSlash >= 0) {
            fileName =
                    fileName.substring(lastSlash + 1);
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

        /*
         * 注意：
         * 文件刚上传完成时，
         * 不能直接 READY。
         * Worker 还没有解析。
         */
        document.setStatus(DocumentStatus.PROCESSING.getCode());
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateTime(LocalDateTime.now());

        save(document);

        // ==============================
        // 4. 生成存储路径
        // ==============================
        String extension = "";

        int lastIndex = fileName.lastIndexOf('.');

        if (lastIndex > 0 && lastIndex < fileName.length() - 1) {

            extension = fileName.substring(lastIndex);
        }

        String storedFileName = UUID.randomUUID() + extension;

        String relativePath = userId
                        + "/"
                        + document.getId()
                        + "/"
                        + storedFileName;
        // ==============================
        // 5. 上传文件
        // ==============================
        String storageKey = null;

        try {
            storageKey = fileStorage.upload(file, relativePath);

            document.setStorageKey(storageKey);
            document.setUpdateTime(LocalDateTime.now());
            updateById(document);

        } catch (Exception e) {
            log.error(
                    "文件上传失败, documentId={}",
                    document.getId(),
                    e
            );

            // 清理已经上传的文件
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

            // 更新失败状态
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
        // ==============================
        // 6. 文件上传成功
        // ==============================
        /*
         * 到这里：
         * DB = PROCESSING
         * 文件 = 已经存在
         * 接下来把处理任务交给 Worker。
         */
        try {
            String taskId =
                    documentProcessProducer.send(
                            document.getId(),
                            document.getOwnerId(),
                            document.getStorageKey()
                    );
            document.setTaskId(taskId);
            document.setUpdateTime(LocalDateTime.now());
            updateById(document);

            log.info(
                    "文档处理任务已发送, documentId={}, taskId={}",
                    document.getId(),
                    taskId
            );
        } catch (Exception e) {
            /*
             * MQ 发送失败不能假装成功。
             * 当前先把文档标记 FAILED。
             * 后面真正做生产级可靠消息时，
             * 再升级成 Outbox。
             */
            log.error(
                    "文档处理任务发送失败, documentId={}",
                    document.getId(),
                    e
            );
            document.setStatus(DocumentStatus.FAILED.getCode());
            document.setErrorMessage("文档处理任务发送失败");
            document.setUpdateTime(LocalDateTime.now());
            updateById(document);

            throw new BusinessException(500, "文档处理任务发送失败");
        }
        return toVO(document);
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
                || document.getStatus()
                != DocumentStatus.READY.getCode()) {

            throw new BusinessException(400, "文档当前不可下载");
        }

        Resource resource =
                fileStorage.load(
                        document.getStorageKey()
                );

        return new DocumentDownloadVO(
                resource,
                document.getFileName(),
                document.getFileType()
        );
    }

    @Override
    public DocumentContentVO getContent(Long documentId) {

        Document document = getById(documentId);

        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }
        checkPermission(document);

        Integer status = document.getStatus();
        if (status == null) {
            throw new BusinessException(409, "文档状态异常");
        }
        if (status == 1) {
            throw new BusinessException(409, "文档正在解析");
        }

        if (status == 3) {
            String message = document.getErrorMessage();

            throw new BusinessException(
                    409,
                    message == null || message.isBlank()
                            ? "文档解析失败" : "文档解析失败：" + message
            );
        }
        DocumentContent content =
                documentContentMapper.selectOne(
                        Wrappers.<DocumentContent>lambdaQuery()
                                .eq(DocumentContent::getDocumentId,
                                        documentId
                                )
                );
        if (content == null) {
            throw new BusinessException(404, "文档内容不存在");
        }
        DocumentContentVO vo = new DocumentContentVO();
        BeanUtils.copyProperties(content, vo);

        return vo;
    }

    /**
     * 检查当前用户是否有权限操作指定文档
     *
     * <p>权限规则：</p>
     * <ul>
     *     <li>管理员（Admin）可以操作任何文档</li>
     *     <li>普通用户只能操作自己创建的文档（ownerId = 当前用户ID）</li>
     * </ul>
     *
     * @param document 待检查的文档对象，不能为 null
     * @throws BusinessException 如果未登录抛出 401 异常（由 requireUserId 抛出）
     * @throws BusinessException 如果无权限抛出 403 异常（无权操作该文档）
     */
    private void checkPermission(Document document) {

        Long userId = requireUserId();
        if (!UserContext.isAdmin() && !userId.equals(document.getOwnerId()
        )) {
            throw new BusinessException(403, "无权操作该文档");
        }
    }

    /**
     * 获取当前登录用户的ID，若未登录则抛出异常
     *
     * <p>该方法在需要强制登录校验的场景下使用，如：</p>
     * <ul>
     *     <li>上传文档</li>
     *     <li>删除文档</li>
     *     <li>文档权限校验</li>
     * </ul>
     *
     * @return 当前登录用户的ID，保证不为 null
     * @throws BusinessException 如果当前用户未登录，抛出 401 未授权异常
     */
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