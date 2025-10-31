# Сборка APK онлайн через GitHub Actions

Если вы не хотите устанавливать Android Studio, соберите APK онлайн!

## Шаги:

### 1. Создайте GitHub аккаунт
Если еще нет: https://github.com/signup

### 2. Создайте репозиторий
- Зайдите на https://github.com/new
- Имя: `call-display-modifier` (или любое другое)
- Тип: Public
- Нажмите "Create repository"

### 3. Загрузите проект на GitHub

В PowerShell выполните:

```powershell
cd E:\podmena

# Инициализируйте git (если еще не сделано)
git init

# Добавьте все файлы
git add .

# Создайте коммит
git commit -m "Initial commit - Call Display Modifier"

# Подключите к GitHub (замените YOUR_USERNAME на ваш логин)
git remote add origin https://github.com/YOUR_USERNAME/call-display-modifier.git

# Загрузите на GitHub
git branch -M main
git push -u origin main
```

При запросе введите:
- Username: ваш GitHub логин
- Password: [Personal Access Token](https://github.com/settings/tokens) (НЕ пароль!)

### 4. Дождитесь сборки

1. Перейдите в ваш репозиторий на GitHub
2. Откройте вкладку **"Actions"**
3. Дождитесь завершения сборки (~5-10 минут)
4. Зеленая галочка ✅ = сборка успешна

### 5. Скачайте APK

1. Нажмите на завершенную сборку
2. Прокрутите вниз до раздела **"Artifacts"**
3. Скачайте **"app-debug"**
4. Распакуйте ZIP - внутри будет `app-debug.apk`

## Готово! 🎉

Теперь у вас есть APK файл без установки Android Studio!

---

## Альтернатива: Ручная загрузка (без git)

1. Упакуйте проект в ZIP:
   ```powershell
   Compress-Archive -Path E:\podmena\* -DestinationPath E:\podmena.zip
   ```

2. Создайте репозиторий на GitHub

3. Нажмите "uploading an existing file"

4. Загрузите все файлы из проекта

5. Actions запустятся автоматически

---

## Преимущества этого метода:

✅ Не нужно устанавливать Android Studio  
✅ Не нужен мощный компьютер  
✅ Сборка в облаке (бесплатно)  
✅ Можно собирать из любого места  
✅ История всех сборок  

## Примечание

Файл `.github/workflows/build.yml` уже создан в вашем проекте!
Просто загрузите проект на GitHub, и сборка начнется автоматически.

