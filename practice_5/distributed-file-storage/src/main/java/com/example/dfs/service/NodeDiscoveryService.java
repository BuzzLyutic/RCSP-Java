package com.example.dfs.service;

import com.example.dfs.model.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NodeDiscoveryService {

    private final String currentNodeId;
    private final int currentNodePort;
    private final Map<String, Node> knownNodes = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate;  // Инжектим через конструктор

    public NodeDiscoveryService(
            @Value("${node.id}") String nodeId,
            @Value("${server.port}") int port,
            @Value("${node.peers:}") String[] peers,
            RestTemplate restTemplate) {  // Добавили параметр

        this.currentNodeId = nodeId;
        this.currentNodePort = port;
        this.restTemplate = restTemplate;  // Сохраняем

        log.info("Инициализация узла: {} на порту {}", nodeId, port);

        if (peers != null) {
            for (String peer : peers) {
                if (!peer.isEmpty()) {
                    registerPeer(peer);
                }
            }
        }
    }

    private void registerPeer(String peerString) {
        try {
            String[] parts = peerString.split("@");
            String peerId = parts[0];
            String[] hostPort = parts[1].split(":");
            String host = hostPort[0];
            int port = Integer.parseInt(hostPort[1]);

            Node node = new Node(peerId, host, port, true, System.currentTimeMillis());
            knownNodes.put(peerId, node);
            log.info("Зарегистрирован peer: {}", node);
        } catch (Exception e) {
            log.error("Ошибка парсинга peer: {}", peerString, e);
        }
    }

    public List<Node> getActiveNodes() {
        return knownNodes.values().stream()
                .filter(Node::isActive)
                .toList();
    }

    public List<Node> getRandomNodes(int count) {
        List<Node> active = getActiveNodes();

        if (active.isEmpty()) {
            return Collections.emptyList();
        }

        // Создаём изменяемую копию списка (важно для shuffle!)
        List<Node> mutableList = new ArrayList<>(active);
        Collections.shuffle(mutableList);

        return mutableList.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 30000)
    public void checkNodesHealth() {
        log.debug("Проверка здоровья {} узлов", knownNodes.size());

        for (Node node : knownNodes.values()) {
            try {
                String healthUrl = node.getUrl() + "/api/health";
                restTemplate.getForObject(healthUrl, String.class);
                node.setActive(true);
                node.setLastSeen(System.currentTimeMillis());
                log.debug("Узел {} активен", node.getId());
            } catch (Exception e) {
                node.setActive(false);
                log.warn("Узел {} недоступен: {}", node.getId(), e.getMessage());
            }
        }
    }

    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public int getNodeCount() {
        return (int) knownNodes.values().stream().filter(Node::isActive).count();
    }
}
