package com.wkr.document.enums;

import lombok.Getter;

@Getter
public enum DocumentStatus {

    /**
     * 草稿状态：文档已上传但尚未提交处理
     * 通常用于用户保存为草稿，等待后续编辑或提交
     */
    DRAFT(0),
    /**
     * 处理中：文档正在后台处理（如格式转换、OCR识别、内容解析等）
     * 该状态时文档不可下载，需要等待处理完成
     */
    PROCESSING(1),
    /**
     * 已就绪：文档处理完成，可供用户下载或查看
     * 该状态表示文档已完全可用
     */
    READY(2),
    /**
     * 处理失败：文档处理过程中发生异常（如文件损坏、格式不支持、服务超时等）
     * 用户需要重新上传或联系管理员
     */
    FAILED(3);

    private final int code;

    DocumentStatus(int code) {
        this.code = code;
    }
}