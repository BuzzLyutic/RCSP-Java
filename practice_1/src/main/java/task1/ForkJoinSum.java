package main.java.task1;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinSum {
    
    private static final int THRESHOLD = 1000;
    
    public static long calculate(int[] array) {
        ForkJoinPool pool = new ForkJoinPool();
        
        System.out.println("  Пороговое значение для деления: " + THRESHOLD);
        System.out.println("  Количество потоков в пуле: " + pool.getParallelism());
        
        SumTask rootTask = new SumTask(array, 0, array.length, 0);
        
        long result = pool.invoke(rootTask);
        
        pool.shutdown();
        
        System.out.println("  Всего создано подзадач: " + SumTask.taskCount);
        
        return result;
    }
    
    static class SumTask extends RecursiveTask<Long> {
        private final int[] array;
        private final int start;
        private final int end;
        private final int depth;
        
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
            
            if (length <= THRESHOLD) {
                return computeDirectly();
            }
            
            int mid = start + length / 2;
            
            SumTask leftTask = new SumTask(array, start, mid, depth + 1);
            SumTask rightTask = new SumTask(array, mid, end, depth + 1);
            
            leftTask.fork();
            
            long rightResult = rightTask.compute();
            
            long leftResult = leftTask.join();
            
            return leftResult + rightResult;
        }
        
        private long computeDirectly() {
            long sum = 0;
            
            if (taskCount <= 10) {
                System.out.println("  Задача обрабатывает элементы " + 
                                 start + "-" + (end - 1) + 
                                 " (глубина: " + depth + ")");
            }
            
            for (int i = start; i < end; i++) {
                sum += array[i];         
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
