# 🚀 Как запушить изменения в GitHub и получить APK

## ✅ Что нужно сделать

1. Добавить файлы в Git
2. Создать commit
3. Отправить в GitHub
4. Скачать APK из Actions

---

## 📝 Команды для PowerShell/Terminal

### Шаг 1: Перейдите в папку проекта

```powershell
cd E:\podmena
```

### Шаг 2: Проверьте статус

```bash
git status
```

Вы должны увидеть измененные файлы:
- `app/src/main/java/.../MainActivity.java`
- `app/src/main/java/.../ApiClient.java`
- `app/src/main/java/.../CallMonitorService.java`
- `app/src/main/java/.../OverlayService.java`
- `app/src/main/res/layout/activity_main.xml`
- `app/build.gradle`
- `server/...` (новые файлы)
- `.github/workflows/build.yml`

### Шаг 3: Добавьте все изменения

```bash
git add .
```

> **Примечание**: Точка `.` добавляет ВСЕ файлы. Для выборочного добавления используйте `git add путь/к/файлу`

### Шаг 4: Создайте commit с описанием

```bash
git commit -m "Add remote control system with admin panel v2.0

- Added Node.js backend server with REST API
- Added web admin panel for remote management
- Added ApiClient for HTTP communication
- Updated MainActivity with remote control UI
- Updated CallMonitorService to fetch rules from server
- Updated OverlayService to show rule descriptions
- Added OkHttp and Gson dependencies
- Updated GitHub Actions workflow
- Added comprehensive documentation"
```

### Шаг 5: Отправьте в GitHub

```bash
git push origin main
```

> **Примечание**: Если ваш branch называется `master`, используйте:
> ```bash
> git push origin master
> ```

---

## 📥 Как скачать APK после push

### Шаг 1: Откройте GitHub

Перейдите на страницу вашего репозитория:
```
https://github.com/ВАШ_USERNAME/call-display-modifier
```

### Шаг 2: Перейдите во вкладку Actions

Нажмите на вкладку **"Actions"** вверху страницы.

### Шаг 3: Найдите сборку

Вы увидите запущенную сборку с названием:
- **"Build Android APK with Remote Control"**
- Commit message: "Add remote control system..."

Статусы:
- 🟡 **Желтый кружок** = сборка идет (~5-7 минут)
- ✅ **Зеленая галочка** = успешно
- ❌ **Красный крестик** = ошибка

### Шаг 4: Откройте успешную сборку

Нажмите на сборку с зеленой галочкой ✅

### Шаг 5: Скачайте APK

1. Прокрутите вниз до раздела **"Artifacts"**
2. Найдите **"call-display-modifier-v2"**
3. Нажмите для скачивания (будет скачан ZIP архив)

### Шаг 6: Распакуйте и установите

1. Распакуйте скачанный ZIP
2. Внутри найдете файл **`app-debug.apk`**
3. Установите на Android устройство:
   - Через USB: `adb install app-debug.apk`
   - Через файловый менеджер: скопируйте на телефон и откройте

---

## 🔄 Если нужно внести изменения

### Вариант 1: Новый commit

```bash
# Сделайте изменения в файлах

git add .
git commit -m "Fix something"
git push origin main
```

### Вариант 2: Изменить последний commit

```bash
# Сделайте изменения в файлах

git add .
git commit --amend --no-edit
git push origin main --force
```

> ⚠️ **Внимание**: `--force` перезаписывает историю. Используйте осторожно!

---

## 🐛 Решение проблем

### Ошибка: "nothing to commit"

**Причина**: Нет изменений для commit

**Решение**: Проверьте статус и сделайте изменения
```bash
git status
```

### Ошибка: "refused to update"

**Причина**: Локальная версия отстает от удаленной

**Решение**:
```bash
git pull origin main
# Разрешите конфликты если есть
git push origin main
```

### Ошибка: "GitHub Actions failed"

**Причина**: Ошибка сборки

**Решение**:
1. Откройте failed сборку в Actions
2. Посмотрите логи
3. Исправьте ошибку
4. Сделайте новый commit и push

### Ошибка: "fatal: not a git repository"

**Причина**: Вы не в папке проекта

**Решение**:
```bash
cd E:\podmena
git status
```

---

## 📋 Быстрая шпаргалка

```bash
# 1. Перейти в проект
cd E:\podmena

# 2. Проверить статус
git status

# 3. Добавить файлы
git add .

# 4. Создать commit
git commit -m "Ваше сообщение"

# 5. Отправить в GitHub
git push origin main

# 6. Скачать APK:
# https://github.com/USERNAME/call-display-modifier/actions
```

---

## ✅ Checklist

Перед push:
- [ ] Все файлы добавлены (`git add .`)
- [ ] Commit создан с описанием
- [ ] Push выполнен успешно

После push:
- [ ] GitHub Actions запустились
- [ ] Сборка прошла успешно (✅)
- [ ] APK скачан из Artifacts
- [ ] APK распакован
- [ ] APK установлен на устройство

---

## 🎯 Готово!

После успешного push и скачивания APK:

1. ✅ Установите APK на телефон
2. ✅ Запустите сервер: `cd server && npm start`
3. ✅ Откройте админ-панель: http://localhost:3000
4. ✅ Настройте приложение
5. ✅ Зарегистрируйте устройство
6. ✅ Создайте правила
7. ✅ Тестируйте!

**См. [HOW_TO_BUILD_APK.md](HOW_TO_BUILD_APK.md) для детальных инструкций.**

---

## 📚 Дополнительные ресурсы

- [Git Basic Commands](https://git-scm.com/docs)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Android ADB Commands](https://developer.android.com/studio/command-line/adb)

**Удачи! 🚀**

