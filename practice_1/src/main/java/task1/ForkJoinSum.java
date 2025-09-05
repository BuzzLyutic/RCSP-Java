package main.java.task1;

// src/main/java/ForkJoinSum.java
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinSum {
    
    // Пороговое значение для рекурсивного деления
    private static final int THRESHOLD = 1000;
    
    public static long calculate(int[] array) {
        // Создаем ForkJoin пул
        ForkJoinPool pool = new ForkJoinPool();
        
        System.out.println("  Пороговое значение для деления: " + THRESHOLD);
        System.out.println("  Количество потоков в пуле: " + pool.getParallelism());
        
        // Создаем корневую задачу
        SumTask rootTask = new SumTask(array, 0, array.length, 0);
        
        // Запускаем вычисление и ждем результат
        long result = pool.invoke(rootTask);
        
        // Завершаем работу пула
        pool.shutdown();
        
        System.out.println("  Всего создано подзадач: " + SumTask.taskCount);
        
        return result;
    }
    
    // Рекурсивная задача для вычисления суммы
    static class SumTask extends RecursiveTask<Long> {
        private final int[] array;
        private final int start;
        private final int end;
        private final int depth;
        
        // Счетчик созданных задач
        static int taskCount = 0;
        
        public SumTask(int[] array, int start, int end, int depth) {
            this.array = array;
            this.start = start;
            this.end = end;
            this.depth = depth;
            taskCount++;
        }
        
        @Override
        protected Long compute() {
            int length = end - start;
            
            // Если размер части меньше порога, вычисляем напрямую
            if (length <= THRESHOLD) {
                return computeDirectly();
            }
            
            // Иначе делим задачу на две части
            int mid = start + length / 2;
            
            // Создаем подзадачи
            SumTask leftTask = new SumTask(array, start, mid, depth + 1);
            SumTask rightTask = new SumTask(array, mid, end, depth + 1);
            
            // Запускаем левую задачу асинхронно
            leftTask.fork();
            
            // Вычисляем правую задачу в текущем потоке
            long rightResult = rightTask.compute();
            
            // Ждем результат левой задачи
            long leftResult = leftTask.join();
            
            // Возвращаем сумму результатов
            return leftResult + rightResult;
        }
        
        private long computeDirectly() {
            long sum = 0;
            
            // Выводим информацию только для первых нескольких задач
            if (taskCount <= 10) {
                System.out.println("  Задача обрабатывает элементы " + 
                                 start + "-" + (end - 1) + 
                                 " (глубина: " + depth + ")");
            }
            
            for (int i = start; i < end; i++) {
                // Выполняем сложение
                sum += array[i];
                
                // Добавляем задержку после операции
                simulateWork();
            }
            
            return sum;
        }
        
        private void simulateWork() {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
