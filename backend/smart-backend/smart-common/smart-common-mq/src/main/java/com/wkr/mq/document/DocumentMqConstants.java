package com.wkr.mq.document;

public final class DocumentMqConstants {

    private DocumentMqConstants() {
    }

    public static final String PROCESS_EXCHANGE =
            "smart.document.exchange";

    public static final String PROCESS_QUEUE =
            "smart.document.process.queue";

    public static final String PROCESS_ROUTING_KEY =
            "document.process";

    public static final String RESULT_EXCHANGE =
            "smart.document.result.exchange";

    public static final String RESULT_QUEUE =
            "smart.document.result.queue";

    public static final String RESULT_ROUTING_KEY =
            "document.process.result";
}