package main.java.task1;

import java.nio.file.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class FileReaderTask {
    private static final String TEST_FILE = "test_files/test.txt";
    
    public static void main(String[] args) {
        System.out.println("Задание 1: Чтение файла через java.nio");
        
        createTestFile();
        readFileUsingNIO();
    }
    
    private static void createTestFile() {
        try {
            Path path = Paths.get(TEST_FILE);
            Files.createDirectories(path.getParent());
            
            String content = """
                Строка 1: Это тестовый файл для демонстрации java.nio
                Строка 2: Вторая строка с данными
                Строка 3: Числовые данные: 12345, 67890
                Строка 4: Специальные символы: @#$%^&*()
                Строка 5: Последняя строка файла
                """;
            
            Files.writeString(path, content, StandardCharsets.UTF_8);
            System.out.println("Тестовый файл создан: " + TEST_FILE);
            
        } catch (IOException e) {
            System.err.println("Ошибка создания файла: " + e.getMessage());
        }
    }
    
    private static void readFileUsingNIO() {
        System.out.println("\nЧтение файла методом Files.lines() (java.nio):");
        
        try {
            Path path = Paths.get(TEST_FILE);
            
            try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
                lines.forEach(line -> System.out.println("  " + line));
            }
            
            System.out.println("Файл успешно прочитан\n");
            
            long size = Files.size(path);
            System.out.println("Информация о файле:");
            System.out.println("  Размер: " + size + " байт");
            System.out.println("  Путь: " + path.toAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}
