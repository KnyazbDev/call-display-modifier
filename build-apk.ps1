# PowerShell скрипт для автоматической сборки APK
# Call Display Modifier Build Script

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Call Display Modifier - Build Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Проверка наличия Java
Write-Host "[1/5] Проверка Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "  ✓ Java обнаружена: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "  ✗ Java не установлена!" -ForegroundColor Red
    Write-Host "  Скачайте Java с: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

# Проверка local.properties
Write-Host ""
Write-Host "[2/5] Проверка конфигурации SDK..." -ForegroundColor Yellow
if (Test-Path "local.properties") {
    Write-Host "  ✓ Файл local.properties найден" -ForegroundColor Green
    $sdkPath = Get-Content "local.properties" | Select-String "sdk.dir"
    Write-Host "  SDK Path: $sdkPath" -ForegroundColor Gray
} else {
    Write-Host "  ✗ Файл local.properties не найден!" -ForegroundColor Red
    Write-Host ""
    Write-Host "  Создайте файл 'local.properties' в корне проекта:" -ForegroundColor Yellow
    Write-Host "  sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk" -ForegroundColor Cyan
    Write-Host ""
    
    # Попытка автоматически найти SDK
    $possiblePaths = @(
        "$env:LOCALAPPDATA\Android\Sdk",
        "C:\Android\Sdk",
        "$env:ANDROID_HOME",
        "$env:ANDROID_SDK_ROOT"
    )
    
    foreach ($path in $possiblePaths) {
        if ($path -and (Test-Path $path)) {
            Write-Host "  Найден возможный путь к SDK: $path" -ForegroundColor Green
            $escapedPath = $path -replace '\\', '\\'
            "sdk.dir=$escapedPath" | Out-File -FilePath "local.properties" -Encoding UTF8
            Write-Host "  ✓ Файл local.properties создан автоматически" -ForegroundColor Green
            break
        }
    }
    
    if (-not (Test-Path "local.properties")) {
        Write-Host "  Пожалуйста, создайте файл вручную и запустите скрипт снова." -ForegroundColor Yellow
        exit 1
    }
}

# Очистка предыдущей сборки
Write-Host ""
Write-Host "[3/5] Очистка предыдущей сборки..." -ForegroundColor Yellow
if (Test-Path "app\build") {
    Remove-Item -Path "app\build" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "  ✓ Очистка завершена" -ForegroundColor Green
} else {
    Write-Host "  ✓ Чистый проект" -ForegroundColor Green
}

# Сборка проекта
Write-Host ""
Write-Host "[4/5] Сборка APK (это может занять несколько минут)..." -ForegroundColor Yellow
Write-Host "  Пожалуйста, подождите..." -ForegroundColor Gray
Write-Host ""

$buildProcess = Start-Process -FilePath ".\gradlew.bat" -ArgumentList "clean", "assembleDebug", "--stacktrace" -NoNewWindow -Wait -PassThru

if ($buildProcess.ExitCode -eq 0) {
    Write-Host "  ✓ Сборка успешно завершена!" -ForegroundColor Green
} else {
    Write-Host "  ✗ Ошибка при сборке (код: $($buildProcess.ExitCode))" -ForegroundColor Red
    Write-Host "  Проверьте лог выше для деталей" -ForegroundColor Yellow
    exit 1
}

# Проверка результата
Write-Host ""
Write-Host "[5/5] Проверка результата..." -ForegroundColor Yellow
$apkPath = "app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $apkPath) {
    $apkSize = (Get-Item $apkPath).Length / 1MB
    Write-Host "  ✓ APK создан успешно!" -ForegroundColor Green
    Write-Host "  Размер: $([math]::Round($apkSize, 2)) MB" -ForegroundColor Gray
    Write-Host "  Путь: $apkPath" -ForegroundColor Cyan
    
    # Копирование в корень для удобства
    Copy-Item $apkPath -Destination "CallDisplayModifier.apk" -Force
    Write-Host "  ✓ APK скопирован в корень проекта: CallDisplayModifier.apk" -ForegroundColor Green
} else {
    Write-Host "  ✗ APK файл не найден!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "         СБОРКА ЗАВЕРШЕНА УСПЕШНО!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Установите APK на устройство:" -ForegroundColor Yellow
Write-Host "  adb install CallDisplayModifier.apk" -ForegroundColor Cyan
Write-Host ""
Write-Host "или скопируйте CallDisplayModifier.apk на устройство вручную" -ForegroundColor Yellow
Write-Host ""

