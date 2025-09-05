package main.java.task3;

// src/main/java/task3/FileGenerator.java

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FileGenerator implements Runnable {
    private final BlockingQueue<File> queue;
    private final Random random = new Random();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicInteger generatedCount = new AtomicInteger(0);
    private final AtomicInteger rejectedCount = new AtomicInteger(0);
    
    public FileGenerator(BlockingQueue<File> queue) {
        this.queue = queue;
    }
    
    @Override
    public void run() {
        System.out.println("🚀 Генератор файлов запущен");
        
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                // Генерируем новый файл
                File file = generateRandomFile();
                
                // Пытаемся добавить в очередь с таймаутом
                boolean added = queue.offer(file, 100, TimeUnit.MILLISECONDS);
                
                if (added) {
                    generatedCount.incrementAndGet();
                    System.out.println("➕ Сгенерирован: " + file + 
                                     " | Очередь: " + queue.size() + "/5");
                } else {
                    rejectedCount.incrementAndGet();
                    System.out.println("⚠️  Очередь полна! Файл отклонен: " + file);
                }
                
                // Задержка между генерациями (100-1000 мс)
                int delay = random.nextInt(900) + 100;
                Thread.sleep(delay);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("🛑 Генератор файлов остановлен");
        printStatistics();
    }
    
    private File generateRandomFile() {
        // Случайный тип файла
        File.FileType[] types = File.FileType.values();
        File.FileType type = types[random.nextInt(types.length)];
        
        // Случайный размер от 10 до 100
        int size = random.nextInt(91) + 10;
        
        return new File(type, size);
    }
    
    public void stop() {
        running.set(false);
    }
    
    public void printStatistics() {
        System.out.println("📊 Статистика генератора:");
        System.out.println("   • Сгенерировано файлов: " + generatedCount.get());
        System.out.println("   • Отклонено файлов: " + rejectedCount.get());
    }
    
    public int getGeneratedCount() {
        return generatedCount.get();
    }
}