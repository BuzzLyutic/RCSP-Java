package main.java.task4;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DirectoryWatcher {
    private static final String WATCH_DIR = "test_files/watch_dir";
    private static final Map<Path, FileSnapshot> snapshots = new ConcurrentHashMap<>();
    
    static class FileSnapshot {
        List<String> content;
        long size;
        short checksum;
        
        FileSnapshot(List<String> content, long size, short checksum) {
            this.content = content;
            this.size = size;
            this.checksum = checksum;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("ЗАДАНИЕ 4: Мониторинг директории");
        
        try {
            Path dir = Paths.get(WATCH_DIR);
            Files.createDirectories(dir);
            
            System.out.println("Наблюдение за: " + dir.toAbsolutePath());
            System.out.println("\nИнструкция:");
            System.out.println("1. Создайте файл в папке " + WATCH_DIR);
            System.out.println("2. Измените его содержимое");
            System.out.println("3. Удалите файл");
            System.out.println("\nНажмите Ctrl+C для выхода\n");
            
            startWatching(dir);
            
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void startWatching(Path dir) throws Exception {
        // Сканируем существующие файлы
        scanDirectory(dir);
        
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            
            dir.register(watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
            
            while (true) {
                WatchKey key = watcher.take();
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path fileName = (Path) event.context();
                    Path fullPath = dir.resolve(fileName);
                                        
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        handleCreate(fullPath);
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        handleModify(fullPath);
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        handleDelete(fullPath);
                    }
                    
                }
                
                if (!key.reset()) break;
            }
        }
    }
    
    private static void scanDirectory(Path dir) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    saveSnapshot(file);
                }
            }
        }
    }
    
    private static void handleCreate(Path file) {
        System.out.println("СОЗДАН ФАЙЛ: " + file.getFileName());
        
        try {
            Thread.sleep(100); // Ждем завершения создания
            
            if (Files.exists(file)) {
                long size = Files.size(file);
                System.out.println("   Размер: " + size + " байт");
                saveSnapshot(file);
            }
        } catch (Exception e) {
            System.err.println("   Ошибка: " + e.getMessage());
        }
    }
    
    private static void handleModify(Path file) {
        System.out.println("ИЗМЕНЕН ФАЙЛ: " + file.getFileName());
        
        try {
            Thread.sleep(100);
            
            FileSnapshot old = snapshots.get(file);
            if (old != null && isTextFile(file)) {
                List<String> newContent = Files.readAllLines(file);
                
                System.out.println("\n   Изменения:");
                compareContents(old.content, newContent);
            }
            
            saveSnapshot(file);
            
        } catch (Exception e) {
            System.err.println("   Ошибка: " + e.getMessage());
        }
    }
    
    private static void handleDelete(Path file) {
        System.out.println("УДАЛЕН ФАЙЛ: " + file.getFileName());
        
        FileSnapshot snapshot = snapshots.get(file);
        if (snapshot != null) {
            System.out.println("   Размер: " + snapshot.size + " байт");
            System.out.println("   Контрольная сумма: " + 
                String.format("0x%04X", snapshot.checksum & 0xFFFF));
        } else {
            System.out.println("   Информация недоступна");
            System.out.println("   Причина: файл не может быть прочитан после удаления");
        }
        
        snapshots.remove(file);
    }
    
    private static void saveSnapshot(Path file) throws IOException {
        if (!Files.exists(file)) return;
        
        long size = Files.size(file);
        short checksum = calculateChecksum(file);
        List<String> content = null;
        
        if (isTextFile(file)) {
            content = Files.readAllLines(file);
        }
        
        snapshots.put(file, new FileSnapshot(content, size, checksum));
    }
    
    private static void compareContents(List<String> old, List<String> current) {
        Set<String> oldSet = new HashSet<>(old);
        Set<String> newSet = new HashSet<>(current);
        
        // Добавленные строки
        for (String line : current) {
            if (!oldSet.contains(line)) {
                System.out.println("" + line);
            }
        }
        
        // Удаленные строки
        for (String line : old) {
            if (!newSet.contains(line)) {
                System.out.println("" + line);
            }
        }
    }
    
    private static short calculateChecksum(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        short sum = 0;
        for (byte b : bytes) {
            sum += b;
        }
        return sum;
    }
    
    private static boolean isTextFile(Path file) {
        String name = file.toString().toLowerCase();
        return name.endsWith(".txt") || name.endsWith(".log") || 
               name.endsWith(".md") || name.endsWith(".java");
    }
}
