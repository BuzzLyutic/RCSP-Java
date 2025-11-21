package com.example.dfs.controller;

import com.example.dfs.model.FileMetadata;
import com.example.dfs.model.Node;
import com.example.dfs.service.FileStorageService;
import com.example.dfs.service.NodeDiscoveryService;
import com.example.dfs.service.ReplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API для работы с файлами
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final ReplicationService replicationService;
    private final NodeDiscoveryService nodeDiscoveryService;

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("nodeId", nodeDiscoveryService.getCurrentNodeId());
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }

    /**
     * Информация о системе
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("nodeId", nodeDiscoveryService.getCurrentNodeId());
        info.put("activeNodes", nodeDiscoveryService.getNodeCount());
        info.put("storedFiles", fileStorageService.getAllFiles().size());
        info.put("nodes", nodeDiscoveryService.getActiveNodes());
        return ResponseEntity.ok(info);
    }

    /**
     * Загрузить файл
     */
    @PostMapping("/files/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        try {
            log.info("📤 Получен запрос на загрузку файла: {} ({} bytes)",
                    file.getOriginalFilename(), file.getSize());

            // Сохраняем локально
            FileMetadata metadata = fileStorageService.saveFile(file);

            // Реплицируем на другие узлы (асинхронно)
            replicationService.replicateFile(file.getOriginalFilename(), metadata);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fileName", file.getOriginalFilename());
            response.put("size", file.getSize());
            response.put("nodeId", nodeDiscoveryService.getCurrentNodeId());
            response.put("message", "Файл сохранён локально, репликация запущена");

            log.info("✓ Файл {} загружен успешно", file.getOriginalFilename());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("✗ Ошибка загрузки файла", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Скачать файл
     */
    @GetMapping("/files/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            log.info("Получен запрос на скачивание файла: {}", fileName);

            byte[] fileContent;

            // Проверяем локальное хранилище
            if (fileStorageService.hasFile(fileName)) {
                File file = fileStorageService.getFile(fileName);
                fileContent = FileUtils.readFileToByteArray(file);
                log.info("✓ Файл {} найден локально", fileName);
            } else {
                // Ищем на других узлах
                log.info("⚠ Файл {} не найден локально, ищем на других узлах", fileName);
                fileContent = replicationService.findFileOnOtherNodes(fileName);

                if (fileContent == null) {
                    log.warn("✗ Файл {} не найден нигде", fileName);
                    return ResponseEntity.notFound().build();
                }

                log.info("✓ Файл {} получен с другого узла", fileName);
            }

            ByteArrayResource resource = new ByteArrayResource(fileContent);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileContent.length)
                    .body(resource);

        } catch (Exception e) {
            log.error("✗ Ошибка скачивания файла {}", fileName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint для репликации (вызывается другими узлами)
     */
    @PostMapping("/files/replicate")
    public ResponseEntity<String> replicateFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "contentType", required = false) String contentType) {

        try {
            log.info("Получен файл для репликации: {}", file.getOriginalFilename());

            fileStorageService.saveFileFromBytes(
                    file.getOriginalFilename(),
                    file.getBytes(),
                    contentType != null ? contentType : file.getContentType()
            );

            return ResponseEntity.ok("Replicated successfully");

        } catch (Exception e) {
            log.error("Ошибка репликации файла", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Replication failed: " + e.getMessage());
        }
    }

    /**
     * Список всех файлов
     */
    @GetMapping("/files")
    public ResponseEntity<Map<String, Object>> listFiles() {
        Map<String, Object> response = new HashMap<>();
        response.put("nodeId", nodeDiscoveryService.getCurrentNodeId());
        response.put("files", fileStorageService.getAllFiles());
        return ResponseEntity.ok(response);
    }
}
