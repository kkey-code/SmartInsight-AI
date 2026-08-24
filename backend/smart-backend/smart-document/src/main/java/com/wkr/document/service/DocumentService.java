package com.wkr.document.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wkr.document.dto.DocumentCreateDTO;
import com.wkr.document.dto.DocumentPageDTO;
import com.wkr.document.dto.DocumentUpdateDTO;
import com.wkr.document.entity.Document;
import com.wkr.document.vo.DocumentContentVO;
import com.wkr.document.vo.DocumentDownloadVO;
import com.wkr.document.vo.DocumentVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService extends IService<Document> {

    DocumentVO create(DocumentCreateDTO dto);

    DocumentVO getDetail(Long id);

    Page<DocumentVO> page(DocumentPageDTO dto);

    DocumentVO update(DocumentUpdateDTO dto);

    void delete(Long id);

    DocumentVO upload(MultipartFile file);

    DocumentDownloadVO download(Long id);

    DocumentContentVO getContent(Long documentId);
}