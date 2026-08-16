package com.wkr.document.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param path 相对存储路径
     * @return 文件存储路径
     */
    String upload(MultipartFile file, String path);

    /**
     * 删除文件
     *
     * @param path 文件存储路径
     */
    void delete(String path);

    /**
     * 加载文件
     *
     * @param path 文件存储路径
     * @return 文件资源
     */
    Resource load(String path);
}