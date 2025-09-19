package task2;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import org.apache.commons.io.FileUtils;

public class FileCopyComparison {
    private static final int FILE_SIZE = 100 * 1024 * 1024; // 100 MB
    private static final String SOURCE = "test_files/source_100mb.dat";
    private static final String DEST = "test_files/dest_100mb.dat";
    
    public static void main(String[] args) {
        System.out.println("ЗАДАНИЕ 2: Сравнение методов копирования");
        
        try {
            createTestFile();
            runComparison();
            cleanup();
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void createTestFile() throws IOException {
        System.out.print("Создание файла 100 MB... ");
        Path path = Paths.get(SOURCE);
        Files.createDirectories(path.getParent());
        
        try (RandomAccessFile raf = new RandomAccessFile(SOURCE, "rw")) {
            raf.setLength(FILE_SIZE);
        }
        System.out.println("Готово\n");
    }
    
    private static void runComparison() throws IOException {
        System.out.println("Результаты копирования файла 100 MB:\n");
        
        // Метод 1
        testFileStreams();
        
        // Метод 2
        testFileChannel();
        
        // Метод 3
        testApacheCommonsIO();
        
        // Метод 4
        testFilesClass();
        
    }
    
    private static void testFileStreams() throws IOException {
        System.out.println("1. FileInputStream/FileOutputStream");

        Files.deleteIfExists(Paths.get(DEST));
        long start = System.currentTimeMillis();
        
        try (FileInputStream in = new FileInputStream(SOURCE);
             FileOutputStream out = new FileOutputStream(DEST)) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        
        long time = System.currentTimeMillis() - start;
        System.out.println("   Время: " + time + " мс\n");
    }
    
    private static long testFileChannel() throws IOException {
        System.out.println("2. FileChannel (NIO)");

        Files.deleteIfExists(Paths.get(DEST));
        long start = System.currentTimeMillis();
        
        try (FileInputStream fis = new FileInputStream(SOURCE);
             FileOutputStream fos = new FileOutputStream(DEST);
             FileChannel srcChannel = fis.getChannel();
             FileChannel dstChannel = fos.getChannel()) {
            
            long transferred = 0;
            long size = srcChannel.size();
            while (transferred < size) {
                transferred += dstChannel.transferFrom(srcChannel, transferred, size - transferred);
            }
        }
        
        long time = System.currentTimeMillis() - start;
        System.out.println("   Время: " + time + " мс");
        System.out.println("   Обычно самый быстрый метод!\n");
        return time;
    }
    
    private static void testApacheCommonsIO() throws IOException {
        System.out.println("3. Apache Commons IO");

        Files.deleteIfExists(Paths.get(DEST));
        long start = System.currentTimeMillis();
        
        FileUtils.copyFile(new File(SOURCE), new File(DEST));
        
        long time = System.currentTimeMillis() - start;
        System.out.println("   Время: " + time + " мс\n");
    }
    
    private static void testFilesClass() throws IOException {
        System.out.println("4. Files.copy() (NIO.2)");

        Files.deleteIfExists(Paths.get(DEST));
        long start = System.currentTimeMillis();
        
        Files.copy(Paths.get(SOURCE), Paths.get(DEST));
        
        long time = System.currentTimeMillis() - start;
        System.out.println("   Время: " + time + " мс\n");
    }

    private static void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(SOURCE));
        Files.deleteIfExists(Paths.get(DEST));
        System.out.println("\nВременные файлы удалены");
    }
}
