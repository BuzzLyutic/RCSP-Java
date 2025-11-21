package com.example.rxjava.task1;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorSystem {
    private static final CompositeDisposable disposables = new CompositeDisposable();
    private static final int TEMP_NORM = 25;
    private static final int CO2_NORM = 70;

    // Класс для хранения данных датчика
    static class SensorData {
        String type;
        int value;
        long timestamp;

        public SensorData(String type, int value) {
            this.type = type;
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("%s: %d", type, value);
        }
    }

    public static void run() throws InterruptedException {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   СИСТЕМА МОНИТОРИНГА ДАТЧИКОВ        ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Нормальные показатели:                 ║");
        System.out.println("║ • Температура: ≤ 25°C                  ║");
        System.out.println("║ • CO2: ≤ 70 ppm                        ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Мониторинг в течение 15 секунд...      ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        Random random = new Random();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        // Датчик температуры - публикует данные каждую секунду
        Observable<SensorData> temperatureSensor = Observable
                .interval(1, TimeUnit.SECONDS)
                .map(tick -> {
                    int temp = random.nextInt(16) + 15;
                    return new SensorData("Temperature", temp);
                })
                .subscribeOn(Schedulers.io())
                .share();

        Observable<SensorData> co2Sensor = Observable
                .interval(1, TimeUnit.SECONDS)
                .map(tick -> {
                    int co2 = random.nextInt(71) + 30;
                    return new SensorData("CO2", co2);
                })
                .subscribeOn(Schedulers.io())
                .share();

        disposables.add(
                Observable
                        .combineLatest(
                                temperatureSensor,
                                co2Sensor,
                                (temp, co2) -> {
                                    String time = sdf.format(new Date());

                                    // Выводим текущие показания
                                    System.out.println(String.format(
                                            "[%s] Показания: Температура=%d°C | CO2=%d ppm",
                                            time, temp.value, co2.value
                                    ));

                                    // Проверяем превышение норм
                                    boolean tempExceeded = temp.value > TEMP_NORM;
                                    boolean co2Exceeded = co2.value > CO2_NORM;

                                    // Формируем сообщение в зависимости от ситуации
                                    if (tempExceeded && co2Exceeded) {
                                        return String.format(
                                                "[%s] ALARM!!! ОБА ПОКАЗАТЕЛЯ КРИТИЧЕСКИЕ! " +
                                                        "Температура=%d°C (норма ≤%d), CO2=%d ppm (норма ≤%d)",
                                                time, temp.value, TEMP_NORM, co2.value, CO2_NORM
                                        );
                                    } else if (tempExceeded) {
                                        return String.format(
                                                "[%s] ВНИМАНИЕ: Превышена температура! %d°C (норма ≤%d)",
                                                time, temp.value, TEMP_NORM
                                        );
                                    } else if (co2Exceeded) {
                                        return String.format(
                                                "[%s] ВНИМАНИЕ: Превышен уровень CO2! %d ppm (норма ≤%d)",
                                                time, co2.value, CO2_NORM
                                        );
                                    } else {
                                        return String.format(
                                                "[%s] Все показатели в норме",
                                                time
                                        );
                                    }
                                }
                        )
                        .subscribe(
                                alert -> {
                                    if (alert.contains("ALARM")) {
                                        System.err.println(alert);
                                    } else if (alert.contains("ВНИМАНИЕ")) {
                                        System.out.println(alert);
                                    } else {
                                        System.out.println(alert);
                                    }
                                    System.out.println("─".repeat(60));
                                },
                                error -> System.err.println("Ошибка: " + error),
                                () -> System.out.println("Мониторинг завершен")
                        )
        );

        Thread.sleep(15000);

        disposables.dispose();
        System.out.println("\nСистема мониторинга остановлена.");
    }
}