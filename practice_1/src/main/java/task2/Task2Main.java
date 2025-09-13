package main.java.task2;


import java.util.Scanner;
import java.util.concurrent.*;
import java.util.Random;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Task2Main {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Random random = new Random();
    
    private static final Map<Integer, Future<Integer>> activeTasks = new ConcurrentHashMap<>();
    private static final AtomicInteger requestCounter = new AtomicInteger(0);
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Асинхронный калькулятор квадратов");
        System.out.println();
        System.out.println("Команды:");
        System.out.println("  • Введите число для возведения в квадрат");
        System.out.println("  • 'status' - показать активные запросы");
        System.out.println("  • 'exit' - выход из программы");
        System.out.println();
        
        Thread monitor = new Thread(() -> monitorCompletedTasks());
        monitor.setDaemon(true);
        monitor.start();
        
        while (true) {
            System.out.print(">>> ");
            String input = scanner.nextLine().trim();
            
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }
            
            if ("status".equalsIgnoreCase(input)) {
                showStatus();
                continue;
            }
            
            try {
                int number = Integer.parseInt(input);
                submitRequest(number);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число или команду");
            }
        }
        
        System.out.println("\nЗавершение работы...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        
        scanner.close();
        System.out.println("Программа завершена.");
    }
    
    private static void submitRequest(int number) {
        int requestId = requestCounter.incrementAndGet();
        
        Future<Integer> future = executor.submit(new SquareCalculator(number, requestId));
        activeTasks.put(requestId, future);
        
        System.out.println("Запрос #" + requestId + " принят: " + number + " обработка...");
    }
    
    private static void monitorCompletedTasks() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(100);
                
                activeTasks.entrySet().removeIf(entry -> {
                    if (entry.getValue().isDone()) {
                        try {
                            int result = entry.getValue().get();
                            System.out.println("\nЗапрос #" + entry.getKey() + 
                                             " завершен. Результат: " + result);
                            System.out.print(">>> ");
                        } catch (Exception e) {
                            System.out.println("\nЗапрос #" + entry.getKey() + 
                                             " завершен с ошибкой: " + e.getMessage());
                            System.out.print(">>> ");
                        }
                        return true;
                    }
                    return false;
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private static void showStatus() {
        if (activeTasks.isEmpty()) {
            System.out.println("Нет активных запросов");
        } else {
            System.out.println("Активные запросы:");
            activeTasks.forEach((id, future) -> {
                String status = future.isDone() ? "завершен" : "обрабатывается";
                System.out.println("   • Запрос #" + id + ": " + status);
            });
        }
    }
    
    static class SquareCalculator implements Callable<Integer> {
        private final int number;
        private final int requestId;
        
        public SquareCalculator(int number, int requestId) {
            this.number = number;
            this.requestId = requestId;
        }
        
        @Override
        public Integer call() throws Exception {
            int delay = random.nextInt(4000) + 1000;
            
            System.out.println(" Запрос #" + requestId + 
                             ": обработка займет " + (delay / 1000.0) + " сек");
            
            Thread.sleep(delay);
            return number * number;
        }
    }
}
