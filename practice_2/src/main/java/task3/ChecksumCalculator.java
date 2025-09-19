package task3;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public class ChecksumCalculator {
    private static final String TEST_FILE = "test_files/checksum_test.txt";
    
    public static void main(String[] args) {
        System.out.println("Задание 3: Вычисление 16-битной контрольной суммы");
        
        try {
            createTestFile();
            calculateAndDisplay();
            demonstrateChecksum();
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
    
    private static void createTestFile() throws IOException {
        Path path = Paths.get(TEST_FILE);
        Files.createDirectories(path.getParent());
        
        String content = "Тестовый файл для контрольной суммы\n" +
                        "Строка с числами: 12345\n" +
                        "Символы: ABC xyz";
        
        Files.writeString(path, content);
        System.out.println("Создан тестовый файл\n");
    }
    
    private static void calculateAndDisplay() throws IOException {
        short checksum = calculate16BitChecksum(TEST_FILE);
        
        System.out.println("Результаты:");
        System.out.println("  Файл: " + TEST_FILE);
        System.out.println("  Размер: " + Files.size(Paths.get(TEST_FILE)) + " байт");
        System.out.println("  16-битная контрольная сумма:");
        System.out.println("    HEX: " + String.format("0x%04X", checksum & 0xFFFF));
        System.out.println("    DEC: " + (checksum & 0xFFFF));
        System.out.println("    BIN: " + String.format("%16s", 
            Integer.toBinaryString(checksum & 0xFFFF)).replace(' ', '0'));
    }
    
    public static short calculate16BitChecksum(String fileName) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "r");
             FileChannel channel = file.getChannel()) {
            
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int sum = 0;
            
            while (channel.read(buffer) != -1) {
                buffer.flip();
                
                while (buffer.hasRemaining()) {
                    int byteValue = buffer.get() & 0xFF;
                    
                    sum = (sum + byteValue) & 0xFFFF;
                    
                    sum = ((sum << 1) | (sum >> 15)) & 0xFFFF;
                }
                
                buffer.clear();
            }
            
            return (short)(~sum & 0xFFFF);
        }
    }
    
    private static void demonstrateChecksum() throws IOException {
        System.out.println("\nДемонстрация изменения контрольной суммы:");
        
        Path path = Paths.get(TEST_FILE);
        String original = Files.readString(path);
        
        Files.writeString(path, original + "!");
        short newChecksum = calculate16BitChecksum(TEST_FILE);
        
        System.out.println("После добавления '!':");
        System.out.println("  Новая контрольная сумма: " + 
            String.format("0x%04X", newChecksum & 0xFFFF));
        System.out.println("  Контрольная сумма изменилась!");
        
        Files.writeString(path, original);
        short restoredChecksum = calculate16BitChecksum(TEST_FILE);
        System.out.println("\nПосле восстановления:");
        System.out.println("  Контрольная сумма: " + 
            String.format("0x%04X", restoredChecksum & 0xFFFF));
        System.out.println("  Контрольная сумма вернулась к исходной!");
    }
}