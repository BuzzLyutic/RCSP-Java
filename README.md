# 🚀 Разработка клиент-серверных приложений

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0+-6DB33F?style=for-the-badge&logo=spring-boot)
![RxJava](https://img.shields.io/badge/RxJava-3.x-B7178C?style=for-the-badge&logo=reactivex&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Solidity](https://img.shields.io/badge/Solidity-0.8.0+-363636?style=for-the-badge&logo=solidity)

<div align="center">

### Практические работы по разработке современных клиент-серверных систем

*От реактивного программирования до блокчейн-технологий*

[МИРЭА - Российский Технологический Университет] | [ИКБО-16-22] | [2025]

</div>

---

## Содержание курса

Репозиторий содержит практические работы по дисциплине **"Разработка клиент-серверных приложений"**, охватывающие современные подходы и технологии разработки:

- 🌊 **Реактивное программирование**
- 🔌 **Протоколы реального времени**
- 🗄️ **Распределенные системы**
- ⛓️ **Блокчейн и смарт-контракты**

---

## Практические работы

### [📘 ПР №3: Реактивное программирование с RxJava](./practice_3/README.md)

<table>
<tr>
<td width="30%">

**Технологии:**
- RxJava 3.x
- Java 17+
- Reactive Streams

</td>
<td width="70%">

**Темы:**
- ✅ Observable и Observer
- ✅ Операторы трансформации потоков
- ✅ Системы мониторинга датчиков
- ✅ Обработка асинхронных потоков данных

</td>
</tr>
<tr>
<td colspan="2">

**Задания:**
1. 🌡️ Система мониторинга датчиков (температура + CO2)
2. 🔄 Операции с потоками данных (filter, concat, take)
3. 👥 Социальная сеть друзей (flatMap)
4. 📁 Система обработки файлов с очередью

</td>
</tr>
</table>

---

### [📗 ПР №4: Клиент-серверная система на RSocket](./practice_4/README.md)

<table>
<tr>
<td width="30%">

**Технологии:**
- RSocket Protocol
- Spring Boot 3.0+
- Project Reactor
- R2DBC

</td>
<td width="70%">

**Темы:**
- ✅ 4 типа взаимодействия RSocket
- ✅ Реактивные потоки (Flux/Mono)
- ✅ Операторы преобразования
- ✅ Реактивная работа с БД

</td>
</tr>
<tr>
<td colspan="2">

**Модели взаимодействия:**
1. 🔄 **Request-Response** - классический запрос-ответ
2. 📡 **Request-Stream** - запрос с потоком ответов
3. 🚀 **Fire-and-Forget** - отправка без ожидания ответа
4. 🔀 **Channel** - двунаправленный поток данных

</td>
</tr>
</table>

---

### [📕 ПР №5: Распределенная файловая система](./practice_5/README.md)

<table>
<tr>
<td width="30%">

**Технологии:**
- Spring Boot 3.0+
- Docker Compose
- REST API
- Maven

</td>
<td width="70%">

**Темы:**
- ✅ Децентрализованная архитектура
- ✅ Автоматическая репликация данных
- ✅ Отказоустойчивость
- ✅ Docker-контейнеризация

</td>
</tr>
<tr>
<td colspan="2">

**Возможности системы:**
- 📤 Загрузка файлов любых форматов
- 📥 Скачивание с любого узла
- 🔄 Автоматическая репликация на 3 узла
- 💪 Работа при отказе до 2 узлов из 4
- 🐳 Развертывание в Docker

</td>
</tr>
</table>

---

### [📙 ПР №6: Смарт-контракты для торговли ценными бумагами](./practice_6/README.md)

<table>
<tr>
<td width="30%">

**Технологии:**
- Solidity 0.8+
- Ethereum
- Remix IDE
- ERC-20

</td>
<td width="70%">

**Темы:**
- ✅ Смарт-контракты на Solidity
- ✅ Токены стандарта ERC-20
- ✅ Децентрализованная биржа
- ✅ Unit-тестирование контрактов

</td>
</tr>
<tr>
<td colspan="2">

**Функциональность:**
- 💰 Создание ценных бумаг (Security Token)
- 📊 Торговля через децентрализованную биржу
- 📝 Ордера на покупку и продажу
- 🔐 Безопасность на уровне блокчейна

</td>
</tr>
</table>

---

### [📙 ПР №7: Реактивный REST API на Spring WebFlux](./practice_7/space/README.md)

<table>
<tr>
<td width="30%">

**Технологии:**
- Spring WebFlux
- Spring Data R2DBC
- PostgreSQL
- Docker Compose
- Project Reactor

</td>
<td width="70%">

**Темы:**
- ✅ Реактивные типы Mono/Flux
- ✅ Операторы преобразования потоков
- ✅ Backpressure (противодавление)
- ✅ Неблокирующий доступ к БД
- ✅ Unit-тестирование реактивного кода

</td>
</tr>
<tr>
<td colspan="2">

**Реализованный функционал:**
- 🚀 8 REST endpoints для управления космическими миссиями
- 📡 Server-Sent Events (SSE) стриминг с backpressure
- 🗄️ Реактивная работа с PostgreSQL через R2DBC
- ⚡ Операторы: `map`, `flatMap`, `filter`, `sort`, `reduce`, `limitRate`
- 🛡️ Глобальная обработка ошибок
- 🧪 Тесты: StepVerifier + WebTestClient

</td>
</tr>
</table>

---

## 🛠️ Технологический стек

### Backend & Core

```
┌─────────────────────────────────────────────────────────┐
│                     Java Ecosystem                      │
├─────────────────────────────────────────────────────────┤
│  Java 17+  │  Spring Boot 3.0+  │  Maven  │  Lombok    │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                 Reactive Programming                    │
├─────────────────────────────────────────────────────────┤
│  RxJava 3.x  │  Project Reactor  │  RSocket  │  R2DBC  │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                   Infrastructure                        │
├─────────────────────────────────────────────────────────┤
│  Docker  │  Docker Compose  │  REST API  │  WebSocket  │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                      Blockchain                         │
├─────────────────────────────────────────────────────────┤
│  Solidity  │  Ethereum  │  Remix IDE  │  ERC-20        │
└─────────────────────────────────────────────────────────┘
```

### Databases

- **H2** - встроенная БД для разработки
- **PostgreSQL** - реляционная БД
- **R2DBC** - реактивный доступ к данным

---

## 🚀 Быстрый старт

### Установка зависимостей

```bash
# Проверка Java
java -version

# Проверка Maven
mvn -version

# Проверка Docker
docker --version
docker-compose --version
```

### Клонирование репозитория

```bash
git clone <repository-url>
cd client-server-development
```

---

## 📖 Дополнительные материалы

### Документация

- [RxJava Documentation](https://github.com/ReactiveX/RxJava)
- [RSocket Protocol](https://rsocket.io/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/)
- [Solidity Documentation](https://docs.soliditylang.org/)
- [Docker Documentation](https://docs.docker.com/)

### Обучающие ресурсы

- [Reactive Programming Introduction](https://gist.github.com/staltz/868e7e9bc2a7b8c1f754)
- [Spring RSocket Tutorial](https://spring.io/guides/gs/messaging-rsocket/)
- [Ethereum Development](https://ethereum.org/en/developers/)

---

## 🤝 Вклад и сотрудничество

Проект разработан в рамках учебного курса. Приветствуются:

- 🐛 Сообщения об ошибках
- 💡 Предложения по улучшению
- 📝 Дополнения к документации
- 🔧 Pull requests с исправлениями

---

## 📝 Лицензия

Этот проект создан в образовательных целях.

---

## 👨‍💻 Автор

**[Климкин Владимир Олегович]**
- 🎓 Группа: [ИКБО-16-22]

---


## ❓ FAQ

<details>
<summary><b>Какая Java версия нужна?</b></summary>

Минимум Java 17 (LTS). Рекомендуется использовать последнюю LTS версию.
</details>

<details>
<summary><b>Можно ли использовать IDE кроме IntelliJ IDEA?</b></summary>

Да, подойдет любая IDE: Eclipse, VS Code, NetBeans. Проекты основаны на Maven.
</details>

<details>
<summary><b>Нужен ли реальный Ethereum для ЛР №6?</b></summary>

Нет, используется Remix VM - виртуальная машина с тестовыми средствами.
</details>

<details>
<summary><b>Как запустить все проекты одновременно?</b></summary>

Каждый проект независим. Запускайте их в отдельных терминалах/процессах.
</details>

---


<div align="center">

## 💬 Обратная связь

Есть вопросы или предложения? Создайте [Issue](../../issues) или напишите напрямую!

---

**Made with ❤️ and ☕**

**Think Reactive. Build Distributed. Stay Decentralized.**

⭐ Если проект был полезен, поставьте звезду!

</div>

---


<div align="center">
