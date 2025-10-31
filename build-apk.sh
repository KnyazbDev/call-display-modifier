#!/bin/bash
# Bash скрипт для автоматической сборки APK (Linux/Mac)
# Call Display Modifier Build Script

echo "========================================"
echo "  Call Display Modifier - Build Script"
echo "========================================"
echo ""

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Проверка наличия Java
echo -e "${YELLOW}[1/5] Проверка Java...${NC}"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo -e "${GREEN}  ✓ Java обнаружена: $JAVA_VERSION${NC}"
else
    echo -e "${RED}  ✗ Java не установлена!${NC}"
    echo -e "${YELLOW}  Скачайте Java с: https://adoptium.net/${NC}"
    exit 1
fi

# Проверка local.properties
echo ""
echo -e "${YELLOW}[2/5] Проверка конфигурации SDK...${NC}"
if [ -f "local.properties" ]; then
    echo -e "${GREEN}  ✓ Файл local.properties найден${NC}"
    SDK_PATH=$(grep "sdk.dir" local.properties)
    echo -e "  SDK Path: $SDK_PATH"
else
    echo -e "${RED}  ✗ Файл local.properties не найден!${NC}"
    echo ""
    echo -e "${YELLOW}  Создайте файл 'local.properties' в корне проекта:${NC}"
    echo -e "${CYAN}  sdk.dir=/path/to/Android/Sdk${NC}"
    echo ""
    
    # Попытка автоматически найти SDK
    POSSIBLE_PATHS=(
        "$HOME/Android/Sdk"
        "$HOME/Library/Android/sdk"
        "$ANDROID_HOME"
        "$ANDROID_SDK_ROOT"
        "/usr/local/android-sdk"
    )
    
    for path in "${POSSIBLE_PATHS[@]}"; do
        if [ -d "$path" ]; then
            echo -e "${GREEN}  Найден возможный путь к SDK: $path${NC}"
            echo "sdk.dir=$path" > local.properties
            echo -e "${GREEN}  ✓ Файл local.properties создан автоматически${NC}"
            break
        fi
    done
    
    if [ ! -f "local.properties" ]; then
        echo -e "${YELLOW}  Пожалуйста, создайте файл вручную и запустите скрипт снова.${NC}"
        exit 1
    fi
fi

# Очистка предыдущей сборки
echo ""
echo -e "${YELLOW}[3/5] Очистка предыдущей сборки...${NC}"
if [ -d "app/build" ]; then
    rm -rf app/build
    echo -e "${GREEN}  ✓ Очистка завершена${NC}"
else
    echo -e "${GREEN}  ✓ Чистый проект${NC}"
fi

# Сделать gradlew исполняемым
chmod +x gradlew

# Сборка проекта
echo ""
echo -e "${YELLOW}[4/5] Сборка APK (это может занять несколько минут)...${NC}"
echo -e "  Пожалуйста, подождите..."
echo ""

./gradlew clean assembleDebug --stacktrace

if [ $? -eq 0 ]; then
    echo -e "${GREEN}  ✓ Сборка успешно завершена!${NC}"
else
    echo -e "${RED}  ✗ Ошибка при сборке${NC}"
    echo -e "${YELLOW}  Проверьте лог выше для деталей${NC}"
    exit 1
fi

# Проверка результата
echo ""
echo -e "${YELLOW}[5/5] Проверка результата...${NC}"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo -e "${GREEN}  ✓ APK создан успешно!${NC}"
    echo -e "  Размер: $APK_SIZE"
    echo -e "${CYAN}  Путь: $APK_PATH${NC}"
    
    # Копирование в корень для удобства
    cp "$APK_PATH" "CallDisplayModifier.apk"
    echo -e "${GREEN}  ✓ APK скопирован в корень проекта: CallDisplayModifier.apk${NC}"
else
    echo -e "${RED}  ✗ APK файл не найден!${NC}"
    exit 1
fi

echo ""
echo "========================================"
echo -e "${GREEN}         СБОРКА ЗАВЕРШЕНА УСПЕШНО!${NC}"
echo "========================================"
echo ""
echo -e "${YELLOW}Установите APK на устройство:${NC}"
echo -e "${CYAN}  adb install CallDisplayModifier.apk${NC}"
echo ""
echo -e "${YELLOW}или скопируйте CallDisplayModifier.apk на устройство вручную${NC}"
echo ""

