# Android APK Build Script
# Call Display Modifier

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Android APK Build Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check Java
Write-Host "[1/5] Checking Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "  [OK] Java found: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "  [ERROR] Java not installed!" -ForegroundColor Red
    Write-Host "  Download Java from: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

# Check local.properties
Write-Host ""
Write-Host "[2/5] Checking SDK configuration..." -ForegroundColor Yellow
if (Test-Path "local.properties") {
    Write-Host "  [OK] local.properties found" -ForegroundColor Green
    $sdkPath = Get-Content "local.properties" | Select-String "sdk.dir"
    Write-Host "  SDK Path: $sdkPath" -ForegroundColor Gray
} else {
    Write-Host "  [WARNING] local.properties not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "  Searching for Android SDK..." -ForegroundColor Yellow
    
    # Try to find SDK
    $possiblePaths = @(
        "$env:LOCALAPPDATA\Android\Sdk",
        "C:\Android\Sdk",
        "$env:ANDROID_HOME",
        "$env:ANDROID_SDK_ROOT"
    )
    
    $foundPath = $null
    foreach ($path in $possiblePaths) {
        if ($path -and (Test-Path $path)) {
            Write-Host "  [OK] Found SDK at: $path" -ForegroundColor Green
            $foundPath = $path
            break
        }
    }
    
    if ($foundPath) {
        $escapedPath = $foundPath -replace '\\', '\\'
        "sdk.dir=$escapedPath" | Out-File -FilePath "local.properties" -Encoding UTF8
        Write-Host "  [OK] Created local.properties" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "  [ERROR] Android SDK not found!" -ForegroundColor Red
        Write-Host ""
        Write-Host "  Please install Android SDK:" -ForegroundColor Yellow
        Write-Host "  1. Download Android Studio: https://developer.android.com/studio" -ForegroundColor Cyan
        Write-Host "  2. Install and open Android Studio" -ForegroundColor Cyan
        Write-Host "  3. SDK will be installed automatically" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "  Or manually create 'local.properties' with:" -ForegroundColor Yellow
        Write-Host "  sdk.dir=C:\\Path\\To\\Android\\Sdk" -ForegroundColor Cyan
        Write-Host ""
        exit 1
    }
}

# Clean previous build
Write-Host ""
Write-Host "[3/5] Cleaning previous build..." -ForegroundColor Yellow
if (Test-Path "app\build") {
    Remove-Item -Path "app\build" -Recurse -Force -ErrorAction SilentlyContinue
    Write-Host "  [OK] Clean completed" -ForegroundColor Green
} else {
    Write-Host "  [OK] Clean project" -ForegroundColor Green
}

# Build APK
Write-Host ""
Write-Host "[4/5] Building APK (this may take several minutes)..." -ForegroundColor Yellow
Write-Host "  Please wait..." -ForegroundColor Gray
Write-Host ""

$buildProcess = Start-Process -FilePath ".\gradlew.bat" -ArgumentList "clean", "assembleDebug", "--stacktrace" -NoNewWindow -Wait -PassThru

if ($buildProcess.ExitCode -eq 0) {
    Write-Host ""
    Write-Host "  [OK] Build completed successfully!" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "  [ERROR] Build failed (exit code: $($buildProcess.ExitCode))" -ForegroundColor Red
    Write-Host "  Check the log above for details" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  Common issues:" -ForegroundColor Yellow
    Write-Host "  - SDK not properly configured" -ForegroundColor Gray
    Write-Host "  - Missing SDK components" -ForegroundColor Gray
    Write-Host "  - Internet connection required for first build" -ForegroundColor Gray
    Write-Host ""
    exit 1
}

# Check result
Write-Host ""
Write-Host "[5/5] Checking result..." -ForegroundColor Yellow
$apkPath = "app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $apkPath) {
    $apkSize = (Get-Item $apkPath).Length / 1MB
    Write-Host "  [OK] APK created successfully!" -ForegroundColor Green
    Write-Host "  Size: $([math]::Round($apkSize, 2)) MB" -ForegroundColor Gray
    Write-Host "  Path: $apkPath" -ForegroundColor Cyan
    
    # Copy to root
    Copy-Item $apkPath -Destination "CallDisplayModifier.apk" -Force
    Write-Host "  [OK] APK copied to: CallDisplayModifier.apk" -ForegroundColor Green
} else {
    Write-Host "  [ERROR] APK file not found!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "         BUILD SUCCESS!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Install APK on device:" -ForegroundColor Yellow
Write-Host "  adb install CallDisplayModifier.apk" -ForegroundColor Cyan
Write-Host ""
Write-Host "Or copy CallDisplayModifier.apk to device manually" -ForegroundColor Yellow
Write-Host ""

