package com.example.rsocketserver.repository;

import com.example.rsocketserver.model.Task;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TaskRepository extends ReactiveCrudRepository<Task, Long> {
    Flux<Task> findByStatus(String status);

    @Query("SELECT * FROM tasks ORDER BY created_at DESC")
    Flux<Task> findAllOrderByCreatedAtDesc();
}