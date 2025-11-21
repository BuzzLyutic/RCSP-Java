package com.example.rsocketserver.controller;

import com.example.rsocketserver.model.Task;
import com.example.rsocketserver.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    // Request-Response
    @MessageMapping("task.create")
    public Mono<Task> createTask(@Payload Task task) {
        log.info("Received create task request: {}", task);
        return taskService.createTask(task);
    }

    // Request-Stream
    @MessageMapping("task.getAll")
    public Flux<Task> getAllTasks() {
        log.info("Received get all tasks request");
        return taskService.getAllTasks();
    }

    // Fire-and-Forget
    @MessageMapping("task.updateStatus")
    public Mono<Void> updateTaskStatus(@Payload TaskStatusUpdate update) {
        log.info("Received update status request: {}", update);
        return taskService.updateTaskStatus(update.getId(), update.getStatus());
    }

    // Channel
    @MessageMapping("task.updates")
    public Flux<Task> taskUpdates() {
        log.info("Client connected to task updates stream");
        return taskService.taskUpdatesStream();
    }

    // Channel - bidirectional
    @MessageMapping("task.process")
    public Flux<Task> processTasks(Flux<Task> tasks) {
        log.info("Received tasks for processing");
        return taskService.processTasks(tasks);
    }

    // Вспомогательный класс
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TaskStatusUpdate {
        private Long id;
        private String status;
    }
}