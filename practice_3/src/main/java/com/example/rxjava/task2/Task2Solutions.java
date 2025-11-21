package com.example.rxjava.task2;

import io.reactivex.rxjava3.core.Observable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Task2Solutions {

    public static void runAll() {
        System.out.println("2: ОПЕРАЦИИ С ПОТОКАМИ");

        task212();
        System.out.println("\n" + "=".repeat(60) + "\n");

        task222();
        System.out.println("\n" + "=".repeat(60) + "\n");

        task232();
    }

    // Задание 2.1.2: Фильтрация чисел больше 500
    private static void task212() {
        System.out.println("ЗАДАНИЕ 2.1.2: Числа больше 500");
        System.out.println("Генерируем 1000 случайных чисел от 0 до 1000");
        System.out.println("и оставляем только числа > 500\n");

        AtomicInteger counter = new AtomicInteger(0);
        AtomicInteger filteredCounter = new AtomicInteger(0);

        Observable
                .create(emitter -> {
                    Random random = new Random();
                    for (int i = 0; i < 1000; i++) {
                        emitter.onNext(random.nextInt(1001)); // от 0 до 1000
                    }
                    emitter.onComplete();
                })
                .doOnNext(num -> counter.incrementAndGet())
                .filter(num -> (Integer)num > 500)
                .doOnNext(num -> filteredCounter.incrementAndGet())
                .take(10) // Показываем первые 10 для наглядности
                .subscribe(
                        number -> System.out.println("  Число > 500: " + number),
                        error -> System.err.println("Ошибка: " + error),
                        () -> {
                            System.out.println("\nСтатистика:");
                            System.out.println("  • Всего чисел обработано: " + counter.get());
                            System.out.println("  • Чисел больше 500: " + filteredCounter.get());
                            System.out.println("  • Процент отфильтрованных: " +
                                    String.format("%.1f%%", (filteredCounter.get() * 100.0 / counter.get())));
                        }
                );
    }

    // Задание 2.2.2: Последовательное объединение двух потоков
    private static void task222() {
        System.out.println("ЗАДАНИЕ 2.2.2: Последовательное объединение потоков");
        System.out.println("Создаем два потока по 1000 случайных цифр (0-9)");
        System.out.println("и объединяем их последовательно\n");

        Random random = new Random();

        // Первый поток: 1000 случайных цифр
        Observable<Integer> stream1 = Observable
                .create(emitter -> {
                    for (int i = 0; i < 1000; i++) {
                        emitter.onNext(random.nextInt(10)); // цифры 0-9
                    }
                    emitter.onComplete();
                });

        // Второй поток: 1000 случайных цифр
        Observable<Integer> stream2 = Observable
                .create(emitter -> {
                    for (int i = 0; i < 1000; i++) {
                        emitter.onNext(random.nextInt(10)); // цифры 0-9
                    }
                    emitter.onComplete();
                });

        AtomicInteger totalCount = new AtomicInteger(0);
        AtomicInteger fromStream1 = new AtomicInteger(0);
        AtomicInteger fromStream2 = new AtomicInteger(0);

        // Используем concat для последовательного объединения
        Observable
                .concat(
                        stream1.doOnNext(x -> fromStream1.incrementAndGet()),
                        stream2.doOnNext(x -> fromStream2.incrementAndGet())
                )
                .doOnNext(x -> totalCount.incrementAndGet())
                .take(20) // Показываем первые 20 элементов для демонстрации
                .toList()
                .subscribe(
                        list -> {
                            System.out.println("Первые 20 элементов объединенного потока:");
                            System.out.println("  " + list);
                            System.out.println("\nСтатистика объединения:");
                            System.out.println("  • Элементов из потока 1: " + fromStream1.get());
                            System.out.println("  • Элементов из потока 2: " + fromStream2.get());
                            System.out.println("  • Всего элементов: " + (fromStream1.get() + fromStream2.get()));
                            System.out.println("  Потоки объединены последовательно!");
                        },
                        error -> System.err.println("Ошибка: " + error)
                );
    }

    // Задание 2.3.2: Взять первые 5 элементов из потока
    private static void task232() {
        System.out.println("ЗАДАНИЕ 2.3.2: Первые 5 элементов потока");
        System.out.println("Генерируем поток из 10 случайных чисел");
        System.out.println("и берем только первые 5\n");

        Random random = new Random();
        AtomicInteger generatedCount = new AtomicInteger(0);

        System.out.println("Исходный поток (10 чисел):");
        Integer[] allNumbers = new Integer[10];
        for (int i = 0; i < 10; i++) {
            allNumbers[i] = random.nextInt(100);
            System.out.println("  " + (i + 1) + ". " + allNumbers[i]);
        }

        System.out.println("\nПервые 5 элементов:");
        Observable
                .fromArray(allNumbers)
                .doOnNext(x -> generatedCount.incrementAndGet())
                .take(5) // Берем только первые 5 элементов
                .toList()
                .subscribe(
                        list -> {
                            for (int i = 0; i < list.size(); i++) {
                                System.out.println("  " + (i + 1) + ". " + list.get(i) + " ");
                            }
                            System.out.println("\nРезультат:");
                            System.out.println("  Всего чисел в потоке: 10");
                            System.out.println("  Взято первых элементов: " + list.size());
                            System.out.println("  Пропущено элементов: " + (10 - list.size()));
                        },
                        error -> System.err.println("Ошибка: " + error)
                );
    }
}
