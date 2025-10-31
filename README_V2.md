# 📱 Call Display Modifier v2.0 - С удаленным управлением!

## 🌐 Remote Control System + Admin Panel

> ⚠️ **ВАЖНОЕ ПРЕДУПРЕЖДЕНИЕ**: Этот проект создан исключительно в образовательных целях для демонстрации Client-Server архитектуры, REST API и Android разработки. **НЕ ИСПОЛЬЗУЙТЕ** для мошенничества или обмана людей!

---

## ✨ Что нового в версии 2.0

### 🌐 Система удаленного управления:
- ✅ **Backend сервер** (Node.js + Express + SQLite)
- ✅ **Веб админ-панель** для управления правилами
- ✅ **REST API** для взаимодействия приложения с сервером
- ✅ **Автоматическая регистрация** устройств
- ✅ **Удаленное создание** правил замены номеров
- ✅ **Real-time синхронизация** между устройствами и сервером
- ✅ **Fallback режим** на локальные настройки при ошибке

### 📱 Обновленное Android приложение:
- ✅ Секция UI для подключения к серверу
- ✅ HTTP клиент (OkHttp + Gson)
- ✅ Регистрация и синхронизация с сервером
- ✅ Проверка подключения одной кнопкой
- ✅ Статус подключения в реальном времени
- ✅ Работает с сервером И автономно

---

## 🎯 Как это работает

```
┌─────────────────┐      HTTP REST API      ┌──────────────┐
│  Android App    │ ◄──────────────────────► │   Server     │
│   (Клиент)      │   JSON over HTTP         │  (Node.js)   │
└─────────────────┘                          └──────┬───────┘
     │                                               │
     │ При входящем звонке:                         │
     │ GET /api/client/{id}/rule/{number}          │
     │                                               ▼
     │                                       ┌──────────────┐
     │                                       │ Admin Panel  │
     │                                       │ (Web Browser)│
     │                                       └──────────────┘
     │                                               │
     └─────────────── Fallback на локальные ────────┘
              настройки при ошибке соединения
```

---

## 🚀 Быстрый старт

### Шаг 1: Запустите backend сервер

```bash
# Установите Node.js с https://nodejs.org/

# Перейдите в папку сервера
cd server

# Установите зависимости (первый раз)
npm install

# Запустите сервер
npm start
```

Сервер запустится на **http://localhost:3000**

Откройте админ-панель: **http://localhost:3000**

### Шаг 2: Соберите и установите APK

**Через GitHub Actions (рекомендуется):**
```bash
git add .
git commit -m "Add remote control system"
git push origin main
```

Затем скачайте APK из GitHub Actions → Artifacts → `call-display-modifier-v2.zip`

**Локально:**
```bash
.\build.ps1  # Windows
# или
./gradlew assembleDebug  # Linux/Mac
```

### Шаг 3: Настройте приложение

1. Установите APK на устройство
2. Предоставьте все разрешения
3. Включите как "Телефонное приложение"
4. В секции **🌐 Удаленное управление**:
   - Включите чекбокс "Удаленный режим"
   - Введите URL: `http://ВАШ_IP:3000` (например: `http://192.168.1.100:3000`)
   - Нажмите **"🔌 Проверить подключение"**
   - Нажмите **"📱 Регистрация"**

### Шаг 4: Создайте правила через админ-панель

1. Откройте админ-панель в браузере: `http://localhost:3000`
2. Найдите ваше устройство в списке
3. Нажмите на устройство
4. Создайте правило:
   - **Оригинальный номер**: `+79991234567`
   - **Отображаемый номер**: `+79000000001`
   - **Описание**: `Тестовое правило`
5. Нажмите **"➕ Добавить правило"**

### Шаг 5: Синхронизируйте и тестируйте

1. В приложении нажмите **"🔄 Синхронизация"**
2. Сделайте тестовый звонок с номера из правила
3. Должен появиться overlay с измененным номером!

---

## 📖 Документация

### 🌟 НАЧНИТЕ ЗДЕСЬ:
- **[HOW_TO_BUILD_APK.md](HOW_TO_BUILD_APK.md)** - Полная инструкция по сборке и настройке

