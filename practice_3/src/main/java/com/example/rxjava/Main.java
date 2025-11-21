package com.example.rxjava;

import com.example.rxjava.task1.SensorSystem;
import com.example.rxjava.task2.Task2Solutions;
import com.example.rxjava.task3.UserFriendSystem;
import com.example.rxjava.task4.FileProcessingSystem;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        System.out.println("\n1: СИСТЕМА ДАТЧИКОВ");
                        SensorSystem.run();
                        break;
                    case "2":
                        System.out.println("\n2: ОПЕРАЦИИ С ПОТОКАМИ");
                        Task2Solutions.runAll();
                        break;
                    case "3":
                        System.out.println("\n3: USER FRIEND");
                        UserFriendSystem.run();
                        break;
                    case "4":
                        System.out.println("\n4: ОБРАБОТКА ФАЙЛОВ");
                        FileProcessingSystem.run();
                        break;
                    case "0":
                        System.out.println("Выход из программы...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Неверный выбор!");
                }
            } catch (Exception e) {
                System.err.println("Ошибка при выполнении: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("\nНажмите Enter для продолжения...");
            scanner.nextLine();
        }
    }

    private static void printMenu() {
        System.out.println("\nRxJava ЗАДАНИЯ");
        System.out.println("1. Задание 1: Система датчиков с сигнализацией");
        System.out.println("2. Задание 2: Все операции с потоками (2.1.2, 2.2.2, 2.3.2)");
        System.out.println("3. Задание 3: UserFriend");
        System.out.println("4. Задание 4: Система обработки файлов");
        System.out.println("0. Выход");
        System.out.print("Выберите задание: ");
    }
}
