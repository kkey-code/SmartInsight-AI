package com.wkr.mq.document;

/**
 * 文档模块 RabbitMQ 消息队列常量配置
 * <p>
 * 定义了文档上传、处理、结果通知等场景所需的交换机、队列和路由键。
 * 所有 MQ 相关组件名称统一在此管理，便于维护和修改。
 * </p>
 *
 * @author wkr
 * @since 2026-08-27
 */
public final class DocumentMqConstants {

    private DocumentMqConstants() {}

    // ==================== 文档处理流程 ====================
    /**
     * 文档处理交换机（Direct Exchange）
     * <p>
     * 用于路由文档上传后的处理任务，如 OCR 识别、格式转换、内容审核等
     * </p>
     * <p>
     * 适用场景：用户上传文档后，将文档 ID 发送到该交换机，
     * 由绑定的处理队列消费，执行异步任务
     * </p>
     */
    public static final String PROCESS_EXCHANGE =
            "smart.document.exchange";

    /**
     * 文档处理队列
     * <p>
     * 监听该队列的消费者负责执行文档的异步处理任务
     * </p>
     * <p>
     * 队列特性：持久化（durable），确保 RabbitMQ 重启后消息不丢失
     * </p>
     */
    public static final String PROCESS_QUEUE =
            "smart.document.process.queue";

    /**
     * 文档处理路由键
     * <p>
     * 用于将消息从交换机路由到处理队列
     * </p>
     */
    public static final String PROCESS_ROUTING_KEY =
            "document.process";

    // ==================== 文档处理结果通知 ====================

    /**
     * 文档结果通知交换机（Direct Exchange）
     * <p>
     * 用于路由文档处理完成后的结果通知，
     * 如处理成功/失败状态、处理结果摘要等
     * </p>
     * <p>
     * 适用场景：文档处理完成后，将处理结果发送到该交换机，
     * 由绑定的结果队列消费，执行状态更新、用户通知等操作
     * </p>
     */
    public static final String RESULT_EXCHANGE =
            "smart.document.result.exchange";

    /**
     * 文档结果通知队列
     * <p>
     * 监听该队列的消费者负责处理文档处理完成后的后续操作：
     * <ul>
     *     <li>更新文档状态（处理成功/失败）</li>
     *     <li>发送站内信/邮件/短信通知用户</li>
     *     <li>记录审计日志</li>
     * </ul>
     * </p>
     * <p>
     * 队列特性：持久化（durable），防止消息丢失
     * </p>
     */
    public static final String RESULT_QUEUE =
            "smart.document.result.queue";

    /**
     * 文档结果通知路由键
     * <p>
     * 用于将消息从结果交换机路由到结果队列
     * </p>
     */
    public static final String RESULT_ROUTING_KEY =
            "document.process.result";
}