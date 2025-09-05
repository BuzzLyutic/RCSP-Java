package main.java.task1;

import java.util.Random;

public class Main {
    private static final int ARRAY_SIZE = 10000;
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    
    public static void main(String[] args) {
        System.out.println("=== ПОИСК СУММЫ ЭЛЕМЕНТОВ МАССИВА ===");
        System.out.println("Размер массива: " + ARRAY_SIZE);
        System.out.println("Количество доступных процессоров: " + THREAD_COUNT);
        System.out.println("=====================================\n");
        
        // Генерируем массив случайных чисел
        int[] array = generateArray(ARRAY_SIZE);
        System.out.println("Массив сгенерирован. Первые 10 элементов: ");
        for (int i = 0; i < Math.min(10, array.length); i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println("\n");
        
        // 1. Последовательная реализация
        testSequential(array);
        
        // 2. Многопоточная реализация
        testMultithreaded(array);
        
        // 3. ForkJoin реализация
        testForkJoin(array);
        
        // Сравнительная таблица
        printComparison();
    }
    
    private static int[] generateArray(int size) {
        Random random = new Random(42); // Фиксированный seed для воспроизводимости
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(100) + 1; // Числа от 1 до 100
        }
        return array;
    }
    
    private static void testSequential(int[] array) {
        System.out.println("1. ПОСЛЕДОВАТЕЛЬНАЯ РЕАЛИЗАЦИЯ");
        System.out.println("--------------------------------");
        
        // Замер памяти до выполнения
        System.gc();
        long memoryBefore = getUsedMemory();
        
        // Замер времени
        long startTime = System.currentTimeMillis();
        
        // Вычисление суммы
        long sum = SequentialSum.calculate(array);
        
        long endTime = System.currentTimeMillis();
        long memoryAfter = getUsedMemory();
        
        // Вывод результатов
        long executionTime = endTime - startTime;
        long memoryUsed = (memoryAfter - memoryBefore) / 1024;
        
        System.out.println("Результат: " + sum);
        System.out.println("Время выполнения: " + executionTime + " мс");
        System.out.println("Использовано памяти: ~" + memoryUsed + " КБ");
        System.out.println();
    }
    
    private static void testMultithreaded(int[] array) {
        System.out.println("2. МНОГОПОТОЧНАЯ РЕАЛИЗАЦИЯ");
        System.out.println("--------------------------------");
        System.out.println("Используется потоков: " + THREAD_COUNT);
        
        // Замер памяти до выполнения
        System.gc();
        long memoryBefore = getUsedMemory();
        
        // Замер времени
        long startTime = System.currentTimeMillis();
        
        // Вычисление суммы
        long sum = 0;
        try {
            sum = MultithreadedSum.calculate(array, THREAD_COUNT);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
        
        long endTime = System.currentTimeMillis();
        long memoryAfter = getUsedMemory();
        
        // Вывод результатов
        long executionTime = endTime - startTime;
        long memoryUsed = (memoryAfter - memoryBefore) / 1024;
        
        System.out.println("Результат: " + sum);
        System.out.println("Время выполнения: " + executionTime + " мс");
        System.out.println("Использовано памяти: ~" + memoryUsed + " КБ");
        System.out.println("Ускорение относительно последовательной версии: " + 
                          String.format("%.2f", getSpeedup(executionTime)));
        System.out.println();
    }
    
    private static void testForkJoin(int[] array) {
        System.out.println("3. FORK/JOIN РЕАЛИЗАЦИЯ");
        System.out.println("--------------------------------");
        
        // Замер памяти до выполнения
        System.gc();
        long memoryBefore = getUsedMemory();
        
        // Замер времени
        long startTime = System.currentTimeMillis();
        
        // Вычисление суммы
        long sum = ForkJoinSum.calculate(array);
        
        long endTime = System.currentTimeMillis();
        long memoryAfter = getUsedMemory();
        
        // Вывод результатов
        long executionTime = endTime - startTime;
        long memoryUsed = (memoryAfter - memoryBefore) / 1024;
        
        System.out.println("Результат: " + sum);
        System.out.println("Время выполнения: " + executionTime + " мс");
        System.out.println("Использовано памяти: ~" + memoryUsed + " КБ");
        System.out.println("Ускорение относительно последовательной версии: " + 
                          String.format("%.2f", getSpeedup(executionTime)));
        System.out.println();
    }
    
    private static long sequentialTime = 0;
    
    private static double getSpeedup(long currentTime) {
        if (sequentialTime == 0) {
            sequentialTime = currentTime;
            return 1.0;
        }
        return (double) sequentialTime / currentTime;
    }
    
    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    private static void printComparison() {
        System.out.println("=====================================");
        System.out.println("СРАВНИТЕЛЬНЫЙ АНАЛИЗ");
        System.out.println("=====================================");
        System.out.println("Последовательная версия использована как базовая (100%)");
        System.out.println("Многопоточная и ForkJoin версии показывают ускорение");
        System.out.println("относительно последовательной реализации.");
        System.out.println("\nВЫВОДЫ:");
        System.out.println("- Из-за задержки Thread.sleep(1) параллельные версии");
        System.out.println("  должны показать значительное ускорение");
        System.out.println("- ForkJoin обычно эффективнее для рекурсивных задач");
        System.out.println("- Многопоточная версия с пулом потоков хороша для");
        System.out.println("  простого разделения работы");
    }
}
