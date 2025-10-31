# Техническая документация проекта

## Архитектура приложения

### Компоненты

#### 1. MainActivity
**Файл**: `app/src/main/java/com/calldisplaymodifier/app/MainActivity.java`

**Назначение**: Главная активность приложения, управление UI и настройками

**Основные функции**:
- Проверка и запрос разрешений
- Настройка префикса и суффикса
- Включение/отключение модификации
- Тестирование overlay

**Используемые разрешения**:
```java
- READ_PHONE_STATE
- READ_CALL_LOG
- CALL_PHONE
- SYSTEM_ALERT_WINDOW
```

#### 2. CallMonitorService
**Файл**: `app/src/main/java/com/calldisplaymodifier/app/CallMonitorService.java`

**Назначение**: Сервис мониторинга входящих звонков на базе InCallService

**Принцип работы**:
```
1. Регистрируется как InCallService в системе
2. Получает уведомления о входящих звонках
3. Извлекает номер телефона из Call.Details
4. Передает информацию в OverlayService
5. Управляет lifecycle overlay
```

**Call States**:
- `STATE_RINGING` → Показывает overlay
- `STATE_ACTIVE` → Скрывает overlay
- `STATE_DISCONNECTED` → Скрывает overlay

#### 3. OverlayService
**Файл**: `app/src/main/java/com/calldisplaymodifier/app/OverlayService.java`

**Назначение**: Отображение custom overlay поверх системного UI звонков

**Технология**:
- WindowManager для создания overlay
- TYPE_APPLICATION_OVERLAY (Android 8+)
- TYPE_PHONE (Android 7 и ниже)

**Layout**: Использует CardView с Material Design

## Android API и совместимость

### Минимальная версия: Android 6.0 (API 23)

**Причина**: InCallService API доступен с API 23

### Поддерживаемые версии

| Android Version | API Level | Поддержка | Особенности |
|----------------|-----------|-----------|-------------|
| 6.0 Marshmallow | 23 | ✅ | Runtime permissions |
| 7.0-7.1 Nougat | 24-25 | ✅ | Multi-window |
| 8.0-8.1 Oreo | 26-27 | ✅ | Notification channels |
| 9.0 Pie | 28 | ✅ | Display cutout |
| 10 Q | 29 | ✅ | Scoped storage |
| 11 R | 30 | ✅ | One-time permissions |
| 12 S | 31 | ✅ | Material You |
| 13 T | 33 | ✅ | Runtime notifications |
| 14 U | 34 | ✅ | Full screen intent |

### Ключевые API

#### InCallService API
```java
public class CallMonitorService extends InCallService {
    @Override
    public void onCallAdded(Call call) {
        // Новый звонок
    }
    
    @Override
    public void onCallRemoved(Call call) {
        // Звонок завершен
    }
}
```

**Требования**:
- Разрешение `BIND_INCALL_SERVICE`
- Регистрация в AndroidManifest.xml
- Установка как default dialer (опционально)

#### WindowManager Overlay
```java
WindowManager.LayoutParams params = new WindowManager.LayoutParams(
    MATCH_PARENT,
    WRAP_CONTENT,
    TYPE_APPLICATION_OVERLAY, // API 26+
    FLAG_NOT_FOCUSABLE,
    PixelFormat.TRANSLUCENT
);
```

**Разрешение**: `SYSTEM_ALERT_WINDOW`

### Foreground Services

С Android 8.0 (API 26) все long-running services должны быть foreground:

```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    startForeground(NOTIFICATION_ID, notification);
}
```

## Структура проекта

```
CallDisplayModifier/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/calldisplaymodifier/app/
│   │       │   ├── MainActivity.java          # Main Activity
│   │       │   ├── CallMonitorService.java    # InCall Service
│   │       │   └── OverlayService.java        # Overlay Service
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   ├── activity_main.xml      # Main UI
│   │       │   │   └── overlay_call_info.xml  # Overlay UI
│   │       │   ├── values/
│   │       │   │   ├── strings.xml
│   │       │   │   ├── colors.xml
│   │       │   │   └── themes.xml
│   │       │   ├── mipmap-*/                  # Icons
│   │       │   └── drawable/                  # Vector assets
│   │       └── AndroidManifest.xml            # App manifest
│   ├── build.gradle                           # App build config
│   └── proguard-rules.pro                     # ProGuard rules
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties          # Gradle wrapper
├── build.gradle                               # Project build config
├── settings.gradle                            # Project settings
├── gradle.properties                          # Gradle properties
├── gradlew / gradlew.bat                      # Gradle wrapper scripts
└── README.md                                  # Documentation
```

