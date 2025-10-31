# 🏗️ Сборка системы

## 📦 Компиляция Android APK

### Вариант 1: Локальная сборка (Windows PowerShell)

```powershell
# Перейдите в корень проекта
cd E:\podmena

# Запустите скрипт сборки
.\build.ps1
```

APK будет создан: `app\build\outputs\apk\debug\app-debug.apk`

### Вариант 2: GitHub Actions (автоматическая сборка)

1. Push изменений в GitHub:
```bash
git add .
git commit -m "Add remote control system"
git push origin main
```

2. Перейдите: https://github.com/ВАШ_USERNAME/call-display-modifier/actions

3. Дождитесь завершения сборки (~5-7 минут)

4. Скачайте APK из артефактов

---

## 🌐 Запуск сервера

### Шаг 1: Установка Node.js (если не установлен)

**Windows:**
- Скачайте: https://nodejs.org/
- Установите LTS версию
- Проверьте: `node --version`

### Шаг 2: Установка зависимостей

```bash
cd server
npm install
```

### Шаг 3: Запуск

```bash
npm start
```

Сервер запустится на **http://localhost:3000**

---

## 🔧 Настройка Android приложения

После установки APK:

1. Откройте приложение
2. Предоставьте все разрешения
3. Включите как "Телефонное приложение"
4. В настройках приложения:
   - Включите "Удаленный режим" (когда будет готов UI)
   - Введите URL сервера: `http://192.168.1.X:3000` (ваш IP)
   - Нажмите "Регистрация"

---

## ✅ Проверка работы

1. **Сервер работает?**
   - Откройте: http://localhost:3000
   - Должна открыться админ-панель

2. **Устройство зарегистрировано?**
   - В админ-панели должно появиться ваше устройство

3. **Правило создано?**
   - Создайте тестовое правило через админ-панель

4. **Правило работает?**
   - Сделайте тестовый звонок
   - Должен появиться overlay с измененным номером

---

## 🐛 Решение проблем

### Сервер не запускается
```bash
# Убедитесь что Node.js установлен
node --version

# Переустановите зависимости
cd server
rm -rf node_modules
npm install
```

### APK не собирается
```bash
# Проверьте Java
java -version

# Скачайте gradle wrapper
.\download-wrapper.ps1

# Проверьте SDK
.\find-sdk.ps1
```

### Устройство не подключается к серверу
- Проверьте что устройство и компьютер в одной сети
- Используйте IP адрес компьютера, не localhost
- Проверьте firewall

---

## 📱 Узнать IP адрес компьютера

**Windows:**
```cmd
ipconfig
```
Ищите "IPv4 Address" для вашей сети

**Linux/Mac:**
```bash
ifconfig
# или
ip addr show
```

---

## 🚀 Быстрый старт для демонстрации

```bash
# Терминал 1: Запуск сервера
cd server
npm install
npm start

# Откройте браузер: http://localhost:3000

# Установите APK на устройство
# Зарегистрируйте устройство
# Создайте правило
# Сделайте тестовый звонок
```

---

## 📄 Файлы проекта

**Backend Server:**
- `server/server.js` - Основной сервер
- `server/package.json` - Зависимости
- `server/public/index.html` - Админ-панель HTML
- `server/public/style.css` - Админ-панель CSS
- `server/public/script.js` - Админ-панель JS

**Android App:**
- `app/src/main/java/com/calldisplaymodifier/app/ApiClient.java` - API клиент
- `app/src/main/java/com/calldisplaymodifier/app/CallMonitorService.java` - Сервис звонков
- `app/src/main/java/com/calldisplaymodifier/app/OverlayService.java` - Overlay сервис
- `app/build.gradle` - Зависимости (OkHttp, Gson)

**Документация:**
- `REMOTE_CONTROL_SETUP.md` - Полная инструкция
- `WHATS_NEW.md` - Что было создано
- `BUILD_SYSTEM.md` - Сборка (этот файл)

