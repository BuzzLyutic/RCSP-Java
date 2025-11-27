package com.example.space;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Comparator;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository repository;

    /**
     * Создание миссии
     * Демонстрация: Mono<>
     */
    public Mono<Mission> create(Mission mission) {
        return repository.save(mission)
                .doOnSuccess(m -> log.info("Mission created: {}", m.getName()))
                // Обработка ошибок
                .onErrorResume(e -> {
                    log.error("Failed to create mission: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Mission creation failed"));
                });
    }

    /**
     * Получение по ID
     * Демонстрация: Mono<>, обработка ошибок через switchIfEmpty
     */
    public Mono<Mission> getById(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(
                        new RuntimeException("Mission not found: " + id)));
    }

    /**
     * Получение всех миссий
     * Демонстрация: Flux<>, операторы sort и map
     */
    public Flux<Mission> getAll() {
        return repository.findAll()
                // Оператор преобразования: сортировка по году запуска
                .sort(Comparator.comparing(Mission::getLaunchYear,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                // Оператор преобразования: установка значений по умолчанию
                .map(mission -> {
                    if (mission.getStatus() == null) {
                        mission.setStatus("PLANNED");
                    }
                    return mission;
                });
    }

    /**
     * Обновление миссии
     * Демонстрация: цепочка flatMap
     */
    public Mono<Mission> update(Long id, Mission mission) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Mission not found: " + id)))
                .flatMap(existing -> {
                    existing.setName(mission.getName());
                    existing.setDestination(mission.getDestination());
                    existing.setLaunchYear(mission.getLaunchYear());
                    existing.setStatus(mission.getStatus());
                    existing.setCrewSize(mission.getCrewSize());
                    return repository.save(existing);
                });
    }

    /**
     * Удаление миссии
     */
    public Mono<Void> delete(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Mission not found: " + id)))
                .flatMap(repository::delete);
    }

    /**
     * Поиск по пункту назначения
     * Демонстрация: Flux<>, оператор filter
     */
    public Flux<Mission> getByDestination(String destination) {
        return repository.findByDestination(destination)
                // Оператор: фильтруем только запущенные или завершённые
                .filter(m -> !"FAILED".equals(m.getStatus()));
    }

    /**
     * Стриминг миссий с backpressure
     * Демонстрация: limitRate, delayElements, onBackpressureBuffer
     */
    public Flux<Mission> streamMissions() {
        return repository.findAll()
                // BACKPRESSURE: ограничиваем скорость запросов
                .limitRate(2)
                // Задержка между элементами для демонстрации
                .delayElements(Duration.ofMillis(500))
                // Буфер при переполнении
                .onBackpressureBuffer(10,
                        dropped -> log.warn("Dropped mission: {}", dropped.getName()))
                .doOnNext(m -> log.info("Streaming: {}", m.getName()));
    }

    /**
     * Подсчёт экипажа всех активных миссий
     * Демонстрация: reduce оператор
     */
    public Mono<Integer> getTotalCrew() {
        return repository.findByStatus("LAUNCHED")
                // Оператор reduce: суммируем экипаж
                .map(Mission::getCrewSize)
                .reduce(0, Integer::sum);
    }
}
