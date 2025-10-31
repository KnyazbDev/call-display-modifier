# Как собрать APK БЕЗ установки Android SDK

## 🚀 Онлайн-сервисы для сборки APK

Если у вас нет возможности установить Android SDK, используйте эти методы:

---

## Метод 1: GitHub Actions (Бесплатно, Рекомендуется)

### Шаги:

1. **Создайте GitHub репозиторий**
   ```bash
   cd /e/podmena
   git init
   git add .
   git commit -m "Initial commit"
   ```

2. **Создайте workflow файл**
   ```bash
   mkdir -p .github/workflows
   ```

3. **Создайте `.github/workflows/build.yml`:**

```yaml
name: Android Build

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'adopt'
        
    - name: Setup Android SDK
      uses: android-actions/setup-android@v2
      
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build APK
      run: ./gradlew assembleDebug
      
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

4. **Загрузите на GitHub**
   ```bash
   # Создайте репозиторий на github.com
   git remote add origin https://github.com/yourusername/podmena.git
   git push -u origin main
   ```

5. **Скачайте APK**
   - Перейдите на GitHub → Actions
   - Дождитесь завершения сборки
   - Скачайте APK из Artifacts

---

## Метод 2: Appetize.io (Онлайн эмулятор)

1. Зарегистрируйтесь на https://appetize.io
2. Загрузите исходники проекта
3. Сервис автоматически соберет APK
4. Тестируйте прямо в браузере

---

## Метод 3: Использование Docker (Не требует установки SDK)

### Создайте файл `Dockerfile`:

```dockerfile
FROM gradle:7.6-jdk17

# Установка Android SDK
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

RUN apt-get update && apt-get install -y wget unzip

# Скачиваем Command Line Tools
RUN mkdir -p $ANDROID_HOME/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip -q commandlinetools-linux-*.zip -d $ANDROID_HOME/cmdline-tools && \
    mv $ANDROID_HOME/cmdline-tools/cmdline-tools $ANDROID_HOME/cmdline-tools/latest && \
    rm commandlinetools-linux-*.zip

# Принимаем лицензии и устанавливаем компоненты
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-34" "platforms;android-23" "build-tools;34.0.0"

WORKDIR /app
COPY . .

# Создаем local.properties
RUN echo "sdk.dir=$ANDROID_HOME" > local.properties

# Собираем APK
RUN chmod +x gradlew && ./gradlew assembleDebug

CMD ["cp", "app/build/outputs/apk/debug/app-debug.apk", "/output/"]
```

### Соберите:

```bash
# Соберите образ
docker build -t android-build .

# Соберите APK
docker run -v $(pwd)/output:/output android-build

# APK будет в папке output/
```

---

## Метод 4: Replit (Онлайн IDE)

1. Зайдите на https://replit.com
2. Создайте новый Repl
3. Загрузите файлы проекта
4. Установите Java и Android SDK через nix
5. Соберите проект

---

## Метод 5: CodeSandbox + Android Build

1. Зайдите на https://codesandbox.io
2. Создайте контейнер с Java
3. Загрузите проект
4. Используйте встроенный терминал для сборки

---

## Метод 6: Попросить кого-то собрать

### Самый простой вариант:

1. **Загрузите проект на Google Drive / Dropbox**
2. **Попросите друга с Android Studio:**
   - Открыть проект
   - Build → Build APK
   - Отправить вам app-debug.apk

---

## Метод 7: Виртуальная машина

### Используйте готовую VM с Android Studio:

1. **Скачайте VirtualBox**: https://www.virtualbox.org/
2. **Скачайте образ Android Development VM**:
   - Android-x86: https://www.android-x86.org/
   - Или готовый образ с Android Studio
3. **Импортируйте и соберите проект внутри VM**

---

## Метод 8: Облачный сервер

### AWS / Google Cloud / Azure:

```bash
# Создайте instance
# Установите на нем Android SDK
# Загрузите проект через scp
# Соберите и скачайте APK
```

Пример для AWS EC2:
```bash
ssh -i key.pem ubuntu@your-instance

# Установите Java
sudo apt update
sudo apt install openjdk-17-jdk -y

# Установите Android SDK
wget https://dl.google.com/android/repository/commandlinetools-linux-latest.zip
unzip commandlinetools-linux-latest.zip
mkdir -p ~/Android/Sdk/cmdline-tools/latest
mv cmdline-tools/* ~/Android/Sdk/cmdline-tools/latest/

export ANDROID_HOME=~/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin

sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Загрузите проект и соберите
cd podmena
./gradlew assembleDebug

# Скачайте APK
scp -i key.pem ubuntu@your-instance:~/podmena/app/build/outputs/apk/debug/app-debug.apk .
```

---

## Метод 9: Termux на Android (!)

Если у вас есть Android устройство:

```bash
# Установите Termux из F-Droid
# В Termux:
pkg install openjdk-17
pkg install gradle

# Клонируйте проект
git clone <your-repo>
cd podmena

# Соберите
./gradlew assembleDebug
```

APK соберется прямо на телефоне!

---

## Рекомендация для вашей ситуации

Судя по тому, что вы используете bash и проект в `/e/podmena`:

### Вариант 1 (Быстро - 5 минут):
```bash
# Выйдите из bash
exit
```

Откройте PowerShell:
```powershell
cd E:\podmena

# Установите Chocolatey (если нет)
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))

# Установите Android Studio
choco install androidstudio -y

# Перезапустите PowerShell, затем:
# Откройте Android Studio и импортируйте проект E:\podmena
```

### Вариант 2 (Онлайн - 10 минут):
1. Загрузите проект на GitHub
2. Настройте GitHub Actions (см. Метод 1)
3. Скачайте готовый APK

### Вариант 3 (Docker - 20 минут):
```bash
# Установите Docker Desktop для Windows
# Затем:
cd /e/podmena
docker build -t android-build -f- . << 'EOF'
FROM gradle:7.6-jdk17
ENV ANDROID_HOME=/opt/android-sdk
RUN apt-get update && apt-get install -y wget unzip
RUN mkdir -p $ANDROID_HOME/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip && \
    unzip -q commandlinetools-linux-*.zip -d $ANDROID_HOME/cmdline-tools && \
    mv $ANDROID_HOME/cmdline-tools/cmdline-tools $ANDROID_HOME/cmdline-tools/latest
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
WORKDIR /app
COPY . .
RUN echo "sdk.dir=$ANDROID_HOME" > local.properties && \
    chmod +x gradlew && \
    ./gradlew assembleDebug
EOF

docker run -v /e/podmena/output:/output android-build \
  cp app/build/outputs/apk/debug/app-debug.apk /output/
```

---

## Что выбрать?

| Метод | Сложность | Время | Требования |
|-------|-----------|-------|------------|
| Android Studio | ⭐ | 30 мин | 2GB скачать |
| GitHub Actions | ⭐⭐ | 10 мин | GitHub аккаунт |
| Docker | ⭐⭐⭐ | 20 мин | Docker Desktop |
| PowerShell скрипт | ⭐ | 5 мин | Windows |
| Termux | ⭐⭐ | 15 мин | Android устройство |

**Мой совет**: Используйте **Android Studio** - это самый надежный способ.

---

Какой метод вы хотите попробовать?

