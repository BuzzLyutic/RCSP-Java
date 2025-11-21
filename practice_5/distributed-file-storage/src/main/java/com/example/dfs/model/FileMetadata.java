package com.example.dfs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Метаданные файла в распределенной системе
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String fileName;           // Имя файла
    private String fileHash;           // Хеш для идентификации
    private long fileSize;             // Размер файла
    private String contentType;        // MIME тип
    private long uploadTime;           // Время загрузки
    private List<String> nodeIds;      // На каких узлах хранится

    public FileMetadata(String fileName, long fileSize, String contentType) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.uploadTime = System.currentTimeMillis();
        this.nodeIds = new ArrayList<>();
    }
}