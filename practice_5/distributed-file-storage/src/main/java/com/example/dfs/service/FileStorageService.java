package com.example.dfs.service;

import com.example.dfs.model.FileMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для локального хранения файлов
 */
@Slf4j
@Service
public class FileStorageService {

    private final Path storageLocation;
    private final Map<String, FileMetadata> fileRegistry = new ConcurrentHashMap<>();

    public FileStorageService(@Value("${storage.location:./storage}") String location) {
        this.storageLocation = Paths.get(location).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.storageLocation);
            log.info("Хранилище инициализировано: {}", this.storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию хранилища", e);
        }
    }

    /**
     * Сохранить файл локально
     */
    public FileMetadata saveFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.contains("..")) {
            throw new IllegalArgumentException("Некорректное имя файла: " + fileName);
        }

        Path targetLocation = storageLocation.resolve(fileName);
        file.transferTo(targetLocation);

        FileMetadata metadata = new FileMetadata(
                fileName,
                file.getSize(),
                file.getContentType()
        );

        fileRegistry.put(fileName, metadata);
        log.info("Файл сохранен локально: {}", fileName);

        return metadata;
    }

    /**
     * Сохранить файл из байтов (для репликации)
     */
    public void saveFileFromBytes(String fileName, byte[] content, String contentType) throws IOException {
        Path targetLocation = storageLocation.resolve(fileName);
        FileUtils.writeByteArrayToFile(targetLocation.toFile(), content);

        FileMetadata metadata = new FileMetadata(
                fileName,
                content.length,
                contentType
        );

        fileRegistry.put(fileName, metadata);
        log.info("Файл реплицирован локально: {}", fileName);
    }

    /**
     * Получить файл
     */
    public File getFile(String fileName) {
        Path filePath = storageLocation.resolve(fileName).normalize();
        File file = filePath.toFile();

        if (!file.exists()) {
            throw new RuntimeException("Файл не найден: " + fileName);
        }

        return file;
    }

    /**
     * Проверить наличие файла
     */
    public boolean hasFile(String fileName) {
        return storageLocation.resolve(fileName).toFile().exists();
    }

    /**
     * Получить метаданные файла
     */
    public FileMetadata getMetadata(String fileName) {
        return fileRegistry.get(fileName);
    }

    /**
     * Получить все файлы
     */
    public Map<String, FileMetadata> getAllFiles() {
        return new ConcurrentHashMap<>(fileRegistry);
    }
}
