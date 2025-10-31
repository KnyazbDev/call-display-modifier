# Информация для курсовой работы

## Название проекта
**Call Display Modifier** - Учебное Android приложение для работы с входящими звонками

## Описание проекта

Данный проект представляет собой Android приложение, разработанное на языке Java, которое демонстрирует работу с современными Android API для управления телефонными звонками. Приложение использует InCallService API для мониторинга входящих звонков и создает пользовательский overlay для отображения дополнительной информации о звонящем.

### Образовательная цель
Проект создан в образовательных целях для изучения:
- Android разработки на Java
- Работы с системными сервисами Android
- Управления разрешениями (Runtime Permissions)
- Создания Foreground Services
- Работы с WindowManager и Overlay
- Современных паттернов разработки Android приложений

## Функциональные возможности

### Основные функции:
1. **Мониторинг входящих звонков** - использует InCallService API
2. **Отображение custom UI** - создает overlay поверх стандартного интерфейса звонков
3. **Модификация отображаемой информации** - добавляет префикс и суффикс к номеру
4. **Управление настройками** - пользовательский интерфейс для конфигурации
5. **Тестовый режим** - возможность проверить работу без реального звонка

### Технические характеристики:
- **Язык программирования**: Java 8+
- **Минимальная версия Android**: 6.0 (API 23)
- **Целевая версия Android**: 14 (API 34)
- **Build система**: Gradle 8.0
- **UI Framework**: Material Design Components
- **Архитектура**: Service-based architecture

## Структура проекта

### Основные компоненты:

#### 1. MainActivity (Главная активность)
- **Назначение**: Основной UI приложения, управление настройками
- **Функции**:
  - Запрос и проверка разрешений
  - Настройка параметров модификации
  - Тестирование функциональности
  - Включение/отключение сервисов

#### 2. CallMonitorService (Сервис мониторинга)
- **Тип**: InCallService
- **Назначение**: Отслеживание входящих звонков
- **Функции**:
  - Получение информации о входящих звонках
  - Извлечение номера телефона
  - Управление lifecycle overlay
  - Работа в режиме foreground service

#### 3. OverlayService (Сервис отображения)
- **Тип**: Foreground Service
- **Назначение**: Отображение custom UI поверх других приложений
- **Функции**:
  - Создание overlay через WindowManager
  - Отображение модифицированной информации
  - Управление жизненным циклом overlay

## Технологии и API

### Использованные Android API:
1. **InCallService API** (API 23+)
   - Мониторинг телефонных звонков
   - Получение информации о Call состояниях

2. **WindowManager API**
   - Создание overlay windows
   - Управление положением и параметрами окон

3. **TelecomManager API**
   - Управление телефонными функциями
   - Регистрация приложения как dialer

4. **Runtime Permissions API**
   - Динамический запрос разрешений
   - Проверка статуса разрешений

5. **Foreground Services**
   - Работа в фоновом режиме
   - Notification channels

### Используемые библиотеки:
```gradle
- androidx.appcompat:appcompat:1.6.1        # AppCompat support
- com.google.android.material:material:1.9.0 # Material Design
- androidx.constraintlayout:constraintlayout:2.1.4 # Layout
- androidx.core:core:1.12.0                  # Core utilities
```

## Разрешения (Permissions)

Приложение использует следующие разрешения:

### Обязательные (Dangerous):
- `READ_PHONE_STATE` - чтение состояния телефона
- `READ_CALL_LOG` - доступ к журналу звонков
- `CALL_PHONE` - возможность совершать звонки
- `SYSTEM_ALERT_WINDOW` - отображение поверх других окон

### Дополнительные:
- `MANAGE_OWN_CALLS` - управление собственными звонками
- `FOREGROUND_SERVICE` - работа foreground сервисов
- `FOREGROUND_SERVICE_PHONE_CALL` - тип foreground сервиса
- `POST_NOTIFICATIONS` - отображение уведомлений (Android 13+)

## Совместимость

### Поддерживаемые версии Android:
- ✅ Android 6.0 Marshmallow (API 23)
- ✅ Android 7.0-7.1 Nougat (API 24-25)
- ✅ Android 8.0-8.1 Oreo (API 26-27)
- ✅ Android 9.0 Pie (API 28)
- ✅ Android 10 Q (API 29)
- ✅ Android 11 R (API 30)
- ✅ Android 12/12L S (API 31-32)
- ✅ Android 13 Tiramisu (API 33)
- ✅ Android 14 UpsideDownCake (API 34)

### Тестирование на устройствах:
Приложение разработано с учетом следующих производителей:
- Samsung (One UI)
- Xiaomi (MIUI)
- Huawei (EMUI)
- OnePlus (OxygenOS)
- Google (Stock Android)

## Безопасность и этика

### ⚠️ ВАЖНОЕ ПРЕДУПРЕЖДЕНИЕ
Данное приложение создано исключительно в **образовательных целях**. 

**Приложение НЕ предназначено для**:
- Мошенничества или обмана
- Нарушения приватности
- Коммерческого использования
- Распространения без согласия

**Автор не несет ответственности** за неправомерное использование данного кода.

### Правовые аспекты:
- Изменение caller ID может быть незаконным в некоторых юрисдикциях
- Использование только с образовательными целями
- Соблюдение законов о защите данных (GDPR, CCPA и др.)
- Получение согласия пользователей

