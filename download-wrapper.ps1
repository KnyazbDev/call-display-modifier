# Quick fix - Download gradle-wrapper.jar
Write-Host "Downloading gradle-wrapper.jar..." -ForegroundColor Yellow

$wrapperUrl = "https://raw.githubusercontent.com/gradle/gradle/v8.0.0/gradle/wrapper/gradle-wrapper.jar"
$wrapperPath = "gradle\wrapper\gradle-wrapper.jar"

try {
    New-Item -ItemType Directory -Force -Path "gradle\wrapper" | Out-Null
    Invoke-WebRequest -Uri $wrapperUrl -OutFile $wrapperPath -UseBasicParsing
    
    if (Test-Path $wrapperPath) {
        Write-Host "[OK] Downloaded successfully!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Now run: .\build.ps1" -ForegroundColor Cyan
    }
} catch {
    Write-Host "[ERROR] Download failed: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Alternative: Install Gradle" -ForegroundColor Yellow
    Write-Host "  choco install gradle" -ForegroundColor Cyan
}

