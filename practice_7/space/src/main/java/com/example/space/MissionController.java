package com.example.space;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService service;

    /**
     * Endpoint 1: Создание миссии
     * Возвращает: Mono<Mission>
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Mission> create(@RequestBody Mission mission) {
        return service.create(mission);
    }

    /**
     * Endpoint 2: Получение по ID
     * Возвращает: Mono<Mission>
     */
    @GetMapping("/{id}")
    public Mono<Mission> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /**
     * Endpoint 3: Получение всех миссий
     * Возвращает: Flux<Mission>
     */
    @GetMapping
    public Flux<Mission> getAll() {
        return service.getAll();
    }

    /**
     * Endpoint 4: Обновление миссии
     * Возвращает: Mono<Mission>
     */
    @PutMapping("/{id}")
    public Mono<Mission> update(@PathVariable Long id, @RequestBody Mission mission) {
        return service.update(id, mission);
    }

    /**
     * Endpoint 5: Удаление миссии
     * Возвращает: Mono<Void>
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }

    /**
     * Endpoint 6: Поиск по пункту назначения
     * Возвращает: Flux<Mission>
     */
    @GetMapping("/destination/{destination}")
    public Flux<Mission> getByDestination(@PathVariable String destination) {
        return service.getByDestination(destination);
    }

    /**
     * Endpoint 7: Стриминг миссий (SSE) с backpressure
     * Возвращает: Flux<Mission>
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Mission> stream() {
        return service.streamMissions();
    }

    /**
     * Endpoint 8: Общее количество астронавтов в космосе
     * Возвращает: Mono<Integer>
     */
    @GetMapping("/stats/crew")
    public Mono<Integer> getTotalCrew() {
        return service.getTotalCrew();
    }
}
