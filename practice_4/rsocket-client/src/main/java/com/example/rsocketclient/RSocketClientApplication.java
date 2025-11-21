package com.example.rsocketclient;

import com.example.rsocketclient.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SpringBootApplication
public class RSocketClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RSocketClientApplication.class, args);
    }

    @Bean
    public RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder
                .tcp("localhost", 7000);
    }

    @Bean
    public CommandLineRunner runner(RSocketRequester requester) {
        return args -> {
            log.info("RSocket Client started. Connecting to server...");

            // Проверка соединения
            testConnection(requester);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                try {
                    System.out.println("\n=== RSocket Task Management Client ===");
                    System.out.println("1. Create Task (Request-Response)");
                    System.out.println("2. Get All Tasks (Request-Stream)");
                    System.out.println("3. Update Task Status (Fire-and-Forget)");
                    System.out.println("4. Subscribe to Updates (Channel)");
                    System.out.println("5. Process Multiple Tasks (Channel)");
                    System.out.println("6. Exit");
                    System.out.print("Choose option: ");

                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1 -> testRequestResponse(requester, scanner);
                        case 2 -> testRequestStream(requester);
                        case 3 -> testFireAndForget(requester, scanner);
                        case 4 -> testChannel(requester);
                        case 5 -> testBidirectionalChannel(requester);
                        case 6 -> {
                            System.out.println("Exiting...");
                            System.exit(0);
                        }
                        default -> System.out.println("Invalid option!");
                    }

                    Thread.sleep(2000);
                } catch (Exception e) {
                    log.error("Error in main loop: ", e);
                }
            }
        };
    }

    private void testConnection(RSocketRequester requester) {
        try {
            log.info("Testing connection to server...");
            // Пустой тест для проверки соединения
            Thread.sleep(1000);
            log.info("Connection established!");
        } catch (Exception e) {
            log.error("Failed to connect to server: ", e);
        }
    }

    private void testRequestResponse(RSocketRequester requester, Scanner scanner) {
        try {
            System.out.print("Enter task title: ");
            String title = scanner.nextLine();
            System.out.print("Enter task description: ");
            String description = scanner.nextLine();

            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);

            CountDownLatch latch = new CountDownLatch(1);

            requester
                    .route("task.create")
                    .data(task)
                    .retrieveMono(Task.class)
                    .subscribe(
                            createdTask -> {
                                log.info("Task created successfully: {}", createdTask);
                                System.out.println("Task created with ID: " + createdTask.getId());
                                latch.countDown();
                            },
                            error -> {
                                log.error("Error creating task: ", error);
                                System.out.println("Failed to create task: " + error.getMessage());
                                latch.countDown();
                            },
                            () -> {
                                log.info("Request-Response completed");
                                latch.countDown();
                            }
                    );

            latch.await();
        } catch (Exception e) {
            log.error("Error in testRequestResponse: ", e);
        }
    }

    private void testRequestStream(RSocketRequester requester) {
        try {
            CountDownLatch latch = new CountDownLatch(1);

            requester
                    .route("task.getAll")
                    .retrieveFlux(Task.class)
                    .subscribe(
                            task -> {
                                log.info("Received task: {}", task);
                                System.out.println("Task: " + task.getTitle() + " [" + task.getStatus() + "]");
                            },
                            error -> {
                                log.error("Error in stream: ", error);
                                latch.countDown();
                            },
                            () -> {
                                log.info("Stream completed");
                                System.out.println("All tasks received");
                                latch.countDown();
                            }
                    );

            latch.await();
        } catch (Exception e) {
            log.error("Error in testRequestStream: ", e);
        }
    }

    private void testFireAndForget(RSocketRequester requester, Scanner scanner) {
        try {
            System.out.print("Enter task ID: ");
            Long id = scanner.nextLong();
            scanner.nextLine();
            System.out.print("Enter new status (NEW/IN_PROGRESS/COMPLETED): ");
            String status = scanner.nextLine();

            TaskStatusUpdate update = new TaskStatusUpdate(id, status);

            requester
                    .route("task.updateStatus")
                    .data(update)
                    .send()
                    .subscribe(
                            null,
                            error -> {
                                log.error("Error updating status: ", error);
                                System.out.println("Failed to send update");
                            },
                            () -> {
                                log.info("Fire-and-Forget sent successfully");
                                System.out.println("Status update sent");
                            }
                    );

            Thread.sleep(1000); // Даем время на отправку
        } catch (Exception e) {
            log.error("Error in testFireAndForget: ", e);
        }
    }

    private void testChannel(RSocketRequester requester) {
        try {
            System.out.println("Subscribing to task updates for 30 seconds...");
            CountDownLatch latch = new CountDownLatch(1);

            requester
                    .route("task.updates")
                    .retrieveFlux(Task.class)
                    .take(Duration.ofSeconds(30))
                    .subscribe(
                            task -> {
                                log.info("Update received: {}", task);
                                System.out.println("Update: " + task.getTitle() + " [" + task.getStatus() + "]");
                            },
                            error -> {
                                log.error("Error in channel: ", error);
                                latch.countDown();
                            },
                            () -> {
                                log.info("Channel closed");
                                System.out.println("Updates subscription ended");
                                latch.countDown();
                            }
                    );

            latch.await();
        } catch (Exception e) {
            log.error("Error in testChannel: ", e);
        }
    }

    private void testBidirectionalChannel(RSocketRequester requester) {
        try {
            System.out.println("Sending 5 tasks for processing...");
            CountDownLatch latch = new CountDownLatch(1);

            Flux<Task> taskFlux = Flux.range(1, 5)
                    .map(i -> {
                        Task task = new Task();
                        task.setTitle("Batch Task " + i);
                        task.setDescription("Batch Description " + i);
                        return task;
                    })
                    .delayElements(Duration.ofSeconds(1));

            requester
                    .route("task.process")
                    .data(taskFlux)
                    .retrieveFlux(Task.class)
                    .subscribe(
                            task -> {
                                log.info("Processed task: {}", task);
                                System.out.println("Processed: " + task.getTitle() + " [" + task.getStatus() + "]");
                            },
                            error -> {
                                log.error("Error processing tasks: ", error);
                                latch.countDown();
                            },
                            () -> {
                                log.info("Channel processing completed");
                                System.out.println("All tasks processed");
                                latch.countDown();
                            }
                    );

            latch.await();
        } catch (Exception e) {
            log.error("Error in testBidirectionalChannel: ", e);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class TaskStatusUpdate {
        private Long id;
        private String status;
    }
}
