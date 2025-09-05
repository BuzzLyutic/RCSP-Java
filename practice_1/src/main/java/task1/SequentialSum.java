package main.java.task1;

// src/main/java/SequentialSum.java
public class SequentialSum {
    
    public static long calculate(int[] array) {
        long sum = 0;
        
        for (int i = 0; i < array.length; i++) {
            // Выполняем сложение
            sum += array[i];
            
            // Добавляем задержку после операции
            simulateWork();
            
            // Выводим прогресс каждые 1000 элементов
            if ((i + 1) % 1000 == 0) {
                System.out.println("  Обработано элементов: " + (i + 1) + "/" + array.length);
            }
        }
        
        return sum;
    }
    
    private static void simulateWork() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Поток был прерван");
        }
    }
}