## Установка и запуск

### Требования:
1. **Java JDK 8+** (рекомендуется JDK 17)
2. **Android SDK** с API 23-34
3. **Gradle 8.0+** (включен в проект)
4. **Android Studio** (опционально, но рекомендуется)

### Процесс сборки:

#### Windows:
```powershell
# Автоматическая сборка
.\build-apk.ps1

# Или вручную
.\gradlew.bat clean assembleDebug
```

#### Linux/Mac:
```bash
# Автоматическая сборка
./build-apk.sh

# Или вручную
./gradlew clean assembleDebug
```

### Результат:
APK файл создается в: `app/build/outputs/apk/debug/app-debug.apk`

## Инструкции для преподавателя

### Проверка функциональности:

1. **Установка**:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Запуск приложения**:
   - Откройте приложение на устройстве
   - Предоставьте необходимые разрешения
   - Используйте кнопку "Тестовый показ" для проверки overlay

3. **Проверка кода**:
   - Все исходные коды находятся в `app/src/main/java/`
   - Layout файлы в `app/src/main/res/layout/`
   - Конфигурация в `app/build.gradle` и `AndroidManifest.xml`

4. **Документация**:
   - `README.md` - общая информация
   - `TECHNICAL_DETAILS.md` - технические детали
   - `INSTALLATION.md` - инструкции по установке
   - `COURSEWORK_INFO.md` - данный файл

## Демонстрация знаний

### Проект демонстрирует знание:

#### 1. Java программирование:
- ООП принципы
- Работа с коллекциями
- Exception handling
- Callbacks и listeners
- Многопоточность

#### 2. Android разработка:
- Activity lifecycle
- Service lifecycle
- Intent и Intent filters
- Broadcast receivers
- Permissions system
- SharedPreferences
- Notifications
- ViewBinding

#### 3. Android UI:
- XML layouts
- Material Design
- ConstraintLayout
- CardView
- Themes и styles
- Resources management

#### 4. Системное программирование:
- InCallService API
- WindowManager
- TelecomManager
- Package Manager
- Foreground Services

#### 5. Best Practices:
- SOLID принципы
- Clean Code
- Error handling
- Resource management
- Version compatibility
- User experience

## Возможные вопросы на защите

### 1. Почему выбран InCallService API?
**Ответ**: InCallService - официальный API для работы со звонками, доступный с Android 6.0. Это единственный легальный способ мониторинга звонков в современных версиях Android.

### 2. Как работает overlay система?
**Ответ**: Overlay создается через WindowManager.addView() с использованием TYPE_APPLICATION_OVERLAY (Android 8+). Требует специального разрешения SYSTEM_ALERT_WINDOW.

### 3. Почему минимальная версия API 23?
**Ответ**: InCallService API был введен в Android 6.0 (API 23). Более ранние версии не поддерживают этот функционал.

### 4. Как обеспечивается совместимость?
**Ответ**: Используется проверка Build.VERSION.SDK_INT для условного выполнения кода в зависимости от версии Android.

### 5. Какие ограничения у приложения?
**Ответ**: 
- Требует установки как default dialer (в некоторых случаях)
- Производители могут ограничивать overlay
- Battery optimization может останавливать сервис
- Android 10+ имеет дополнительные ограничения на фоновую работу

## Статистика проекта

### Код:
- **Java файлы**: 3 основных класса
- **XML layouts**: 2 layout файла
- **Resources**: Strings, Colors, Themes
- **Общий объем**: ~1000+ строк кода

### Документация:
- **README.md**: ~300 строк
- **TECHNICAL_DETAILS.md**: ~500 строк
- **INSTALLATION.md**: ~200 строк
- **COURSEWORK_INFO.md**: этот файл

### Конфигурация:
- Gradle конфигурация
- ProGuard rules
- AndroidManifest с полной конфигурацией
- Build scripts для автоматизации

## Дальнейшее развитие

### Возможные улучшения:
1. База данных контактов (Room)
2. История звонков с поиском
3. Правила модификации по контактам
4. Темная тема / Dynamic colors (Android 12+)
5. Локализация на несколько языков
6. Настройки резервного копирования
7. Widget для быстрого доступа
8. Machine Learning для spam detection

### Технологии для улучшения:
- Kotlin вместо Java
- Jetpack Compose для UI
- Coroutines для async операций
- Hilt для dependency injection
- DataStore вместо SharedPreferences

## Контакты и ресурсы

### Полезные ссылки:
- [Android Developers](https://developer.android.com/)
- [Material Design](https://material.io/)
- [Stack Overflow - Android](https://stackoverflow.com/questions/tagged/android)
- [GitHub - Android Samples](https://github.com/android)

### Сообщество:
- Reddit: r/androiddev
- Discord: Android Dev
- Telegram: Android Developers

---

## Заключение

Данный проект представляет собой полноценное Android приложение, демонстрирующее работу с современными Android API. Проект включает:

✅ Качественный исходный код на Java  
✅ Современную архитектуру приложения  
✅ Поддержку широкого спектра Android версий  
✅ Полную документацию  
✅ Скрипты для автоматической сборки  
✅ Best practices Android разработки  

**Приложение готово к демонстрации и защите курсовой работы.**

---

*Проект создан для образовательных целей. Используйте ответственно.*

