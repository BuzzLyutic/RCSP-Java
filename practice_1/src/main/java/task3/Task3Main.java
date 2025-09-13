package main.java.task3;

// src/main/java/task3/Task3Main.java

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Task3Main {
    private static final int QUEUE_CAPACITY = 5;
    private static final int SIMULATION_TIME_SECONDS = 20;
    
    public static void main(String[] args) {
        System.out.println("Система обработки файлов");
        System.out.println();
        System.out.println("Параметры системы:");
        System.out.println("  • Размер очереди: " + QUEUE_CAPACITY);
        System.out.println("  • Время симуляции: " + SIMULATION_TIME_SECONDS + " секунд");
        System.out.println("  • Типы файлов: XML, JSON, XLS");
        System.out.println("  • Время обработки: размер × 7 мс");
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println();
        
        BlockingQueue<File> queue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        
        FileGenerator generator = new FileGenerator(queue);
        Thread generatorThread = new Thread(generator, "FileGenerator");
        
        List<FileProcessor> processors = new ArrayList<>();
        List<Thread> processorThreads = new ArrayList<>();
        
        for (File.FileType type : File.FileType.values()) {
            for (int i = 1; i <= 2; i++) {
                String processorName = "Processor-" + type + "-" + i;
                FileProcessor processor = new FileProcessor(processorName, type, queue);
                processors.add(processor);
                
                Thread thread = new Thread(processor, processorName);
                processorThreads.add(thread);
            }
        }
        
        generatorThread.start();
        for (Thread thread : processorThreads) {
            thread.start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println();
        
        try {
            Thread.sleep(SIMULATION_TIME_SECONDS * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println("Остановка системы...");
        System.out.println();
        
        generator.stop();
        generatorThread.interrupt();
        
        try {
            generatorThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Ожидание завершения обработки оставшихся файлов...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        for (FileProcessor processor : processors) {
            processor.stop();
        }
        
        for (Thread thread : processorThreads) {
            thread.interrupt();
        }
        
        try {
            for (Thread thread : processorThreads) {
                thread.join(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Итоговая статистика
        System.out.println();
        System.out.println("════════════════════════════════════════════");
        System.out.println("Итог:");
        
        int totalGenerated = generator.getGeneratedCount();
        int totalProcessed = processors.stream()
            .mapToInt(FileProcessor::getProcessedCount)
            .sum();
        int filesInQueue = queue.size();
        
        System.out.println("Файлов сгенерировано: " + totalGenerated);
        System.out.println("Файлов обработано: " + totalProcessed);
        System.out.println("Файлов осталось в очереди: " + filesInQueue);
        System.out.println();
        
        if (totalGenerated > 0) {
            double efficiency = (double) totalProcessed / totalGenerated * 100;
            System.out.printf("Эффективность системы: %.1f%%\n", efficiency);
        }
        
        System.out.println();
        System.out.println("Система остановлена");
    }
}
