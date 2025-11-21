package com.example.rxjava.task4;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.processors.PublishProcessor;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

public class FileProcessingSystem {

    private static final CompositeDisposable disposables = new CompositeDisposable();
    private static final int QUEUE_SIZE = 5;
    private static final AtomicInteger fileIdGenerator = new AtomicInteger(1);

    // Класс File
    static class File {
        int id;
        String type;
        int size;
        long createdAt;

        public File(String type, int size) {
            this.id = fileIdGenerator.getAndIncrement();
            this.type = type;
            this.size = size;
            this.createdAt = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("File#%d[%s, %dKB]", id, type, size);
        }
    }

    // Класс обработчика файлов
    static class FileProcessor {
        String supportedType;
        String name;
        AtomicInteger processedCount = new AtomicInteger(0);

        public FileProcessor(String supportedType) {
            this.supportedType = supportedType;
            this.name = supportedType + " Processor";
        }

        public Flowable<File> process(File file) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            long processingTime = file.size * 7;

            return Flowable.just(file)
                    .delay(processingTime, TimeUnit.MILLISECONDS, Schedulers.io())
                    .doOnNext(processedFile -> {
                        processedCount.incrementAndGet();
                        String endTime = sdf.format(new Date());
                        System.out.println("[" + endTime + "] " + name + " завершил " + processedFile + " за " + processingTime + "мс");
                    })
                    .doOnSubscribe(subscription -> {
                        String time = sdf.format(new Date());
                        System.out.println("[" + time + "] " + name + " начал обработку " + file);
                    });
        }
    }

    public static void run() throws InterruptedException {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   ЗАДАНИЕ 4: СИСТЕМА ОБРАБОТКИ ФАЙЛОВ  ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ • Типы файлов: XML, JSON, XLS          ║");
        System.out.println("║ • Размер файлов: 10-100 KB             ║");
        System.out.println("║ • Размер очереди: 5 файлов             ║");
        System.out.println("║ • Время обработки: размер * 7 мс       ║");
        System.out.println("║ • Время работы: 20 секунд              ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        Random random = new Random();
        String[] fileTypes = {"XML", "JSON", "XLS"};
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        // Счетчики
        AtomicInteger generatedFiles = new AtomicInteger(0);
        AtomicInteger queuedFiles = new AtomicInteger(0);
        AtomicInteger rejectedFiles = new AtomicInteger(0);

        // Создаем обработчики для каждого типа файлов
        FileProcessor xmlProcessor = new FileProcessor("XML");
        FileProcessor jsonProcessor = new FileProcessor("JSON");
        FileProcessor xlsProcessor = new FileProcessor("XLS");

        // Используем PublishProcessor как общую очередь
        PublishProcessor<File> fileQueue = PublishProcessor.create();

        // Преобразуем в Flowable с буфером для обработки backpressure
        Flowable<File> queueWithBackpressure = fileQueue
                .onBackpressureBuffer(
                        QUEUE_SIZE,
                        () -> {
                            rejectedFiles.incrementAndGet();
                            String time = sdf.format(new Date());
                            System.err.println("[" + time + "]  Очередь переполнена! Файл отклонен");
                        },
                        io.reactivex.rxjava3.core.BackpressureOverflowStrategy.DROP_OLDEST
                );

        // XML Processor подписка
        disposables.add(
                queueWithBackpressure
                        .filter(f -> f.type.equals("XML"))
                        .flatMap(xmlProcessor::process, false, 1)
                        .subscribe(
                                processedFile -> {},
                                error -> System.err.println("Ошибка в XML Processor: " + error)
                        )
        );

        // JSON Processor подписка
        disposables.add(
                queueWithBackpressure
                        .filter(f -> f.type.equals("JSON"))
                        .flatMap(jsonProcessor::process, false, 1)
                        .subscribe(
                                processedFile -> {},
                                error -> System.err.println("Ошибка в JSON Processor: " + error)
                        )
        );

        // XLS Processor подписка
        disposables.add(
                queueWithBackpressure
                        .filter(f -> f.type.equals("XLS"))
                        .flatMap(xlsProcessor::process, false, 1)
                        .subscribe(
                                processedFile -> {},
                                error -> System.err.println("Ошибка в XLS Processor: " + error)
                        )
        );

        // ИЗМЕНЕНИЕ: Используем Observable вместо Flowable для генератора
        disposables.add(
                Observable
                        .interval(0, 1, TimeUnit.MILLISECONDS)
                        .concatMap(tick -> Observable
                                .just(tick)
                                .delay(random.nextInt(901) + 100, TimeUnit.MILLISECONDS))
                        .map(tick -> {
                            String type = fileTypes[random.nextInt(fileTypes.length)];
                            int size = random.nextInt(91) + 10;
                            File file = new File(type, size);
                            generatedFiles.incrementAndGet();
                            String time = sdf.format(new Date());
                            System.out.println("[" + time + "] Создан " + file);
                            return file;
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                file -> {
                                    // Проверяем, не переполнена ли очередь, прежде чем добавлять
                                    if (!fileQueue.hasComplete() && !fileQueue.hasThrowable()) {
                                        queuedFiles.incrementAndGet();
                                        String time = sdf.format(new Date());
                                        System.out.println("[" + time + "] " + file + " добавлен в очередь");
                                        fileQueue.onNext(file);
                                    }
                                },
                                error -> System.err.println("Ошибка генератора: " + error)
                        )
        );

        // Работаем 20 секунд
        Thread.sleep(20000);

        // Останавливаем систему
        disposables.dispose();

        // Выводим статистику
        System.out.println("\n" + "═".repeat(50));
        System.out.println("СТАТИСТИКА РАБОТЫ СИСТЕМЫ:");
        System.out.println("═".repeat(50));
        System.out.println("Файлов сгенерировано: " + generatedFiles.get());
        System.out.println("Файлов добавлено в очередь: " + queuedFiles.get());
        System.out.println("Файлов отклонено (переполнение): " + rejectedFiles.get());
        System.out.println("\nСтатистика по обработчикам:");
        System.out.println("  XML Processor обработал: " + xmlProcessor.processedCount.get() + " файлов");
        System.out.println("  JSON Processor обработал: " + jsonProcessor.processedCount.get() + " файлов");
        System.out.println("  XLS Processor обработал: " + xlsProcessor.processedCount.get() + " файлов");

        int totalProcessed = xmlProcessor.processedCount.get() +
                jsonProcessor.processedCount.get() +
                xlsProcessor.processedCount.get();
        System.out.println("  Всего обработано: " + totalProcessed + " файлов");

        System.out.println("\nСистема обработки файлов остановлена.");
    }
}
