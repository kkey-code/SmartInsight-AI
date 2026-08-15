package com.wkr.document.service;

import com.wkr.document.dto.DocumentCreateDTO;
import com.wkr.document.dto.DocumentPageDTO;
import com.wkr.document.dto.DocumentStatusUpdateDTO;
import com.wkr.document.dto.DocumentUpdateDTO;
import com.wkr.document.vo.DocumentPageVO;
import com.wkr.document.vo.DocumentVO;

public interface DocumentService {
    Long create(DocumentCreateDTO dto);
    DocumentVO get(Long id);
    DocumentPageVO page(DocumentPageDTO dto);
    void update(DocumentUpdateDTO dto);
    void delete(Long id);
    void updateStatus(Long id, DocumentStatusUpdateDTO dto);
}
