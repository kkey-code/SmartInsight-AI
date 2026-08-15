package com.wkr.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wkr.core.exception.BusinessException;
import com.wkr.document.context.UserContext;
import com.wkr.document.dto.DocumentCreateDTO;
import com.wkr.document.dto.DocumentPageDTO;
import com.wkr.document.dto.DocumentStatusUpdateDTO;
import com.wkr.document.dto.DocumentUpdateDTO;
import com.wkr.document.entity.DocumentInfo;
import com.wkr.document.enums.DocumentStatus;
import com.wkr.document.mapper.DocumentMapper;
import com.wkr.document.service.DocumentService;
import com.wkr.document.vo.DocumentPageVO;
import com.wkr.document.vo.DocumentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentMapper documentMapper;

    public DocumentServiceImpl(DocumentMapper documentMapper) {
        this.documentMapper = documentMapper;
    }

    @Override
    @Transactional
    public Long create(DocumentCreateDTO dto) {
        Long userId = requireUserId();

        DocumentInfo document = new DocumentInfo();
        document.setOwnerId(userId);
        document.setTitle(dto.getTitle());
        document.setDescription(dto.getDescription());
        document.setStatus(DocumentStatus.DRAFT.name());
        document.setDeleted(0);

        documentMapper.insert(document);
        return document.getId();
    }

    @Override
    public DocumentVO get(Long id) {
        DocumentInfo document = getEntity(id);
        checkAccess(document);
        return toVO(document);
    }

    @Override
    public DocumentPageVO page(DocumentPageDTO dto) {
        long pageNo = Math.max(dto.getPageNo(), 1);
        long pageSize = Math.min(Math.max(dto.getPageSize(), 1), 100);

        LambdaQueryWrapper<DocumentInfo> wrapper = new LambdaQueryWrapper<DocumentInfo>()
                .like(dto.getTitle() != null && !dto.getTitle().isBlank(), DocumentInfo::getTitle, dto.getTitle())
                .orderByDesc(DocumentInfo::getCreateTime);

        if (!UserContext.isAdmin()) {
            wrapper.eq(DocumentInfo::getOwnerId, requireUserId());
        }

        Page<DocumentInfo> page = documentMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);

        List<DocumentVO> records = page.getRecords().stream().map(this::toVO).toList();
        DocumentPageVO result = new DocumentPageVO();
        result.setPageNo(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotal(page.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    @Transactional
    public void update(DocumentUpdateDTO dto) {
        DocumentInfo document = getEntity(dto.getId());
        checkAccess(document);

        if (DocumentStatus.ARCHIVED.name().equals(document.getStatus())) {
            throw new BusinessException(409, "已归档文档不能修改");
        }

        if (dto.getTitle() != null) {
            document.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            document.setDescription(dto.getDescription());
        }
        documentMapper.updateById(document);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        DocumentInfo document = getEntity(id);
        checkAccess(document);
        documentMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, DocumentStatusUpdateDTO dto) {
        DocumentInfo document = getEntity(id);
        checkAccess(document);

        DocumentStatus current = DocumentStatus.valueOf(document.getStatus());
        DocumentStatus target = DocumentStatus.valueOf(dto.getStatus());
        if (!current.canTransitionTo(target)) {
            throw new BusinessException(409, "非法状态转换: " + current + " -> " + target);
        }

        document.setStatus(target.name());
        documentMapper.updateById(document);
    }

    private DocumentInfo getEntity(Long id) {
        DocumentInfo document = documentMapper.selectById(id);
        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }
        return document;
    }

    private void checkAccess(DocumentInfo document) {
        if (!UserContext.isAdmin() && !document.getOwnerId().equals(requireUserId())) {
            throw new BusinessException(403, "无权访问该文档");
        }
    }

    private Long requireUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        return userId;
    }

    private DocumentVO toVO(DocumentInfo entity) {
        DocumentVO vo = new DocumentVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
