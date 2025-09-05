#!/bin/bash

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

clear

echo -e "${CYAN}╔══════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║   МНОГОПОТОЧНОЕ ПРОГРАММИРОВАНИЕ В JAVA  ║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════╝${NC}"
echo

# Создание директории для скомпилированных файлов
mkdir -p out

# Компиляция всех заданий
echo -e "${YELLOW}Компиляция исходного кода...${NC}"

# Компиляция задания 1
if [ -d "src/main/java/task1" ]; then
    javac -d out src/main/java/task1/*.java 2>/dev/null
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓${NC} Задание 1 скомпилировано"
    else
        echo -e "${RED}✗${NC} Ошибка компиляции Задания 1"
    fi
fi

# Компиляция задания 2
if [ -f "src/main/java/task2/Task2Main.java" ]; then
    javac -d out src/main/java/task2/*.java 2>/dev/null
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓${NC} Задание 2 скомпилировано"
    else
        echo -e "${RED}✗${NC} Ошибка компиляции Задания 2"
    fi
fi

# Компиляция задания 3
if [ -d "src/main/java/task3" ]; then
    javac -d out src/main/java/task3/*.java 2>/dev/null
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓${NC} Задание 3 скомпилировано"
    else
        echo -e "${RED}✗${NC} Ошибка компиляции Задания 3"
    fi
fi

echo
echo -e "${BLUE}════════════════════════════════════════════${NC}"
echo

# Меню выбора задания
while true; do
    echo -e "${MAGENTA}Выберите задание для запуска:${NC}"
    echo "  1 - Поиск суммы элементов массива"
    echo "  2 - Асинхронное возведение в квадрат"
    echo "  3 - Система обработки файлов"
    echo "  0 - Выход"
    echo
    read -p "Ваш выбор: " choice
    
    case $choice in
        1)
            echo
            echo -e "${CYAN}Запуск Задания 1...${NC}"
            echo "════════════════════════════════════════════"
            java -cp out main.java.task1.Main
            echo
            echo -e "${BLUE}════════════════════════════════════════════${NC}"
            echo
            ;;
        2)
            echo
            echo -e "${CYAN}Запуск Задания 2...${NC}"
            echo "════════════════════════════════════════════"
            java -cp out main.java.task2.Task2Main
            echo
            echo -e "${BLUE}════════════════════════════════════════════${NC}"
            echo
            ;;
        3)
            echo
            echo -e "${CYAN}Запуск Задания 3...${NC}"
            echo "════════════════════════════════════════════"
            java -cp out main.java.task3.Task3Main
            echo
            echo -e "${BLUE}════════════════════════════════════════════${NC}"
            echo
            ;;
        0)
            echo
            echo -e "${GREEN}Программа завершена.${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}Неверный выбор. Попробуйте снова.${NC}"
            echo
            ;;
    esac
done
