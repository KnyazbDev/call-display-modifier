# 🌐 Call Display Modifier - Remote Control Server

Backend сервер с админ-панелью для удаленного управления Android приложением.

## ⚠️ ТОЛЬКО ДЛЯ ОБРАЗОВАТЕЛЬНЫХ ЦЕЛЕЙ!

---

## 🚀 Быстрый старт

```bash
# Установите зависимости
npm install

# Запустите сервер
npm start

# Сервер запустится на http://localhost:3000
```

## 📂 Структура

```
server/
├── server.js           # Основной сервер
├── package.json        # Зависимости
├── database.db         # SQLite база (создается автоматически)
└── public/            # Веб админ-панель
    ├── index.html     # HTML
    ├── style.css      # CSS
    └── script.js      # JavaScript
```

## 🔗 Endpoints

### Client API
- `POST /api/client/register` - Регистрация устройства
- `GET /api/client/:id/rules` - Получить правила
- `GET /api/client/:id/rule/:number` - Проверить правило

### Admin API  
- `GET /api/admin/clients` - Список клиентов
- `POST /api/admin/rules` - Создать правило
- `DELETE /api/admin/rules/:id` - Удалить правило

## 📊 База данных

SQLite с 2 таблицами:
- `clients` - зарегистрированные устройства
- `rules` - правила замены номеров

## 🌐 Админ-панель

Откройте http://localhost:3000 в браузере

Функции:
- ✅ Просмотр всех клиентов
- ✅ Создание правил замены
- ✅ Удаление правил и клиентов
- ✅ Статистика в реальном времени

## 📖 Полная документация

См. `REMOTE_CONTROL_SETUP.md` в корне проекта

