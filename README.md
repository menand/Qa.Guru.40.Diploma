# Дипломный проект: автоматизация тестирования [Habitica](https://habitica.com)

[![tests](https://github.com/menand/Qa.Guru.40.Diploma/actions/workflows/tests.yml/badge.svg)](https://github.com/menand/Qa.Guru.40.Diploma/actions/workflows/tests.yml)

<p align="center">
  <a href="https://habitica.com/static/home">
    <img src="images/screenshots/habitica_home.png" alt="Habitica" width="900"/>
  </a>
</p>

> **Habitica** — таск-трекер в форме RPG-игры: привычки, ежедневные дела и todo прокачивают персонажа,
> а прокрастинация отнимает у него здоровье. Более 4 миллионов пользователей.

Проект покрывает продукт автотестами на трёх уровнях: **REST API**, **Web UI** и **мобильное Android-приложение**.

## Содержание

- [Технологии и инструменты](#технологии-и-инструменты)
- [Реализованные проверки](#реализованные-проверки)
- [Особенности проекта](#особенности-проекта)
- [Запуск тестов](#запуск-тестов)
- [Запуск в Jenkins](#запуск-в-jenkins)
- [GitHub Actions](#github-actions)
- [Allure-отчёт](#allure-отчёт)
- [Структура проекта](#структура-проекта)

## Технологии и инструменты

<p align="center">
  <a href="https://www.java.com/"><img src="images/logos/Java.png" width="45" height="45" alt="Java" title="Java 21"/></a>&nbsp;&nbsp;
  <a href="https://gradle.org/"><img src="images/logos/Gradle.png" width="45" height="45" alt="Gradle" title="Gradle 8"/></a>&nbsp;&nbsp;
  <a href="https://junit.org/junit5/"><img src="images/logos/JUnit5.png" width="45" height="45" alt="JUnit 5" title="JUnit 5"/></a>&nbsp;&nbsp;
  <a href="https://selenide.org/"><img src="images/logos/Selenide.png" width="45" height="45" alt="Selenide" title="Selenide"/></a>&nbsp;&nbsp;
  <a href="https://rest-assured.io/"><img src="images/logos/Rest-Assured.png" width="45" height="45" alt="REST Assured" title="REST Assured"/></a>&nbsp;&nbsp;
  <a href="https://appium.io/"><img src="images/logos/Appium.svg" width="45" height="45" alt="Appium" title="Appium + selenide-appium"/></a>&nbsp;&nbsp;
  <a href="https://www.browserstack.com/"><img src="images/logos/Browserstack.svg" width="45" height="45" alt="BrowserStack" title="BrowserStack App Automate"/></a>&nbsp;&nbsp;
  <a href="https://aerokube.com/selenoid/"><img src="images/logos/Selenoid.png" width="45" height="45" alt="Selenoid" title="Selenoid"/></a>&nbsp;&nbsp;
  <a href="https://allurereport.org/"><img src="images/logos/Allure_Report.png" width="45" height="45" alt="Allure Report" title="Allure Report"/></a>&nbsp;&nbsp;
  <a href="https://www.jenkins.io/"><img src="images/logos/Jenkins.png" width="45" height="45" alt="Jenkins" title="Jenkins"/></a>&nbsp;&nbsp;
  <a href="https://www.jetbrains.com/idea/"><img src="images/logos/Intelij_IDEA.png" width="45" height="45" alt="IntelliJ IDEA" title="IntelliJ IDEA"/></a>
</p>

- **Java 21 + Gradle 8** — база проекта, слои разведены задачами `api_test` / `web_test` / `mobile_test`
- **JUnit 5** — раннер: теги, параметризованные тесты, `test-retry` для браузерных прогонов
- **REST Assured** — API-тесты: request/response-спецификации, POJO-модели (Lombok + Jackson)
- **Selenide** — web-тесты по паттерну Page Object, локальный Chrome или Selenoid
- **Appium + selenide-appium + BrowserStack** — мобильные смоук-тесты Android-приложения
- **Allure** — отчёты: шаги, вложения (скриншот, page source, видео), Owner/Severity/Epic-разметка
- **Owner** — конфигурация, **Datafaker** — генерация тестовых данных

## Реализованные проверки

### 🔌 API — 15 тестов

- ✅ регистрация: валидные креды в ответе (UUID id и apiToken) / повторный username → точный текст ошибки
- ✅ логин: возвращает те же id и apiToken / неверный пароль и запрос без auth-заголовков → 401 с точными сообщениями
- ✅ профиль: данные и стартовые статы свежего аккаунта (level 1, 50 HP, email) / смена отображаемого имени
- ✅ задачи: дефолты новой todo и habit (сложность, ценность, кнопки «+»/«−»), список, редактирование
- ✅ скоринг: дельта ровно 1, +6 XP и +1 GP, задача становится `completed` — проверяется повторным GET
- ✅ удаление: 404 NotFound с точным сообщением по удалённой задаче
- ✅ теги: создание, список, переименование (id неизменен), удаление

### 🌐 Web UI — 17 тестов

- ✅ лендинг: заголовок, форма быстрой регистрации, секции, ссылки на Google Play и App Store
- ✅ навигация: кнопка Log In и ссылка Get Started ведут на нужные формы
- ✅ регистрация через двухшаговую форму: в приложении появляется персонаж с именем пользователя
- ✅ логин: валидный — страница задач с именем персонажа; неверный пароль — **красный** тост
  с точным текстом ошибки (проверяются и текст, и цвет фона/шрифта)
- ✅ логаут: сессия завершается, пользователь в неавторизованной зоне
- ✅ задачи: создание todo/habit через quick-add, выполнение, редактирование, удаление, поиск

### 📱 Mobile (Android) — 5 смоук-тестов

- ✅ интро показывается при первом запуске
- ✅ Skip ведёт к выбору способа входа
- ✅ кнопка входа открывает форму логина
- ✅ логин с валидными кредами (пользователь создан через API) открывает главный экран с задачами
- ✅ todo, созданная через API, отображается на вкладке To Do's

Тестируется официальный релиз Habitica 4.4 (apk из GitHub-релизов, загружен в BrowserStack
под custom_id `habitica-android`).

## Особенности проекта

- **Никаких селекторов и JSON-экстракторов в тестах**: селекторы инкапсулированы в Page Object'ах
  (`web/pages`, `mobile/screens`), JSON — в шагах API (`api/steps`), возвращающих типизированные модели.
  Статус-коды проверяются response-спецификациями (`ApiSpecs.status(...)`).
- **Точные проверки, снятые с живого продукта**: тексты ошибок дословно, формат UUID, дефолты задач,
  математика наград, цвет тоста — тесты ловят реальные регрессии, а не «что-нибудь не пустое».
- **Rate limit**: Habitica ограничивает клиента 30 запросами в минуту — `RateLimitFilter` следит за
  `X-RateLimit-Remaining` и дожидается нового окна; холодный старт с исчерпанным окном ретраится
  при создании тестового пользователя.
- **Один общий пользователь на прогон** (`TestUsers.shared()`): создаётся через API, удаляется
  shutdown hook'ом — не мусорим аккаунтами и экономим rate limit.
- **Авторизация в web-тестах без UI**: креды кладутся в `localStorage` (`BrowserSession`), форма
  логина проверяется только в тестах логина.
- **Мобильный код — в отдельном source set** (`src/mobileTest/java`): selenide-appium глобально
  подменяет плагины Selenide (page source, описание элементов) и через CDP-websocket ломает
  web-прогоны в Selenoid, поэтому appium-зависимостей нет на classpath web/api-тестов.
- **Ретраи** только для браузерных прогонов — инфраструктурные флейки живого стенда.

## Запуск тестов

```bash
./gradlew api_test                      # только API
./gradlew web_test                      # web, локальный Chrome
./gradlew web_test -Dheadless=true      # web без окна браузера
./gradlew web_test -Dbrowser=firefox -DbrowserSize=1280x1024        # другой браузер и размер окна
./gradlew web_test "-DremoteUrl=https://user:pass@selenoid.example.com/wd/hub" -DbrowserVersion=128   # Selenoid
./gradlew mobile_test                   # mobile на BrowserStack (нужны креды, см. ниже)
./gradlew test                          # api + web (mobile исключён по умолчанию)
```

| Свойство         | По умолчанию           | Назначение                                        |
|------------------|------------------------|---------------------------------------------------|
| `baseUrl`        | `https://habitica.com` | стенд web-тестов                                  |
| `browser`        | `chrome`               | браузер: `chrome` / `firefox`                     |
| `browserVersion` | *(пусто)*              | версия браузера для Selenoid; пусто — любая       |
| `browserSize`    | `1920x1080`            | размер окна                                       |
| `headless`       | `false`                | headless-режим                                    |
| `remoteUrl`      | *(пусто)*              | Selenoid/Grid; пусто — локальный браузер          |
| `videoEnabled`   | `true`                 | запись видео в Selenoid (только с `remoteUrl`)    |

Для мобильных тестов приложение должно быть загружено в BrowserStack
(custom_id `habitica-android`, см. `browserstack.properties`), креды передаются через
переменные окружения `BROWSERSTACK_USER` / `BROWSERSTACK_KEY`.

## Запуск в Jenkins

Параметризованная джоба (Active Choices / Choice Parameters):

| Параметр          | Значения (Groovy return для Active Choices)                          |
|-------------------|----------------------------------------------------------------------|
| `TASK`            | `return ["web_test:selected", "api_test", "mobile_test", "test"]`   |
| `BROWSER`         | `return ["chrome:selected", "firefox"]`                              |
| `BROWSER_VERSION` | `return ["128:selected", "127", "125"]` — версии из вашего Selenoid  |
| `BROWSER_SIZE`    | `return ["1920x1080:selected", "1280x1024", "1024x768"]`             |
| `REMOTE_URL`      | строковый параметр, `https://user:pass@selenoid.example.com/wd/hub`  |

Шаг сборки:

```bash
./gradlew clean ${TASK} \
  -Dbrowser=${BROWSER} \
  -DbrowserVersion=${BROWSER_VERSION} \
  -DbrowserSize=${BROWSER_SIZE} \
  "-DremoteUrl=${REMOTE_URL}" \
  -DBROWSERSTACK_USER=${BROWSERSTACK_USER} \
  -DBROWSERSTACK_KEY=${BROWSERSTACK_KEY}
```

Браузерные свойства и `REMOTE_URL` влияют только на `web_test`, креды BrowserStack — только на
`mobile_test`; лишние свойства безвредны, поэтому shell-шаг один на все варианты. После сборки
Allure-плагин Jenkins публикует отчёт из `build/allure-results`.

## GitHub Actions

Второй, независимый CI — [workflow `tests`](https://github.com/menand/Qa.Guru.40.Diploma/actions/workflows/tests.yml):

- **на каждый push в `main`** автоматически бежит `api_test`;
- **вручную** (Run workflow) запускается любой слой: `api_test` / `web_test` / `mobile_test` / `test`;
- web-тесты идут в headless Chrome самого раннера — Selenoid не нужен;
- mobile-тесты ходят в BrowserStack (креды в GitHub Secrets);
- после прогона Allure-отчёт с историей публикуется на
  [GitHub Pages](https://menand.github.io/Qa.Guru.40.Diploma/), в Telegram уходит уведомление.

## Allure-отчёт

Живой отчёт последнего CI-прогона: **https://menand.github.io/Qa.Guru.40.Diploma/**

```bash
./gradlew allureServe   # собрать и открыть отчёт по результатам последнего локального прогона
```

### Главная страница

<p align="center"><img src="images/screenshots/allure_overview.png" alt="Allure overview" width="900"/></p>

### Тесты по фичам и сторям

<p align="center"><img src="images/screenshots/allure_behaviors.png" alt="Allure behaviors" width="900"/></p>

### Шаги теста с вложениями

К каждому API-шагу прикладываются запрос и ответ, к упавшим браузерным тестам — скриншот
и page source, к мобильным — видео сессии BrowserStack.

<p align="center"><img src="images/screenshots/allure_test_detail.png" alt="Allure test detail" width="900"/></p>

## Структура проекта

```
src/test/java            — api + web (без appium-зависимостей)
├── api
│   ├── models   — POJO-модели запросов/ответов (Lombok)
│   ├── specs    — request/response-спецификации, Allure- и rate-limit-фильтры
│   └── steps    — шаги API (AuthApi, UserApi, TasksApi, TagsApi)
├── web/pages    — Page Object'ы веба (HomePage, LoginPage, RegisterPage, TasksPage)
├── config       — Owner-конфиги (api / web / browserstack)
├── helpers      — TestUsers, BrowserSession, Attachments
└── tests
    ├── api      — 15 тестов
    └── web      — 17 тестов

src/mobileTest/java      — отдельный source set с selenide-appium
├── mobile
│   ├── drivers  — BrowserstackDriver (Appium / App Automate)
│   └── screens  — Page Object'ы мобильного приложения (Intro, Login, Main)
└── tests/mobile — 5 смоук-тестов
```
