package com.example.rxjava.task3;

import io.reactivex.rxjava3.core.Observable;
import java.util.*;

public class UserFriendSystem {

    // Класс UserFriend
    static class UserFriend {
        int userId;
        int friendId;

        public UserFriend(int userId, int friendId) {
            this.userId = userId;
            this.friendId = friendId;
        }

        @Override
        public String toString() {
            return String.format("UserFriend{userId=%d, friendId=%d}", userId, friendId);
        }
    }

    // Массив объектов UserFriend
    private static UserFriend[] userFriends;

    // Инициализация массива случайными данными
    static {
        Random random = new Random();
        userFriends = new UserFriend[150];

        for (int i = 0; i < userFriends.length; i++) {
            userFriends[i] = new UserFriend(
                    random.nextInt(10) + 1,
                    random.nextInt(100) + 1
            );
        }
    }

    // Функция получения друзей по userId
    public static Observable<UserFriend> getFriends(int userId) {
        return Observable
                .fromArray(userFriends)
                .filter(uf -> uf.userId == userId);
    }

    public static void run() {
        System.out.println("ЗАДАНИЕ 3: USER FRIEND SYSTEM");

        // Создаем массив случайных userId
        Random random = new Random();
        Integer[] userIds = new Integer[5];

        System.out.println("Сгенерированные userId для поиска:");
        for (int i = 0; i < userIds.length; i++) {
            userIds[i] = random.nextInt(10) + 1;
            System.out.println("  • User #" + (i + 1) + ": userId = " + userIds[i]);
        }

        System.out.println("\nБаза данных содержит " + userFriends.length + " записей UserFriend");
        System.out.println("\nПоиск друзей для каждого userId:\n");

        Map<Integer, List<Integer>> friendsMap = new HashMap<>();

        // Преобразуем поток userId в поток UserFriend через функцию getFriends
        Observable
                .fromArray(userIds)
                .flatMap(userId -> {
                    System.out.println("─".repeat(50));
                    System.out.println("Обработка userId = " + userId + ":");

                    return getFriends(userId)
                            .doOnNext(uf -> {
                                friendsMap.computeIfAbsent(uf.userId, k -> new ArrayList<>())
                                        .add(uf.friendId);
                            });
                })
                .toList()
                .subscribe(
                        userFriendList -> {
                            // Выводим результаты
                            System.out.println("\n" + "═".repeat(50));
                            System.out.println("ИТОГОВАЯ СТАТИСТИКА:");
                            System.out.println("═".repeat(50));

                            for (Integer userId : userIds) {
                                List<Integer> friends = friendsMap.getOrDefault(userId, new ArrayList<>());
                                System.out.println("\nUserId " + userId + ":");
                                if (friends.isEmpty()) {
                                    System.out.println("  Друзей не найдено");
                                } else {
                                    System.out.println("  Найдено друзей: " + friends.size());
                                    System.out.print("  📱 ID друзей: ");
                                    // Показываем первые 10 друзей
                                    int showCount = Math.min(friends.size(), 10);
                                    for (int i = 0; i < showCount; i++) {
                                        System.out.print(friends.get(i));
                                        if (i < showCount - 1) System.out.print(", ");
                                    }
                                    if (friends.size() > 10) {
                                        System.out.print(" ... и еще " + (friends.size() - 10));
                                    }
                                    System.out.println();
                                }
                            }

                            System.out.println("\n" + "═".repeat(50));
                            System.out.println("Всего обработано записей: " + userFriendList.size());
                        },
                        error -> System.err.println("Ошибка: " + error)
                );
    }
}
