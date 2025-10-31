# Установка Android SDK - Пошаговая инструкция

## Проблема
```
bash: sdkmanager: command not found
```
Это означает, что Android SDK не установлен или не добавлен в PATH.

## Решение 1: Установка Android Studio (Самый простой способ)

### Шаги:

1. **Скачайте Android Studio**
   - Откройте: https://developer.android.com/studio
   - Скачайте установщик для вашей ОС
   - Размер: ~1 GB

2. **Установите Android Studio**
   ```bash
   # Windows: Запустите .exe файл
   # Linux: 
   sudo tar -xzf android-studio-*.tar.gz -C /opt/
   cd /opt/android-studio/bin
   ./studio.sh
   ```

3. **При первом запуске**
   - Выберите "Standard" installation
   - Дождитесь загрузки SDK компонентов
   - SDK установится автоматически в:
     - Windows: `C:\Users\YourName\AppData\Local\Android\Sdk`
     - Linux: `~/Android/Sdk`
     - Mac: `~/Library/Android/sdk`

4. **Установите дополнительные компоненты**
   - Откройте: Tools → SDK Manager
   - Отметьте:
     - ✅ Android SDK Platform 34
     - ✅ Android SDK Platform 23
     - ✅ Android SDK Build-Tools 34.0.0
     - ✅ Android SDK Command-line Tools
     - ✅ Android SDK Platform-Tools
   - Нажмите "Apply" и дождитесь установки

5. **Настройте проект**
   ```bash
   cd /e/podmena
   
   # Создайте local.properties
   # Windows (если SDK в стандартном месте):
   echo "sdk.dir=C:\\Users\\$(whoami)\\AppData\\Local\\Android\\Sdk" > local.properties
   
   # Linux:
   echo "sdk.dir=$HOME/Android/Sdk" > local.properties
   ```

6. **Соберите проект**
   ```bash
   ./gradlew clean assembleDebug
   ```

---

## Решение 2: Command Line Tools (Без Android Studio)

### Для Linux/WSL/Git Bash:

1. **Скачайте Command Line Tools**
   ```bash
   # Создайте директорию для SDK
   mkdir -p ~/Android/Sdk
   cd ~/Android/Sdk
   
   # Скачайте tools (замените URL на актуальный)
   wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
   
   # Или для Windows в Git Bash:
   # wget https://dl.google.com/android/repository/commandlinetools-win-9477386_latest.zip
   
   unzip commandlinetools-*.zip
   mkdir -p cmdline-tools/latest
   mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null || true
   ```

2. **Добавьте в PATH**
   ```bash
   # Добавьте в ~/.bashrc или ~/.bash_profile
   echo 'export ANDROID_HOME=$HOME/Android/Sdk' >> ~/.bashrc
   echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin' >> ~/.bashrc
   echo 'export PATH=$PATH:$ANDROID_HOME/platform-tools' >> ~/.bashrc
   source ~/.bashrc
   ```

3. **Установите компоненты**
   ```bash
   sdkmanager --sdk_root=$ANDROID_HOME "platform-tools" "platforms;android-34" "build-tools;34.0.0" "platforms;android-23"
   
   # Примите лицензии
   sdkmanager --sdk_root=$ANDROID_HOME --licenses
   ```

4. **Настройте проект**
   ```bash
   cd /e/podmena
   echo "sdk.dir=$HOME/Android/Sdk" > local.properties
   ```

5. **Соберите проект**
   ```bash
   chmod +x gradlew
   ./gradlew clean assembleDebug
   ```

---

## Решение 3: Использование Docker (Для продвинутых)

```dockerfile
# Создайте Dockerfile
FROM openjdk:17-slim

# Установите Android SDK
RUN apt-get update && apt-get install -y wget unzip

ENV ANDROID_HOME=/opt/android-sdk
RUN mkdir -p $ANDROID_HOME/cmdline-tools
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
RUN unzip commandlinetools-linux-*.zip -d $ANDROID_HOME/cmdline-tools
RUN mv $ANDROID_HOME/cmdline-tools/cmdline-tools $ANDROID_HOME/cmdline-tools/latest

ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

RUN yes | sdkmanager --licenses
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app
COPY . .

RUN ./gradlew assembleDebug
```

```bash
# Соберите в Docker
docker build -t android-builder .
docker run -v $(pwd):/app android-builder
```

---

## Решение 4: Быстрый способ для WSL/Git Bash (Windows)

Если вы используете Windows, проще собрать через PowerShell:

```bash
# Закройте bash и откройте PowerShell
exit
```

Затем в PowerShell:
```powershell
cd E:\podmena
.\build-apk.ps1
```

Скрипт автоматически найдет SDK или предложит его установить.

---

## Решение 5: Открыть проект в Android Studio

Самый простой способ:

1. Установите Android Studio (см. Решение 1)
2. Откройте проект:
   - File → Open
   - Выберите папку `E:\podmena`
3. Дождитесь синхронизации Gradle
4. Build → Build Bundle(s) / APK(s) → Build APK(s)

APK будет в: `app/build/outputs/apk/debug/app-debug.apk`

---

## Проверка установки

После установки SDK проверьте:

```bash
# Проверьте sdkmanager
sdkmanager --version

# Проверьте Java
java -version

# Проверьте переменные окружения
echo $ANDROID_HOME
echo $PATH | grep android
```

Должно показать версию sdkmanager и путь к Android SDK.

---

## Частые проблемы

### "sdkmanager: command not found"
- SDK не установлен или не в PATH
- Решение: Установите SDK и добавьте в PATH (см. выше)

### "ANDROID_HOME not set"
```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
```

### "License not accepted"
```bash
sdkmanager --licenses
# Нажимайте 'y' для всех лицензий
```

### Git Bash на Windows
Если используете Git Bash, проще переключиться на PowerShell:
```bash
powershell.exe
cd E:\podmena
.\build-apk.ps1
```

---

## Текущая ситуация

Судя по вашему терминалу:
- Вы используете bash (возможно Git Bash или WSL)
- SDK не установлен
- Путь: `/e/podmena` (Windows диск E:)

### Рекомендую:

**Вариант A (быстрый):**
```bash
exit  # Выйдите из bash
```
Затем в PowerShell:
```powershell
cd E:\podmena
.\build-apk.ps1
```

**Вариант B (полноценный):**
1. Установите Android Studio
2. Откройте проект в Android Studio
3. Нажмите Build APK

**Вариант C (для опытных):**
1. Установите Command Line Tools (см. Решение 2)
2. Настройте PATH
3. Запустите `./gradlew assembleDebug`

---

## Следующие шаги

После установки SDK:

```bash
# 1. Проверьте Java
java -version

# 2. Настройте local.properties
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# 3. Соберите проект
./gradlew clean assembleDebug

# 4. APK будет здесь:
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

---

## Нужна помощь?

Напишите в терминале:
```bash
# Проверьте вашу систему
uname -a
echo $SHELL
which java
echo $ANDROID_HOME
```

И я помогу с более точными инструкциями!

