package com.wkr.document.enums;

import lombok.Getter;

@Getter
public enum DocumentStatus {

    DRAFT(0),
    PROCESSING(1),
    READY(2),
    FAILED(3);

    private final int code;

    DocumentStatus(int code) {
        this.code = code;
    }
}