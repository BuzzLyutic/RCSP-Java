package main.java.task1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MultithreadedSum {
    
    public static long calculate(int[] array, int threadCount) 
            throws InterruptedException, ExecutionException {
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Long>> futures = new ArrayList<>();
        
        int chunkSize = array.length / threadCount;
        int remainder = array.length % threadCount;
        
        System.out.println("  Размер части для каждого потока: ~" + chunkSize);
        
        int startIndex = 0;
        for (int i = 0; i < threadCount; i++) {
            int endIndex = startIndex + chunkSize;
            
            if (i == threadCount - 1) {
                endIndex += remainder;
            }
            
            SumTask task = new SumTask(array, startIndex, endIndex, i + 1);
            futures.add(executor.submit(task));
            
            startIndex = endIndex;
        }
        
        long totalSum = 0;
        for (int i = 0; i < futures.size(); i++) {
            long partialSum = futures.get(i).get();
            totalSum += partialSum;
            System.out.println("  Поток " + (i + 1) + " вернул сумму: " + partialSum);
        }
        
        executor.shutdown();
        
        return totalSum;
    }
    
    static class SumTask implements Callable<Long> {
        private final int[] array;
        private final int startIndex;
        private final int endIndex;
        private final int taskId;
        
        public SumTask(int[] array, int startIndex, int endIndex, int taskId) {
            this.array = array;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.taskId = taskId;
        }
        
        @Override
        public Long call() {
            long sum = 0;
            
            System.out.println("  Поток " + taskId + " начал работу с элементами " + 
                              startIndex + "-" + (endIndex - 1));
            
            for (int i = startIndex; i < endIndex; i++) {
                sum += array[i];
                simulateWork();
            }
            
            System.out.println("  Поток " + taskId + " завершил работу");
            
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
