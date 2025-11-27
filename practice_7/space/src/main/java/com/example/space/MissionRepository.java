package com.example.space;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface MissionRepository extends R2dbcRepository<Mission, Long> {

    Flux<Mission> findByDestination(String destination);

    Flux<Mission> findByStatus(String status);
}
