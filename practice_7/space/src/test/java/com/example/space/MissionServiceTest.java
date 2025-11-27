package com.example.space;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository repository;

    @InjectMocks
    private MissionService service;

    private Mission testMission;

    @BeforeEach
    void setUp() {
        testMission = Mission.builder()
                .id(1L)
                .name("Test Mission")
                .destination("Mars")
                .launchYear(2025)
                .status("PLANNED")
                .crewSize(4)
                .build();
    }

    @Test
    void create_Success() {
        when(repository.save(any(Mission.class))).thenReturn(Mono.just(testMission));

        StepVerifier.create(service.create(testMission))
                .expectNextMatches(m -> m.getName().equals("Test Mission"))
                .verifyComplete();
    }

    @Test
    void getById_Success() {
        when(repository.findById(1L)).thenReturn(Mono.just(testMission));

        StepVerifier.create(service.getById(1L))
                .expectNextMatches(m -> m.getDestination().equals("Mars"))
                .verifyComplete();
    }

    @Test
    void getById_NotFound() {
        when(repository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(service.getById(999L))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getAll_SortedByYear() {
        Mission older = Mission.builder().id(2L).name("Old").launchYear(1969).build();
        Mission newer = Mission.builder().id(3L).name("New").launchYear(2030).build();

        when(repository.findAll()).thenReturn(Flux.just(newer, older));

        StepVerifier.create(service.getAll())
                .expectNextMatches(m -> m.getLaunchYear().equals(1969))
                .expectNextMatches(m -> m.getLaunchYear().equals(2030))
                .verifyComplete();
    }

    @Test
    void update_Success() {
        Mission updated = Mission.builder()
                .id(1L).name("Updated").destination("Moon")
                .launchYear(2026).status("LAUNCHED").crewSize(6)
                .build();

        when(repository.findById(1L)).thenReturn(Mono.just(testMission));
        when(repository.save(any(Mission.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(service.update(1L, updated))
                .expectNextMatches(m -> m.getName().equals("Updated"))
                .verifyComplete();
    }

    @Test
    void delete_Success() {
        when(repository.findById(1L)).thenReturn(Mono.just(testMission));
        when(repository.delete(any(Mission.class))).thenReturn(Mono.empty());

        StepVerifier.create(service.delete(1L))
                .verifyComplete();
    }

    @Test
    void getByDestination_FiltersFailed() {
        Mission failed = Mission.builder()
                .id(2L).name("Failed").destination("Mars").status("FAILED").build();

        when(repository.findByDestination("Mars"))
                .thenReturn(Flux.just(testMission, failed));

        StepVerifier.create(service.getByDestination("Mars"))
                .expectNextMatches(m -> m.getStatus().equals("PLANNED"))
                .verifyComplete();
    }

    @Test
    void getTotalCrew_SumsCorrectly() {
        Mission m1 = Mission.builder().id(1L).status("LAUNCHED").crewSize(4).build();
        Mission m2 = Mission.builder().id(2L).status("LAUNCHED").crewSize(7).build();

        when(repository.findByStatus("LAUNCHED")).thenReturn(Flux.just(m1, m2));

        StepVerifier.create(service.getTotalCrew())
                .expectNext(11)
                .verifyComplete();
    }
}
