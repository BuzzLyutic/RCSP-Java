FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Копируем jar файл
COPY target/*.jar app.jar

# Создаем директорию для хранения файлов
RUN mkdir -p /app/storage

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]