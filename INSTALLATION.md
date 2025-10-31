# Инструкция по установке и компиляции

## Шаг 1: Установка Android SDK

### Вариант A: Установка Android Studio (рекомендуется)

1. Скачайте и установите Android Studio с официального сайта:
   https://developer.android.com/studio

2. Запустите Android Studio
3. Откройте "SDK Manager" (Tools → SDK Manager)
4. Установите следующие компоненты:
   - Android SDK Platform 34
   - Android SDK Platform 23 (минимальная версия)
   - Android SDK Build-Tools
   - Android SDK Platform-Tools
   - Android SDK Command-line Tools

### Вариант B: Установка только Command Line Tools

1. Скачайте Command Line Tools:
   https://developer.android.com/studio#command-tools

2. Извлеките в папку (например, `C:\Android\SDK`)
3. Установите необходимые компоненты через командную строку:
```bash
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
sdkmanager "platforms;android-23"
```

## Шаг 2: Настройка переменных окружения

### Windows:

1. Откройте "Системные переменные среды"
2. Создайте новую переменную `ANDROID_HOME` со значением пути к SDK
   (например: `C:\Users\YourName\AppData\Local\Android\Sdk`)
3. Добавьте в PATH:
   - `%ANDROID_HOME%\platform-tools`
   - `%ANDROID_HOME%\tools`

### Linux/Mac:

Добавьте в `~/.bashrc` или `~/.zshrc`:
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
```

## Шаг 3: Настройка проекта

1. Создайте файл `local.properties` в корне проекта:
```properties
sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

**ВАЖНО**: Используйте двойные обратные слэши `\\` для Windows!

Примеры путей:
- Windows: `sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk`
- Linux: `sdk.dir=/home/yourname/Android/Sdk`
- Mac: `sdk.dir=/Users/yourname/Library/Android/sdk`

## Шаг 4: Компиляция проекта

### Способ 1: Через Android Studio

1. Откройте папку проекта в Android Studio
2. Дождитесь завершения индексации и синхронизации Gradle
3. Выберите Build → Build Bundle(s) / APK(s) → Build APK(s)
4. APK будет создан в `app/build/outputs/apk/debug/app-debug.apk`

### Способ 2: Через командную строку

```bash
# Windows
gradlew.bat clean assembleDebug

# Linux/Mac
./gradlew clean assembleDebug
```

APK файл будет создан в: `app\build\outputs\apk\debug\app-debug.apk`

### Способ 3: Через PowerShell скрипт (Windows)

Запустите скрипт:
```powershell
.\build-apk.ps1
```

## Шаг 5: Установка APK на устройство

### Через ADB:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Вручную:
1. Скопируйте APK на устройство
2. Включите "Установка из неизвестных источников" в настройках
3. Откройте APK файл на устройстве

## Требования к системе

### Минимальные требования:
- Java JDK 8 или выше (рекомендуется JDK 17)
- Gradle 8.0+ (включен в проект)
- Android SDK с минимум API 23 (Android 6.0)
- Минимум 4 ГБ RAM
- Минимум 10 ГБ свободного места

### Проверка версии Java:
```bash
java -version
```

Если Java не установлена, скачайте и установите:
- OpenJDK: https://adoptium.net/
- Oracle JDK: https://www.oracle.com/java/technologies/downloads/

## Возможные проблемы

### Ошибка: "SDK location not found"
**Решение**: Создайте файл `local.properties` с путем к SDK

### Ошибка: "Gradle sync failed"
**Решение**: 
1. Проверьте подключение к интернету
2. Удалите папку `.gradle` в домашней директории
3. Запустите сборку заново

### Ошибка: "Unsupported class file version"
**Решение**: Обновите Java до версии 17 или выше

### Ошибка: "License not accepted"
**Решение**: 
```bash
sdkmanager --licenses
```
Примите все лицензии

## Дополнительная информация

### Создание Release версии (подписанной):

1. Создайте keystore:
```bash
keytool -genkey -v -keystore my-release-key.keystore -alias my-key-alias -keyalg RSA -keysize 2048 -validity 10000
```

2. Добавьте в `app/build.gradle`:
```gradle
android {
    signingConfigs {
        release {
            storeFile file("../my-release-key.keystore")
            storePassword "your-password"
            keyAlias "my-key-alias"
            keyPassword "your-password"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

3. Соберите:
```bash
gradlew assembleRelease
```

## Поддержка

При возникновении проблем:
1. Проверьте версию Java: `java -version`
2. Проверьте версию Gradle: `gradlew --version`
3. Очистите кэш: `gradlew clean`
4. Проверьте логи в папке `build/`

---

**Удачной сборки!** 🚀

