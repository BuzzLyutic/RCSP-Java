package main.java.task3;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FileProcessor implements Runnable {
    private final String name;
    private final File.FileType supportedType;
    private final BlockingQueue<File> queue;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicInteger processedCount = new AtomicInteger(0);
    private final AtomicInteger skippedCount = new AtomicInteger(0);
    
    public FileProcessor(String name, File.FileType supportedType, BlockingQueue<File> queue) {
        this.name = name;
        this.supportedType = supportedType;
        this.queue = queue;
    }
    
    @Override
    public void run() {
        System.out.println(name + " запущен (обрабатывает " + supportedType + ")");
        
        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                File file = queue.poll(500, TimeUnit.MILLISECONDS);
                
                if (file == null) {
                    continue;
                }
                
                if (file.getType() == supportedType) {
                    processFile(file);
                    processedCount.incrementAndGet();
                } else {
                    boolean returned = queue.offer(file, 100, TimeUnit.MILLISECONDS);
                    if (returned) {
                        skippedCount.incrementAndGet();
                        System.out.println(name + " вернул " + file + 
                                         " (не поддерживает " + file.getType() + ")");
                    } else {
                        System.out.println(name + " не смог вернуть " + file + 
                                         " в очередь");
                    }
                    
                    Thread.sleep(50);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println(name + " остановлен");
        printStatistics();
    }
    
    private void processFile(File file) throws InterruptedException {
        int processingTime = file.getProcessingTime();
        
        System.out.println(name + " начал обработку " + file + 
                         " (время: " + processingTime + " мс)");
        
        Thread.sleep(processingTime);
        
        long processingDuration = System.currentTimeMillis() - file.getCreatedAt();
        System.out.println(name + " завершил " + file + 
                         " (общее время в системе: " + processingDuration + " мс)");
    }
    
    public void stop() {
        running.set(false);
    }
    
    public void printStatistics() {
        System.out.println("Статистика " + name + ":");
        System.out.println("   • Обработано файлов: " + processedCount.get());
        System.out.println("   • Пропущено файлов: " + skippedCount.get());
    }
    
    public int getProcessedCount() {
        return processedCount.get();
    }
}