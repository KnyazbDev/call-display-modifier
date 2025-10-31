# Find Android SDK Script
Write-Host "Searching for Android SDK..." -ForegroundColor Yellow
Write-Host ""

# Common SDK locations
$locations = @(
    "$env:LOCALAPPDATA\Android\Sdk",
    "$env:USERPROFILE\AppData\Local\Android\Sdk",
    "C:\Android\Sdk",
    "C:\Program Files\Android\android-sdk",
    "C:\Program Files (x86)\Android\android-sdk",
    "$env:ANDROID_HOME",
    "$env:ANDROID_SDK_ROOT"
)

$found = @()

foreach ($path in $locations) {
    if ($path -and (Test-Path $path)) {
        # Check if it's a real Android SDK
        $hasPlatforms = Test-Path "$path\platforms"
        $hasBuildTools = Test-Path "$path\build-tools"
        $hasPlatformTools = Test-Path "$path\platform-tools"
        
        if ($hasPlatforms -or $hasBuildTools -or $hasPlatformTools) {
            Write-Host "[FOUND] $path" -ForegroundColor Green
            Write-Host "  platforms:      $hasPlatforms" -ForegroundColor Gray
            Write-Host "  build-tools:    $hasBuildTools" -ForegroundColor Gray
            Write-Host "  platform-tools: $hasPlatformTools" -ForegroundColor Gray
            Write-Host ""
            $found += $path
        }
    }
}

if ($found.Count -eq 0) {
    Write-Host "[NOT FOUND] Android SDK not installed!" -ForegroundColor Red
    Write-Host ""
    Write-Host "OPTIONS:" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "1. Install Android Studio (Recommended)" -ForegroundColor Cyan
    Write-Host "   Download: https://developer.android.com/studio" -ForegroundColor Gray
    Write-Host "   SDK will be installed automatically" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Or check C:\Android\SDK contents:" -ForegroundColor Cyan
    dir C:\Android\SDK -ErrorAction SilentlyContinue | Format-Table Name
    Write-Host ""
    Write-Host "This doesn't look like Android SDK." -ForegroundColor Yellow
    Write-Host "Real Android SDK should have: platforms/, build-tools/, platform-tools/" -ForegroundColor Gray
} else {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "Found $($found.Count) Android SDK location(s)" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Use this path in local.properties:" -ForegroundColor Yellow
    Write-Host ""
    $bestPath = $found[0] -replace '\\', '/'
    Write-Host "sdk.dir=$bestPath" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Creating local.properties..." -ForegroundColor Yellow
    "sdk.dir=$bestPath" | Out-File -FilePath "local.properties" -Encoding UTF8 -NoNewline
    Write-Host "[OK] local.properties created!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Now run: .\build.ps1" -ForegroundColor Yellow
}

