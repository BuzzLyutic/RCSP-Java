#!/bin/bash

# Цвета для вывода
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Функция для красивого вывода
print_step() {
    echo -e "\n${BLUE}===================================${NC}"
    echo -e "${GREEN}$1${NC}"
    echo -e "${BLUE}===================================${NC}"
}

print_command() {
    echo -e "${YELLOW}> $1${NC}"
}

print_error() {
    echo -e "${RED}ERROR: $1${NC}"
}

# Функция для создания тестовых файлов
create_test_files() {
    print_step "Создание тестовых файлов"

    if [ ! -f "test.txt" ]; then
        echo "Test file content - Hello DFS!" > test.txt
        echo "✓ Создан test.txt"
    fi

    if [ ! -f "test2.txt" ]; then
        echo "Second test file - Testing node failure" > test2.txt
        echo "✓ Создан test2.txt"
    fi

    if [ ! -f "test3.txt" ]; then
        echo "Third test file - Testing recovery" > test3.txt
        echo "✓ Создан test3.txt"
    fi

    if [ ! -f "image.jpg" ]; then
        # Создаем пустой файл для теста (или можете использовать реальное изображение)
        dd if=/dev/urandom of=image.jpg bs=1024 count=100 2>/dev/null
        echo "✓ Создан image.jpg"
    fi

    if [ ! -f "document.pdf" ]; then
        dd if=/dev/urandom of=document.pdf bs=1024 count=50 2>/dev/null
        echo "✓ Создан document.pdf"
    fi

    if [ ! -f "archive.zip" ]; then
        dd if=/dev/urandom of=archive.zip bs=1024 count=75 2>/dev/null
        echo "✓ Создан archive.zip"
    fi
}

# Пауза между операциями
pause() {
    sleep ${1:-1}
}

# Основные тесты
run_tests() {

    create_test_files

    # Тест 1: Загрузка файла
    print_step "Тест 1: Загрузка файла на узел 1"
    print_command "curl -X POST http://localhost:8081/api/files/upload -F 'file=@test.txt'"
    curl -X POST http://localhost:8081/api/files/upload -F "file=@test.txt"
    pause 2

    # Тест 2: Проверка репликации на всех узлах
    print_step "Тест 2: Проверка списка файлов на всех узлах"

    for port in 8081 8082 8083 8084; do
        print_command "curl http://localhost:$port/api/files"
        curl http://localhost:$port/api/files
        echo ""
        pause 1
    done

    # Тест 3: Скачивание файлов с разных узлов
    print_step "Тест 3: Скачивание файлов с разных узлов"

    print_command "curl http://localhost:8081/api/files/download/test.txt -o downloaded1.txt"
    curl http://localhost:8081/api/files/download/test.txt -o downloaded1.txt
    pause 1

    print_command "curl http://localhost:8082/api/files/download/test.txt -o downloaded2.txt"
    curl http://localhost:8082/api/files/download/test.txt -o downloaded2.txt
    pause 1

    print_command "curl http://localhost:8083/api/files/download/test.txt -o downloaded3.txt"
    curl http://localhost:8083/api/files/download/test.txt -o downloaded3.txt
    pause 1

    print_command "cat downloaded3.txt"
    cat downloaded3.txt
    echo ""

    # Тест 4: Отказоустойчивость - остановка узла 1
    print_step "Тест 4: Остановка узла 1 (dfs-node1)"
    print_command "docker stop dfs-node1"
    docker stop dfs-node1
    pause 3
    echo "✓ Узел 1 остановлен"

    print_step "Тест 5: Загрузка файла после отказа узла 1"
    print_command "curl -X POST http://localhost:8082/api/files/upload -F 'file=@test2.txt'"
    curl -X POST http://localhost:8082/api/files/upload -F "file=@test2.txt"
    pause 2

    print_step "Тест 6: Скачивание старого файла с работающего узла"
    print_command "curl http://localhost:8083/api/files/download/test.txt -o test_after_failure.txt"
    curl http://localhost:8083/api/files/download/test.txt -o test_after_failure.txt
    pause 1

    # Тест 7: Остановка второго узла
    print_step "Тест 7: Остановка узла 2 (dfs-node2)"
    print_command "docker stop dfs-node2"
    docker stop dfs-node2
    pause 3
    echo "✓ Узел 2 остановлен"

    print_step "Тест 8: Проверка информации об узле 3"
    print_command "curl http://localhost:8083/api/info"
    curl http://localhost:8083/api/info
    echo ""
    pause 1

    print_step "Тест 9: Загрузка файла при двух отказавших узлах"
    print_command "curl -X POST http://localhost:8083/api/files/upload -F 'file=@test3.txt'"
    curl -X POST http://localhost:8083/api/files/upload -F "file=@test3.txt"
    pause 2

    print_step "Тест 10: Скачивание файла с узла 4"
    print_command "curl http://localhost:8084/api/files/download/test3.txt -o test3_downloaded.txt"
    curl http://localhost:8084/api/files/download/test3.txt -o test3_downloaded.txt
    pause 1

    # Тест 11: Восстановление узлов
    print_step "Тест 11: Запуск остановленных узлов"
    print_command "docker start dfs-node1"
    docker start dfs-node1
    pause 2

    print_command "docker start dfs-node2"
    docker start dfs-node2
    pause 3
    echo "✓ Узлы 1 и 2 запущены, ожидаем синхронизации..."
    pause 5

    print_step "Тест 12: Проверка информации после восстановления"
    print_command "curl http://localhost:8081/api/info"
    curl http://localhost:8081/api/info
    echo ""
    pause 1

    # Тест 13: Загрузка различных типов файлов
    print_step "Тест 13: Загрузка файлов различных типов"

    print_command "curl -X POST http://localhost:8081/api/files/upload -F 'file=@image.jpg'"
    curl -X POST http://localhost:8081/api/files/upload -F "file=@image.jpg"
    pause 2

    print_command "curl -X POST http://localhost:8082/api/files/upload -F 'file=@document.pdf'"
    curl -X POST http://localhost:8082/api/files/upload -F "file=@document.pdf"
    pause 2

    print_command "curl -X POST http://localhost:8083/api/files/upload -F 'file=@archive.zip'"
    curl -X POST http://localhost:8083/api/files/upload -F "file=@archive.zip"
    pause 2

    # Финальная проверка
    print_step "Тест 14: Финальная проверка списка всех файлов"
    print_command "curl http://localhost:8081/api/files"
    curl http://localhost:8081/api/files
    echo ""

    # Итоги
    print_step "ТЕСТИРОВАНИЕ ЗАВЕРШЕНО!"
    echo -e "${GREEN}Все тесты выполнены успешно!${NC}"
    echo ""
    echo "Созданные файлы для проверки:"
    ls -lh downloaded*.txt test*_downloaded.txt 2>/dev/null
}

# Запуск тестов
clear
echo -e "${GREEN}"
echo "╔═══════════════════════════════════════════════╗"
echo "║   DFS Автоматическое тестирование             ║"
echo "║   Distributed File System Test Suite          ║"
echo "╚═══════════════════════════════════════════════╝"
echo -e "${NC}"

run_tests
