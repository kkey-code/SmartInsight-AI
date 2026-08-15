package com.wkr.document.controller;

import com.wkr.core.result.Result;
import com.wkr.document.dto.DocumentCreateDTO;
import com.wkr.document.dto.DocumentPageDTO;
import com.wkr.document.dto.DocumentStatusUpdateDTO;
import com.wkr.document.dto.DocumentUpdateDTO;
import com.wkr.document.service.DocumentService;
import com.wkr.document.vo.DocumentPageVO;
import com.wkr.document.vo.DocumentVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public Result<Long> create(@RequestBody @Valid DocumentCreateDTO dto) {
        return Result.success(documentService.create(dto));
    }

    @GetMapping("/{id}")
    public Result<DocumentVO> get(@PathVariable Long id) {
        return Result.success(documentService.get(id));
    }

    @GetMapping("/page")
    public Result<DocumentPageVO> page(@ModelAttribute DocumentPageDTO dto) {
        return Result.success(documentService.page(dto));
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Valid DocumentUpdateDTO dto) {
        documentService.update(dto);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return Result.success(null);
    }

    @PostMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestBody @Valid DocumentStatusUpdateDTO dto) {
        documentService.updateStatus(id, dto);
        return Result.success(null);
    }
}