### Детальные руководства:
- [REMOTE_CONTROL_SETUP.md](REMOTE_CONTROL_SETUP.md) - Развертывание системы удаленного управления
- [WHATS_NEW.md](WHATS_NEW.md) - Подробное описание изменений
- [BUILD_SYSTEM.md](BUILD_SYSTEM.md) - Сборка APK и сервера
- [server/README.md](server/README.md) - Документация backend сервера

### Оригинальная документация:
- [README.md](README.md) - Оригинальная документация v1.0
- [INSTALLATION.md](INSTALLATION.md) - Инструкция по установке
- [COURSEWORK_INFO.md](COURSEWORK_INFO.md) - Для защиты курсовой

---

## 🛠️ Технологии

### Backend:
- **Node.js** - JavaScript runtime
- **Express.js** 4.18 - Веб-фреймворк
- **better-sqlite3** - База данных
- **cors** - CORS middleware
- **uuid** - Генерация ID

### Frontend (Admin Panel):
- **HTML5** + **CSS3** + **JavaScript** (Vanilla)
- **Fetch API** - HTTP запросы
- **Material Design** - дизайн система

### Android App:
- **Java** - Язык программирования
- **Min SDK**: 23 (Android 6.0)
- **Target SDK**: 34 (Android 14)
- **OkHttp** 4.11.0 - HTTP клиент
- **Gson** 2.10.1 - JSON обработка
- **InCallService** - Мониторинг звонков
- **WindowManager** - Overlay система

---

## 📊 Архитектура

### Backend API Endpoints

**Для Android App:**
| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/api/client/register` | Регистрация устройства |
| GET | `/api/client/:id/rules` | Получить все правила |
| GET | `/api/client/:id/rule/:number` | Проверить правило для номера |

**Для Admin Panel:**
| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/api/admin/clients` | Список всех клиентов |
| GET | `/api/admin/clients/:id` | Данные клиента |
| POST | `/api/admin/rules` | Создать правило |
| PUT | `/api/admin/rules/:id` | Обновить правило |
| DELETE | `/api/admin/rules/:id` | Удалить правило |
| GET | `/api/admin/stats` | Статистика |

### Database Schema

**Таблица `clients`:**
```sql
id TEXT PRIMARY KEY              -- UUID клиента
device_id TEXT UNIQUE            -- Android ID
device_name TEXT                 -- Модель устройства
registered_at DATETIME           -- Дата регистрации
last_seen DATETIME               -- Последняя активность
```

**Таблица `rules`:**
```sql
id INTEGER PRIMARY KEY           -- ID правила
client_id TEXT                   -- Привязка к клиенту
original_number TEXT             -- Оригинальный номер
display_number TEXT              -- Отображаемый номер
description TEXT                 -- Описание
enabled INTEGER                  -- Активно (1/0)
created_at DATETIME              -- Дата создания
```

---

## 📱 Возможности приложения

### Локальный режим (по умолчанию):
- ✅ Работает без сервера
- ✅ Префикс/суффикс для номеров
- ✅ Быстрая настройка

### Удаленный режим (новое):
- ✅ Подключение к серверу
- ✅ Правила с сервера
- ✅ Удаленное управление
- ✅ Описания правил
- ✅ Fallback на локальный режим

### Общие функции:
- ✅ InCallService API
- ✅ Системные overlay
- ✅ Foreground services
- ✅ Runtime permissions
- ✅ Material Design UI
- ✅ Сохранение настроек

---

## 💻 Админ-панель

### Возможности:
- 👥 **Список клиентов** - все подключенные устройства
- 🟢 **Статус** - онлайн/офлайн в реальном времени
- 📋 **Правила** - создание и управление
- 🗑️ **Удаление** - правил и клиентов
- 📊 **Статистика** - активные клиенты, правила
- 🔄 **Auto-refresh** - обновление каждые 30 секунд

### Интерфейс:
- 🎨 Modern Material Design
- 📱 Responsive (адаптивный)
- 🚀 Быстрая загрузка
- ✨ Анимации
- 📝 Валидация форм

---

## 🎓 Для курсовой работы

### Что демонстрирует проект:

