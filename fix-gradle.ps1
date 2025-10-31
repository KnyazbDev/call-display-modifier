# Fix Gradle Wrapper Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Fixing Gradle Wrapper" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if we have gradle installed globally
Write-Host "[1/3] Checking for Gradle..." -ForegroundColor Yellow
$gradleInstalled = Get-Command gradle -ErrorAction SilentlyContinue

if ($gradleInstalled) {
    Write-Host "  [OK] Found system Gradle" -ForegroundColor Green
    Write-Host ""
    Write-Host "[2/3] Generating Gradle Wrapper..." -ForegroundColor Yellow
    
    gradle wrapper --gradle-version 8.0
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK] Gradle wrapper generated!" -ForegroundColor Green
    } else {
        Write-Host "  [ERROR] Failed to generate wrapper" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "  [INFO] System Gradle not found" -ForegroundColor Yellow
    Write-Host "  [INFO] Downloading Gradle Wrapper JAR..." -ForegroundColor Yellow
    Write-Host ""
    
    # Download gradle-wrapper.jar
    $wrapperUrl = "https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar"
    $wrapperPath = "gradle\wrapper\gradle-wrapper.jar"
    
    Write-Host "[2/3] Downloading gradle-wrapper.jar..." -ForegroundColor Yellow
    try {
        # Create directory if not exists
        New-Item -ItemType Directory -Force -Path "gradle\wrapper" | Out-Null
        
        # Download file
        Invoke-WebRequest -Uri $wrapperUrl -OutFile $wrapperPath -UseBasicParsing
        Write-Host "  [OK] Downloaded gradle-wrapper.jar" -ForegroundColor Green
    } catch {
        Write-Host "  [ERROR] Failed to download: $_" -ForegroundColor Red
        Write-Host ""
        Write-Host "  Please install Gradle manually:" -ForegroundColor Yellow
        Write-Host "  1. Download: https://gradle.org/releases/" -ForegroundColor Cyan
        Write-Host "  2. Or use: choco install gradle" -ForegroundColor Cyan
        exit 1
    }
}

# Verify wrapper files
Write-Host ""
Write-Host "[3/3] Verifying Gradle Wrapper..." -ForegroundColor Yellow
$requiredFiles = @(
    "gradle\wrapper\gradle-wrapper.jar",
    "gradle\wrapper\gradle-wrapper.properties",
    "gradlew.bat"
)

$allExist = $true
foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "  [OK] $file" -ForegroundColor Green
    } else {
        Write-Host "  [MISSING] $file" -ForegroundColor Red
        $allExist = $false
    }
}

Write-Host ""
if ($allExist) {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "         GRADLE WRAPPER FIXED!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Now run: .\build.ps1" -ForegroundColor Yellow
} else {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "         SOME FILES MISSING" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Please install Android Studio and open the project" -ForegroundColor Yellow
    Write-Host "It will automatically fix the Gradle wrapper" -ForegroundColor Yellow
}
Write-Host ""

