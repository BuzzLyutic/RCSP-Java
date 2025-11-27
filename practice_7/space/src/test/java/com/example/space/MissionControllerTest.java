package com.example.space;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(MissionController.class)
@ActiveProfiles("test")
class MissionControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private MissionService service;

    @Test
    void create_ReturnsCreated() {
        Mission mission = Mission.builder()
                .id(1L).name("New Mission").destination("Mars").build();

        when(service.create(any(Mission.class))).thenReturn(Mono.just(mission));

        webClient.post()
                .uri("/api/missions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mission)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("New Mission");
    }

    @Test
    void getById_ReturnsMission() {
        Mission mission = Mission.builder().id(1L).name("Test").build();
        when(service.getById(1L)).thenReturn(Mono.just(mission));

        webClient.get()
                .uri("/api/missions/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void getById_NotFound() {
        when(service.getById(999L))
                .thenReturn(Mono.error(new RuntimeException("Mission not found: 999")));

        webClient.get()
                .uri("/api/missions/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAll_ReturnsFlux() {
        Mission m1 = Mission.builder().id(1L).name("M1").build();
        Mission m2 = Mission.builder().id(2L).name("M2").build();

        when(service.getAll()).thenReturn(Flux.just(m1, m2));

        webClient.get()
                .uri("/api/missions")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Mission.class)
                .hasSize(2);
    }

    @Test
    void update_ReturnsUpdated() {
        Mission updated = Mission.builder()
                .id(1L).name("Updated").destination("Moon").build();

        when(service.update(eq(1L), any(Mission.class)))
                .thenReturn(Mono.just(updated));

        webClient.put()
                .uri("/api/missions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated");
    }

    @Test
    void delete_ReturnsNoContent() {
        when(service.delete(1L)).thenReturn(Mono.empty());

        webClient.delete()
                .uri("/api/missions/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getByDestination_ReturnsFlux() {
        Mission m = Mission.builder().id(1L).destination("Mars").build();
        when(service.getByDestination("Mars")).thenReturn(Flux.just(m));

        webClient.get()
                .uri("/api/missions/destination/Mars")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Mission.class)
                .hasSize(1);
    }

    @Test
    void getTotalCrew_ReturnsMono() {
        when(service.getTotalCrew()).thenReturn(Mono.just(15));

        webClient.get()
                .uri("/api/missions/stats/crew")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .isEqualTo(15);
    }

    @Test
    void stream_ReturnsSSE() {
        Mission m = Mission.builder().id(1L).name("Streaming").build();
        when(service.streamMissions()).thenReturn(Flux.just(m));

        webClient.get()
                .uri("/api/missions/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk();
    }
}
