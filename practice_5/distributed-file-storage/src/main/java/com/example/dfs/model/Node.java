package com.example.dfs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель узла в распределенной системе
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    private String id;           // Уникальный идентификатор узла
    private String host;         // IP или hostname
    private int port;            // Порт
    private boolean active;      // Активен ли узел
    private long lastSeen;       // Время последней проверки

    public String getUrl() {
        return "http://" + host + ":" + port;
    }

    @Override
    public String toString() {
        return id + " (" + getUrl() + ")";
    }
}
