# Быстрый старт - Call Display Modifier

## 🚀 Что сделано

Создано полноценное Android приложение на Java для вашей курсовой работы:

✅ **3 основных Java класса** с полной функциональностью  
✅ **Material Design UI** с современным интерфейсом  
✅ **Поддержка Android 6.0-14** (API 23-34)  
✅ **Полная документация** на русском языке  
✅ **Скрипты автоматической сборки** для Windows/Linux/Mac  
✅ **Все best practices** Android разработки  

## 📋 Что нужно для сборки

### 1. Установите Java
Скачайте и установите Java JDK:
- **Рекомендуется**: [Adoptium JDK 17](https://adoptium.net/)
- Или используйте установленную версию Java 8+

Проверьте установку:
```bash
java -version
```

### 2. Установите Android SDK

#### Вариант A: Android Studio (проще)
1. Скачайте [Android Studio](https://developer.android.com/studio)
2. Установите и запустите
3. Откройте SDK Manager (Tools → SDK Manager)
4. Установите:
   - Android SDK Platform 34
   - Android SDK Build-Tools
   - Android SDK Platform-Tools

#### Вариант B: Command Line Tools (быстрее)
1. Скачайте [Command Line Tools](https://developer.android.com/studio#command-tools)
2. Распакуйте в `C:\Android\Sdk` (Windows) или `~/Android/Sdk` (Linux/Mac)
3. Установите компоненты:
```bash
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

### 3. Настройте проект

Создайте файл `local.properties` в корне проекта:

**Windows:**
```properties
sdk.dir=C:\\Users\\ВашеИмя\\AppData\\Local\\Android\\Sdk
```

**Linux:**
```properties
sdk.dir=/home/вашеимя/Android/Sdk
```

**Mac:**
```properties
sdk.dir=/Users/вашеимя/Library/Android/sdk
```

⚠️ **Важно**: Используйте двойные обратные слэши `\\` для Windows!

## 🔨 Сборка APK

### Windows (PowerShell)

#### Автоматически:
```powershell
.\build-apk.ps1
```

#### Вручную:
```powershell
.\gradlew.bat clean assembleDebug
```

### Linux/Mac

#### Автоматически:
```bash
chmod +x build-apk.sh
./build-apk.sh
```

#### Вручную:
```bash
chmod +x gradlew
./gradlew clean assembleDebug
```

## 📱 Установка на устройство

После успешной сборки APK находится в:
```
app/build/outputs/apk/debug/app-debug.apk
```

Или в корне проекта:
```
CallDisplayModifier.apk
```

### Установка через ADB:
```bash
adb install CallDisplayModifier.apk
```

### Установка вручную:
1. Скопируйте APK на телефон
2. Включите "Установка из неизвестных источников"
3. Откройте APK на телефоне

## 📚 Структура проекта

```
podmena/
├── app/
│   ├── src/main/
│   │   ├── java/com/calldisplaymodifier/app/
│   │   │   ├── MainActivity.java          ← Главный экран
│   │   │   ├── CallMonitorService.java    ← Мониторинг звонков
│   │   │   └── OverlayService.java        ← Отображение overlay
│   │   ├── res/                           ← Ресурсы (UI, strings, etc)
│   │   └── AndroidManifest.xml            ← Конфигурация приложения
│   └── build.gradle                       ← Конфигурация сборки
│
├── build-apk.ps1                          ← Скрипт сборки (Windows)
├── build-apk.sh                           ← Скрипт сборки (Linux/Mac)
│
├── README.md                              ← Общая информация
├── COURSEWORK_INFO.md                     ← Для курсовой работы
├── TECHNICAL_DETAILS.md                   ← Технические детали
├── INSTALLATION.md                        ← Подробная установка
└── QUICK_START.md                         ← Этот файл
```

## 🎯 Использование приложения

1. **Запустите приложение** на Android устройстве
2. **Предоставьте разрешения**:
   - Доступ к телефону
   - Отображение поверх других окон
3. **Настройте параметры**:
   - Префикс (например: "[Тест] ")
   - Суффикс (если нужен)
4. **Протестируйте**: Нажмите "Тестовый показ"
5. **Включите службу**: "Включить службу"

## 📖 Документация

### Основные файлы:
- **README.md** - Полное описание проекта
- **COURSEWORK_INFO.md** - Информация для защиты курсовой
- **TECHNICAL_DETAILS.md** - Технические детали для понимания архитектуры
- **INSTALLATION.md** - Подробные инструкции по установке всех компонентов

### Для защиты курсовой:
Прочитайте `COURSEWORK_INFO.md` - там:
- Описание функциональности
- Использованные технологии
- Ответы на возможные вопросы
- Демонстрация знаний

## ⚠️ Важные замечания

### Безопасность:
- ✅ Приложение создано для **образовательных целей**
- ⚠️ Не используйте для мошенничества
- ⚠️ Соблюдайте законы о защите данных

### Совместимость:
- ✅ Работает на Android 6.0+
- ⚠️ На некоторых устройствах (Xiaomi, Huawei) требуются дополнительные настройки
- ⚠️ Требуется отключение оптимизации батареи

### Разрешения:
- `READ_PHONE_STATE` - чтение состояния телефона
- `READ_CALL_LOG` - доступ к журналу звонков
- `SYSTEM_ALERT_WINDOW` - overlay поверх приложений

## 🆘 Проблемы и решения

### "SDK location not found"
**Решение**: Создайте файл `local.properties` с путем к Android SDK

### "Java version not supported"
**Решение**: Установите Java JDK 17 с https://adoptium.net/

### "Gradle sync failed"
**Решение**: 
```bash
# Удалите кэш
rm -rf ~/.gradle/caches  # Linux/Mac
# или
rmdir /s %USERPROFILE%\.gradle\caches  # Windows

# Попробуйте снова
./gradlew clean build
```

### "License not accepted"
**Решение**:
```bash
sdkmanager --licenses
# Примите все лицензии нажатием 'y'
```

### Overlay не отображается
**Решение**: 
1. Проверьте разрешение "Поверх других окон"
2. Отключите оптимизацию батареи для приложения
3. На Xiaomi: Настройки → Приложения → Разрешения → Автозапуск

## 📞 Тестирование

1. **Запустите приложение**
2. **Нажмите "Тестовый показ"** - должен появиться overlay на 5 секунд
3. **Для реального теста**: Попросите кого-то позвонить вам

## 🎓 Для курсовой работы

### Что рассказать на защите:

1. **Архитектура**:
   - 3 основных компонента: Activity, 2 Services
   - Service-based architecture
   - Material Design UI

2. **Технологии**:
   - Java 8+
   - Android SDK 23-34
   - InCallService API
   - WindowManager для overlay
   - Foreground Services

3. **Особенности**:
   - Runtime permissions
   - Поддержка разных версий Android
   - Совместимость с различными производителями
   - Best practices

### Возможные вопросы:
См. раздел "Возможные вопросы на защите" в `COURSEWORK_INFO.md`

## ✅ Чек-лист перед защитой

- [ ] Проект собирается без ошибок
- [ ] APK установлен на устройство
- [ ] Приложение запускается
- [ ] Тестовый режим работает
- [ ] Прочитана вся документация
- [ ] Понимание архитектуры проекта
- [ ] Готовы ответы на вопросы
- [ ] Подготовлена презентация (если нужна)

## 🚀 Следующие шаги

1. **Соберите проект** (следуя инструкциям выше)
2. **Протестируйте на устройстве**
3. **Изучите код** (начните с MainActivity.java)
4. **Прочитайте документацию** (особенно COURSEWORK_INFO.md)
5. **Подготовьте презентацию** для защиты

## 📧 Полезные ссылки

- [Android Developers](https://developer.android.com/)
- [Java Documentation](https://docs.oracle.com/javase/8/docs/)
- [Material Design](https://material.io/design)
- [Gradle](https://gradle.org/)

---

**Удачи с курсовой работой! 🎓**

Если возникнут вопросы - вся информация есть в документации проекта.

