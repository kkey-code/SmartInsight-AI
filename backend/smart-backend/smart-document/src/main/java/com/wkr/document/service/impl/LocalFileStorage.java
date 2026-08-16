package com.wkr.document.service.impl;

import com.wkr.core.exception.BusinessException;
import com.wkr.document.service.FileStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalFileStorage implements FileStorage {

    private final Path rootPath;

    public LocalFileStorage(
            @Value("${smart.file.storage-path:./storage}") String storagePath
    ) {
        this.rootPath = Paths.get(storagePath)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new BusinessException(500, "文件存储目录创建失败");
        }
    }

    @Override
    public String upload(MultipartFile file, String path) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }

        if (path == null || path.isBlank()) {
            throw new BusinessException(400, "文件路径不能为空");
        }

        Path relativePath = Paths.get(path).normalize();

        if (relativePath.isAbsolute()
                || relativePath.startsWith("..")) {
            throw new BusinessException(400, "非法文件路径");
        }
        Path target = rootPath
                .resolve(relativePath)
                .normalize();

        if (!target.startsWith(rootPath)) {
            throw new BusinessException(400, "非法文件路径");
        }

        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target);
            return relativePath.toString().replace("\\", "/");

        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败");
        }
    }

    @Override
    public void delete(String path) {

        if (path == null || path.isBlank()) {
            return;
        }
        try {
            Path target = rootPath
                    .resolve(path)
                    .normalize();

            if (!target.startsWith(rootPath)) {
                throw new BusinessException(400, "非法文件路径");
            }
            Files.deleteIfExists(target);

        } catch (IOException e) {
            throw new BusinessException(500, "文件删除失败");
        }
    }

    @Override
    public Resource load(String path) {

        if (path == null || path.isBlank()) {
            throw new BusinessException(404, "文件不存在");
        }

        Path target = rootPath
                .resolve(path)
                .normalize();

        if (!target.startsWith(rootPath)) {
            throw new BusinessException(400, "非法文件路径");
        }

        Resource resource = new FileSystemResource(target);

        if (!resource.exists() || !resource.isReadable()) {
            throw new BusinessException(404, "文件不存在");
        }

        return resource;
    }
}