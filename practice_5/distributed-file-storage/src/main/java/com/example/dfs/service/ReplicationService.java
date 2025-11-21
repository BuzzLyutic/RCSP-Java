package com.example.dfs.service;

import com.example.dfs.model.FileMetadata;
import com.example.dfs.model.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class ReplicationService {

    private final NodeDiscoveryService nodeDiscoveryService;
    private final FileStorageService fileStorageService;
    private final RestTemplate restTemplate;

    private static final int REPLICATION_FACTOR = 2;

    public ReplicationService(
            NodeDiscoveryService nodeDiscoveryService,
            FileStorageService fileStorageService,
            RestTemplate restTemplate) {
        this.nodeDiscoveryService = nodeDiscoveryService;
        this.fileStorageService = fileStorageService;
        this.restTemplate = restTemplate;
    }

    /**
     * Реплицировать файл на другие узлы (АСИНХРОННО)
     */
    public void replicateFile(String fileName, FileMetadata metadata) {
        // Запускаем в отдельном потоке, чтобы не блокировать ответ клиенту
        new Thread(() -> {
            try {
                List<Node> targetNodes = nodeDiscoveryService.getRandomNodes(REPLICATION_FACTOR);

                if (targetNodes.isEmpty()) {
                    log.warn("Нет доступных узлов для репликации файла: {}", fileName);
                    return;
                }

                log.info("Начало репликации файла {} на {} узлов", fileName, targetNodes.size());

                for (Node node : targetNodes) {
                    try {
                        replicateToNode(fileName, node);
                        metadata.getNodeIds().add(node.getId());
                        log.info("✓ Файл {} реплицирован на узел {}", fileName, node.getId());
                    } catch (Exception e) {
                        log.error("✗ Ошибка репликации на узел {}: {}", node.getId(), e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Критическая ошибка репликации файла {}", fileName, e);
            }
        }).start();
    }

    /**
     * Отправить файл на конкретный узел
     */
    private void replicateToNode(String fileName, Node node) throws Exception {
        File file = fileStorageService.getFile(fileName);

        String url = node.getUrl() + "/api/files/replicate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Используем FileSystemResource вместо ByteArrayResource
        body.add("file", new FileSystemResource(file));

        FileMetadata metadata = fileStorageService.getMetadata(fileName);
        if (metadata != null && metadata.getContentType() != null) {
            body.add("contentType", metadata.getContentType());
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("HTTP " + response.getStatusCode());
        }
    }

    /**
     * Найти файл на других узлах
     */
    public byte[] findFileOnOtherNodes(String fileName) {
        List<Node> nodes = nodeDiscoveryService.getActiveNodes();

        log.info("Поиск файла {} на {} активных узлах", fileName, nodes.size());

        for (Node node : nodes) {
            try {
                String url = node.getUrl() + "/api/files/download/" + fileName;
                ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    log.info("✓ Файл {} найден на узле {}", fileName, node.getId());
                    return response.getBody();
                }
            } catch (Exception e) {
                log.debug("✗ Файл {} не найден на узле {}: {}", fileName, node.getId(), e.getMessage());
            }
        }

        log.warn("Файл {} не найден ни на одном узле", fileName);
        return null;
    }
}