## Gradle конфигурация

### Версии
- **Gradle**: 8.0
- **Android Gradle Plugin**: 8.1.0
- **Compile SDK**: 34
- **Min SDK**: 23
- **Target SDK**: 34

### Зависимости

```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.core:core:1.12.0'
}
```

## Best Practices

### 1. Управление разрешениями
- Runtime permissions для dangerous permissions
- Проверка перед каждым использованием
- Объяснение пользователю для чего нужны разрешения

### 2. Lifecycle management
- Правильная обработка onCreate/onDestroy
- Отписка от callbacks
- Cleanup ресурсов

### 3. Foreground Services
- Обязательное уведомление
- Правильный тип сервиса (PHONE_CALL)
- Остановка когда не нужен

### 4. Overlay
- Проверка разрешения перед показом
- Удаление view при destroy
- Обработка exceptions

### 5. Совместимость
- Проверка Build.VERSION.SDK_INT
- Использование @RequiresApi
- Fallback для старых версий

## Ограничения платформы

### Android 10+ (API 29)
- Ограничен доступ к PHONE_STATE
- Требуется USE_FULL_SCREEN_INTENT для full screen activities
- Background location access требует специального разрешения

### Android 11+ (API 30)
- Package visibility ограничения
- Scoped storage обязателен
- Auto-reset permissions

### Android 12+ (API 31)
- Approximate location option
- Bluetooth permissions split
- Notification trampolines ограничены

### Android 13+ (API 33)
- POST_NOTIFICATIONS runtime permission
- Photo picker API
- Themed app icons

## Производители устройств

### Xiaomi (MIUI)
- Требует "Display pop-up windows while running in background"
- Autostart permission
- Battery optimization exclusion

### Huawei (EMUI)
- Protected apps setting
- Battery optimization
- Notification importance

### Samsung (One UI)
- Put app to sleep ограничение
- Battery optimization
- Edge panel compatibility

### OnePlus (OxygenOS)
- Advanced optimization
- Battery optimization
- Notification settings

## Тестирование

### Unit Tests
Добавьте в `app/src/test/`:
```java
@Test
public void phoneNumberModification_isCorrect() {
    String prefix = "[Изменено] ";
    String number = "+1234567890";
    String result = prefix + number;
    assertEquals("[Изменено] +1234567890", result);
}
```

### Instrumented Tests
Добавьте в `app/src/androidTest/`:
```java
@Test
public void overlayPermission_isGranted() {
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    assertTrue(Settings.canDrawOverlays(context));
}
```

## Безопасность

### ProGuard/R8
- Обфускация кода в release
- Минификация ресурсов
- Оптимизация bytecode

### Разрешения
- Минимальный набор
- Runtime проверки
- Объяснение пользователю

### Данные
- Не хранить sensitive data
- SharedPreferences для настроек
- Не логировать номера телефонов в production

## Производительность

### Memory
- Своевременное освобождение ресурсов
- Избегать memory leaks
- WeakReference для callbacks

### Battery
- Efficient background work
- JobScheduler для отложенных задач
- Остановка сервисов когда не нужны

### UI
- ViewBinding вместо findViewById
- RecyclerView для списков
- Lazy loading

## Дальнейшее развитие

### Возможные улучшения
1. База данных контактов
2. История звонков
3. Правила модификации
4. Темная тема
5. Локализация
6. Настраиваемые шаблоны
7. Синхронизация с облаком
8. Machine learning для определения спама

### Технологии
- Room Database
- WorkManager
- DataStore
- Kotlin Coroutines
- Jetpack Compose
- Firebase
- ML Kit

## Ресурсы

### Документация Android
- [InCallService](https://developer.android.com/reference/android/telecom/InCallService)
- [WindowManager](https://developer.android.com/reference/android/view/WindowManager)
- [Permissions](https://developer.android.com/guide/topics/permissions/overview)
- [Foreground Services](https://developer.android.com/guide/components/foreground-services)

### Material Design
- [Material Components](https://material.io/components)
- [Design Guidelines](https://material.io/design)

### Gradle
- [Android Gradle Plugin](https://developer.android.com/studio/releases/gradle-plugin)
- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)

