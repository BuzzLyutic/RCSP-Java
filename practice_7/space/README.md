
# 🚀 Практическая работа №7: Reactive Space Missions API

Реактивный REST API сервис для управления космическими миссиями, построенный на **Spring WebFlux**.

## 📋 Требования задания

| Требование | Реализация |
|------------|------------|
| 5+ endpoints | ✅ 8 endpoints |
| Mono<> | ✅ create, getById, update, delete, getTotalCrew |
| Flux<> | ✅ getAll, getByDestination, stream |
| Работа с БД | ✅ PostgreSQL + R2DBC |
| Операторы преобразования | ✅ map, flatMap, filter, sort, reduce |
| Backpressure | ✅ limitRate, onBackpressureBuffer, delayElements |
| Обработка ошибок | ✅ switchIfEmpty, onErrorResume, GlobalExceptionHandler |
| Unit-тесты | ✅ StepVerifier, WebTestClient |

---

## 🏗 Структура проекта

```
src/main/java/com/example/space/
├── SpaceApplication.java      # Точка входа
├── DatabaseConfig.java        # Конфигурация БД и тестовые данные
├── Mission.java               # Entity
├── MissionRepository.java     # Репозиторий (R2DBC)
├── MissionService.java        # Бизнес-логика + реактивные операторы
├── MissionController.java     # REST контроллер
└── GlobalExceptionHandler.java # Обработка ошибок
```

---

## 🛠 Технологии

- **Java 17**
- **Spring Boot 3.2**
- **Spring WebFlux** — реактивный веб-фреймворк
- **Spring Data R2DBC** — реактивный доступ к БД
- **PostgreSQL** — база данных
- **Project Reactor** — реактивная библиотека (Mono/Flux)
- **Docker** — контейнеризация БД

---

## 🚀 Запуск

```bash
# 1. Запуск PostgreSQL
docker-compose up -d

# 2. Запуск приложения
mvn spring-boot:run

# 3. Запуск тестов
mvn test
```

---

## 📡 API Endpoints

| Метод | URL | Описание | Возвращает |
|-------|-----|----------|------------|
| POST | `/api/missions` | Создать миссию | `Mono<Mission>` |
| GET | `/api/missions/{id}` | Получить по ID | `Mono<Mission>` |
| GET | `/api/missions` | Все миссии | `Flux<Mission>` |
| PUT | `/api/missions/{id}` | Обновить | `Mono<Mission>` |
| DELETE | `/api/missions/{id}` | Удалить | `Mono<Void>` |
| GET | `/api/missions/destination/{dest}` | По назначению | `Flux<Mission>` |
| GET | `/api/missions/stream` | SSE стриминг | `Flux<Mission>` |
| GET | `/api/missions/stats/crew` | Экипаж в космосе | `Mono<Integer>` |

### Примеры запросов

```bash
# Создать миссию
curl -X POST http://localhost:8080/api/missions \
  -H "Content-Type: application/json" \
  -d '{"name":"Voyager 3","destination":"Neptune","launchYear":2035,"status":"PLANNED","crewSize":0}'

# Все миссии
curl http://localhost:8080/api/missions

# Стриминг (Server-Sent Events)
curl http://localhost:8080/api/missions/stream
```

---

## 🧪 Тестирование

### Unit-тесты сервиса (MissionServiceTest.java)

```java
@ExtendWith(MockitoExtension.class)  // Подключаем Mockito
class MissionServiceTest {

    @Mock                            // Мок репозитория
    private MissionRepository repository;

    @InjectMocks                     // Внедряем моки в сервис
    private MissionService service;

    @Test
    void getById_NotFound() {
        when(repository.findById(999L)).thenReturn(Mono.empty());

        // StepVerifier — тестирование реактивных потоков
        StepVerifier.create(service.getById(999L))
                .expectError(RuntimeException.class)
                .verify();
    }
}
```

---

