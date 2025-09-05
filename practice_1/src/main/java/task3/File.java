package main.java.task3;

// src/main/java/task3/File.java

import java.util.concurrent.atomic.AtomicLong;

public class File {
    // Счетчик для уникальных ID файлов
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    
    public enum FileType {
        XML("XML", "🟦"),
        JSON("JSON", "🟩"),
        XLS("XLS", "🟨");
        
        private final String name;
        private final String icon;
        
        FileType(String name, String icon) {
            this.name = name;
            this.icon = icon;
        }
        
        public String getIcon() {
            return icon;
        }
        
        @Override
        public String toString() {
            return name;
        }
    }
    
    private final long id;
    private final FileType type;
    private final int size;
    private final long createdAt;
    
    public File(FileType type, int size) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.type = type;
        this.size = size;
        this.createdAt = System.currentTimeMillis();
    }
    
    public long getId() {
        return id;
    }
    
    public FileType getType() {
        return type;
    }
    
    public int getSize() {
        return size;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public int getProcessingTime() {
        return size * 7;
    }
    
    @Override
    public String toString() {
        return String.format("%s File #%d [size=%dKB]", 
                           type.getIcon(), id, size);
    }
}