# Дипломный проект: автоматизация тестирования [Habitica](https://habitica.com)

Habitica — таск-трекер в форме RPG-игры: привычки, ежедневные дела и todo прокачивают персонажа.
Проект покрывает три слоя продукта автотестами: **REST API**, **Web UI** и **мобильное Android-приложение**.

## Технологии и инструменты

- **Java 21**, **Gradle 8**
- **JUnit 5** — тестовый раннер (теги `api` / `web` / `mobile`, параметризованные тесты)
- **REST Assured** — API-тесты (request/response-спецификации, POJO-модели)
- **Selenide** — web-тесты (Page Object)
- **Appium + selenide-appium + BrowserStack** — мобильные тесты
- **Allure** — отчётность (шаги, скриншоты, page source, видео BrowserStack)
- **Owner** — конфигурация, **Lombok** — модели, **Datafaker** — тестовые данные

## Что покрыто

### API (`./gradlew api_test`)

- регистрация: успешная / занятый username
- логин: валидные креды / неверный пароль / запрос без auth-заголовков
- профиль: чтение, смена отображаемого имени
- задачи: создание todo и habit, список, редактирование текста, скоринг (награда), удаление + 404
- теги: создание, список, переименование, удаление

### Web UI (`./gradlew web_test`)

- лендинг: заголовок, форма быстрой регистрации, секции, ссылки на сторы, навигация на логин/регистрацию
- аутентификация: регистрация через двухшаговую форму, логин (валидный/невалидный), логаут
- задачи: создание todo/habit через quick-add, выполнение, редактирование, удаление, поиск

### Mobile (`./gradlew mobile_test`)

Смоук-сценарии Android-приложения Habitica на BrowserStack App Automate:

- интро показывается при первом запуске, Skip ведёт к выбору способа входа
- кнопка Login открывает форму входа
- логин с валидными кредами (пользователь создан через API) открывает главный экран с задачами
- нижняя навигация переключает вкладки задач

Онбординг и логин приложения написаны на Jetpack Compose без resource-id и testTag,
поэтому эти экраны локализуются по видимому тексту; экраны после логина — классические View
с обычными resource-id (см. `mobile/screens/MobileBy.java`).

## Архитектурные решения

- **Никаких селекторов и JSON-экстракторов в тестах**: селекторы инкапсулированы в Page Object'ах
  (`web/pages`, `mobile/screens`), JSON — в шагах API (`api/steps`), которые возвращают типизированные
  модели (`api/models`). Статус-коды проверяются response-спецификациями (`ApiSpecs.status(...)`).
- **Rate limit**: Habitica ограничивает клиента 30 запросами в минуту — `RateLimitFilter` следит за
  заголовком `X-RateLimit-Remaining` и дожидается нового окна вместо падения с 429.
- **Один общий пользователь на прогон** (`TestUsers.shared()`): создаётся через API, удаляется
  shutdown hook'ом — не мусорим аккаунтами и экономим rate limit.
- **Авторизация в web-тестах без UI**: креды кладутся в `localStorage` (`BrowserSession`), логин через
  форму проверяется только в тестах логина.
- **Ретраи** только для браузерных прогонов (web/mobile) — инфраструктурные флейки живого стенда.

## Структура проекта

```
src/test/java
├── api
│   ├── models   — POJO-модели запросов/ответов (Lombok)
│   ├── specs    — request/response-спецификации, Allure- и rate-limit-фильтры
│   └── steps    — шаги API (AuthApi, UserApi, TasksApi, TagsApi)
├── web/pages    — Page Object'ы веба (HomePage, LoginPage, RegisterPage, TasksPage)
├── mobile
│   ├── drivers  — BrowserstackDriver (Appium / App Automate)
│   └── screens  — Page Object'ы мобильного приложения (Intro, Login, Main)
├── config       — Owner-конфиги (api / web / browserstack)
├── helpers      — TestUsers, BrowserSession, Attachments
└── tests
    ├── api      — 15 тестов
    ├── web      — 17 тестов
    └── mobile   — 5 смоук-тестов Android-приложения
```

## Запуск

```bash
./gradlew api_test                      # только API
./gradlew web_test                      # web, Chrome из web.properties
./gradlew web_test -Dheadless=true      # web без окна браузера
./gradlew mobile_test                   # mobile на BrowserStack (нужны креды, см. ниже)
```

Параметры переопределяются системными свойствами:

| Свойство      | По умолчанию          | Назначение              |
|---------------|-----------------------|-------------------------|
| `baseUrl`     | `https://habitica.com`| стенд web-тестов        |
| `browser`     | `chrome`              | браузер                 |
| `browserSize` | `1920x1080`           | размер окна             |
| `headless`    | `false`               | headless-режим          |

Для мобильных тестов приложение должно быть загружено в BrowserStack
(`app=bs://habitica-android` в `browserstack.properties`), креды передаются через
переменные окружения `BROWSERSTACK_USER` / `BROWSERSTACK_KEY`.

## Allure-отчёт

```bash
./gradlew allureServe   # собрать и открыть отчёт по результатам последнего прогона
```

К упавшим браузерным тестам AllureSelenide прикладывает скриншот и page source,
к каждому мобильному тесту прикладывается видео сессии BrowserStack.