1. **Client-Server Architecture**
   - REST API дизайн
   - HTTP протокол
   - JSON обмен данными

2. **Backend Development**
   - Node.js + Express
   - SQLite базы данных
   - CRUD операции
   - API endpoints

3. **Frontend Development**
   - HTML/CSS/JavaScript
   - Async/Await
   - Fetch API
   - DOM manipulation

4. **Mobile Development**
   - Android Services
   - Network programming
   - HTTP клиенты
   - Async callbacks
   - JSON parsing

5. **System Design**
   - Модульность
   - Масштабируемость
   - Error handling
   - Fallback механизмы

### Демонстрация:
1. Запустить сервер
2. Показать админ-панель
3. Зарегистрировать устройство
4. Создать правило
5. Показать работу на телефоне

---

## 📂 Структура проекта

```
E:\podmena\
├── app/                              # Android приложение
│   ├── src/main/java/.../
│   │   ├── MainActivity.java         # ✅ UI для сервера
│   │   ├── ApiClient.java            # ✅ НОВОЕ: HTTP клиент
│   │   ├── CallMonitorService.java   # ✅ API интеграция
│   │   └── OverlayService.java       # ✅ Описания правил
│   ├── src/main/res/layout/
│   │   └── activity_main.xml         # ✅ Remote control UI
│   └── build.gradle                  # ✅ OkHttp + Gson
│
├── server/                           # ✅ НОВОЕ: Backend
│   ├── server.js                     # Node.js сервер
│   ├── package.json                  # Зависимости
│   └── public/                       # Admin Panel
│       ├── index.html                # HTML
│       ├── style.css                 # CSS
│       └── script.js                 # JavaScript
│
├── .github/workflows/
│   └── build.yml                     # ✅ GitHub Actions
│
└── Документация:
    ├── HOW_TO_BUILD_APK.md           # 🌟 НАЧНИТЕ ЗДЕСЬ
    ├── REMOTE_CONTROL_SETUP.md       # Полная инструкция
    ├── WHATS_NEW.md                  # Что нового
    └── BUILD_SYSTEM.md               # Сборка
```

---

## 🐛 Решение проблем

### "Connection error"
- Проверьте, что сервер запущен
- Убедитесь, что устройство и ПК в одной сети
- Проверьте URL в приложении

### "Client not registered"
- Нажмите кнопку "Регистрация"
- Проверьте подключение к интернету

### Правила не применяются
- Включите "Удаленный режим"
- Нажмите "Синхронизация"
- Проверьте формат номера

### APK не собирается
- `.\download-wrapper.ps1` - скачать Gradle wrapper
- `.\find-sdk.ps1` - проверить Android SDK
- См. [HOW_TO_BUILD_APK.md](HOW_TO_BUILD_APK.md)

---

## ⚠️ Предупреждение о безопасности

**Для продакшена потребуется:**
- 🔒 HTTPS (SSL/TLS)
- 🔒 JWT токены
- 🔒 API ключи
- 🔒 Rate limiting
- 🔒 Input validation
- 🔒 Логирование
- 🔒 Мониторинг

**Текущая реализация подходит ТОЛЬКО для:**
- ✅ Локальной разработки
- ✅ Обучения
- ✅ Демонстрации
- ✅ Курсовой работы

---

## 📝 License

Этот проект создан исключительно для образовательных целей.

---

## 🎯 Итог

Полноценная система удаленного управления:
- ✅ Backend сервер (Node.js)
- ✅ Admin Panel (Web)
- ✅ Android App (Java)
- ✅ REST API
- ✅ SQLite Database
- ✅ Real-time sync
- ✅ Material Design
- ✅ Документация

**Готово к демонстрации на защите курсовой! 🎓🚀**

---

## 📞 Контакты

Для вопросов по курсовой работе смотрите документацию в:
- [HOW_TO_BUILD_APK.md](HOW_TO_BUILD_APK.md)
- [REMOTE_CONTROL_SETUP.md](REMOTE_CONTROL_SETUP.md)
- [COURSEWORK_INFO.md](COURSEWORK_INFO.md)

**Удачи на защите! 🎓**

