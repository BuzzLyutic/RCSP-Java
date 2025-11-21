package com.example.rsocketserver;

import com.example.rsocketserver.controller.TaskController;
import com.example.rsocketserver.model.Task;
import com.example.rsocketserver.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus("NEW");
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateTask() {
        when(taskService.createTask(any(Task.class)))
                .thenReturn(Mono.just(testTask));

        Mono<Task> result = taskController.createTask(testTask);

        StepVerifier.create(result)
                .expectNext(testTask)
                .verifyComplete();
    }

    @Test
    void testGetAllTasks() {
        when(taskService.getAllTasks())
                .thenReturn(Flux.just(testTask));

        Flux<Task> result = taskController.getAllTasks();

        StepVerifier.create(result)
                .expectNext(testTask)
                .verifyComplete();
    }

    @Test
    void testUpdateTaskStatus() {
        TaskController.TaskStatusUpdate update =
                new TaskController.TaskStatusUpdate(1L, "COMPLETED");

        when(taskService.updateTaskStatus(1L, "COMPLETED"))
                .thenReturn(Mono.empty());

        Mono<Void> result = taskController.updateTaskStatus(update);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void testTaskUpdatesStream() {
        when(taskService.taskUpdatesStream())
                .thenReturn(Flux.just(testTask));

        Flux<Task> result = taskController.taskUpdates();

        StepVerifier.create(result)
                .expectNext(testTask)
                .verifyComplete();
    }

    @Test
    void testProcessTasks() {
        Flux<Task> inputTasks = Flux.just(testTask);
        Task processedTask = new Task();
        processedTask.setId(1L);
        processedTask.setStatus("COMPLETED");

        when(taskService.processTasks(any()))
                .thenReturn(Flux.just(processedTask));

        Flux<Task> result = taskController.processTasks(inputTasks);

        StepVerifier.create(result)
                .expectNext(processedTask)
                .verifyComplete();
    }
}
