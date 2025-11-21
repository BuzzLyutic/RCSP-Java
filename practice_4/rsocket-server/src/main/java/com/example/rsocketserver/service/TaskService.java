package com.example.rsocketserver.service;

import com.example.rsocketserver.model.Task;
import com.example.rsocketserver.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private Sinks.Many<Task> taskUpdatesSink; // Убрали final

    // Конструктор
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void init() {
        // Инициализируем Sinks после создания бина
        this.taskUpdatesSink = Sinks.many()
                .multicast()
                .directBestEffort();
    }

    // Request-Response
    public Mono<Task> createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        task.setStatus("NEW");

        return taskRepository.save(task)
                .doOnSuccess(savedTask -> {
                    log.info("Task created: {}", savedTask);
                    if (taskUpdatesSink != null) {
                        Sinks.EmitResult result = taskUpdatesSink.tryEmitNext(savedTask);
                        log.info("Emit result: {}", result);
                    }
                })
                .doFinally(signal -> log.info("Create task completed with signal: {}", signal));
    }

    // Request-Stream
    public Flux<Task> getAllTasks() {
        return taskRepository.findAllOrderByCreatedAtDesc()
                .map(task -> {
                    task.setTitle(task.getTitle().toUpperCase());
                    return task;
                })
                .filter(task -> task.getStatus() != null)
                .take(100)
                .doOnComplete(() -> log.info("Stream completed"))
                .doFinally(signal -> log.info("Get all tasks completed with signal: {}", signal));
    }

    // Fire-and-Forget
    public Mono<Void> updateTaskStatus(Long id, String status) {
        return taskRepository.findById(id)
                .flatMap(task -> {
                    task.setStatus(status);
                    task.setUpdatedAt(LocalDateTime.now());
                    return taskRepository.save(task);
                })
                .doOnSuccess(task -> {
                    log.info("Task status updated: {}", task);
                    if (taskUpdatesSink != null && task != null) {
                        taskUpdatesSink.tryEmitNext(task);
                    }
                })
                .then()
                .doFinally(signal -> log.info("Update status completed with signal: {}", signal));
    }

    // Channel - поток реального времени
    public Flux<Task> taskUpdatesStream() {
        log.info("Client subscribing to task updates stream");

        if (taskUpdatesSink == null) {
            // Для тестов возвращаем простой поток
            return Flux.just(createWelcomeTask());
        }

        return Flux.merge(
                        Flux.just(createWelcomeTask()),
                        taskUpdatesSink.asFlux(),
                        Flux.interval(Duration.ofSeconds(5))
                                .map(i -> {
                                    Task periodicUpdate = new Task();
                                    periodicUpdate.setId(1000L + i);
                                    periodicUpdate.setTitle("Periodic Update #" + i);
                                    periodicUpdate.setDescription("Server heartbeat");
                                    periodicUpdate.setStatus("HEARTBEAT");
                                    periodicUpdate.setCreatedAt(LocalDateTime.now());
                                    periodicUpdate.setUpdatedAt(LocalDateTime.now());
                                    log.info("Sending periodic update #{}", i);
                                    return periodicUpdate;
                                })
                )
                .doOnSubscribe(s -> log.info("Client subscribed to updates"))
                .doOnCancel(() -> log.info("Client cancelled subscription"))
                .doOnError(error -> log.error("Error in updates stream: ", error))
                .doFinally(signal -> log.info("Updates stream terminated with signal: {}", signal));
    }

    private Task createWelcomeTask() {
        Task welcomeTask = new Task();
        welcomeTask.setId(0L);
        welcomeTask.setTitle("Connected to updates stream");
        welcomeTask.setDescription("You will receive real-time updates");
        welcomeTask.setStatus("CONNECTED");
        welcomeTask.setCreatedAt(LocalDateTime.now());
        welcomeTask.setUpdatedAt(LocalDateTime.now());
        return welcomeTask;
    }

    // Channel - обработка множественных задач
    public Flux<Task> processTasks(Flux<Task> tasks) {
        return tasks
                .flatMap(task -> {
                    log.info("Processing task: {}", task.getTitle());

                    task.setStatus("PROCESSING");
                    task.setCreatedAt(LocalDateTime.now());
                    task.setUpdatedAt(LocalDateTime.now());

                    return taskRepository.save(task)
                            .delayElement(Duration.ofSeconds(1))
                            .map(savedTask -> {
                                savedTask.setStatus("COMPLETED");
                                savedTask.setUpdatedAt(LocalDateTime.now());
                                return savedTask;
                            })
                            .flatMap(taskRepository::save)
                            .doOnNext(completedTask -> {
                                log.info("Task processed: {}", completedTask);
                                if (taskUpdatesSink != null) {
                                    taskUpdatesSink.tryEmitNext(completedTask);
                                }
                            });
                })
                .doOnComplete(() -> log.info("All tasks processed"))
                .doFinally(signal -> log.info("Process tasks completed with signal: {}", signal));
    }
}
