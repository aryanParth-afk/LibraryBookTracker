# ============================================================
# Library Book Tracker - Fix Structure, Compile & Run Script
# ============================================================

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

Write-Host ""
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  Library Book Tracker - Build & Run" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

# ── Step 1: Check Java ─────────────────────────────────────
Write-Host "[1/4] Checking Java installation..." -ForegroundColor Yellow
try {
    $javaVersion = & java -version 2>&1
    Write-Host "      Java found: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "  ERROR: Java is not installed or not on PATH." -ForegroundColor Red
    Write-Host "  Please install JDK 14+ from https://adoptium.net/" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# ── Step 2: Create proper package directory structure ───────
Write-Host ""
Write-Host "[2/4] Creating package directory structure..." -ForegroundColor Yellow

$srcRoot = "$projectRoot\src"

$dirs = @(
    "$srcRoot\library",
    "$srcRoot\library\model",
    "$srcRoot\library\service",
    "$srcRoot\library\ui"
)

foreach ($dir in $dirs) {
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir | Out-Null
        Write-Host "      Created: $dir" -ForegroundColor DarkGray
    }
}

# ── Step 3: Copy files into correct package directories ─────
Write-Host ""
Write-Host "[3/4] Placing source files into correct packages..." -ForegroundColor Yellow

$fileMappings = @{
    "Main.java"           = "$srcRoot\library\Main.java"
    "Book.java"           = "$srcRoot\library\model\Book.java"
    "LibraryService.java" = "$srcRoot\library\service\LibraryService.java"
    "MainWindow.java"     = "$srcRoot\library\ui\MainWindow.java"
    "BookTableModel.java" = "$srcRoot\library\ui\BookTableModel.java"
    "Theme.java"          = "$srcRoot\library\ui\Theme.java"
    "RoundedPanel.java"   = "$srcRoot\library\ui\RoundedPanel.java"
    "StyledButton.java"   = "$srcRoot\library\ui\StyledButton.java"
}

foreach ($file in $fileMappings.Keys) {
    $src  = "$projectRoot\$file"
    $dest = $fileMappings[$file]
    if (Test-Path $src) {
        Copy-Item -Path $src -Destination $dest -Force
        Write-Host "      Placed: $file -> $(Split-Path -Parent $dest | Split-Path -Leaf)\$(Split-Path -Leaf $dest)" -ForegroundColor DarkGray
    } else {
        Write-Host "      WARNING: $file not found in project root!" -ForegroundColor Yellow
    }
}

# ── Step 4: Compile ─────────────────────────────────────────
Write-Host ""
Write-Host "[4/4] Compiling..." -ForegroundColor Yellow

$outDir = "$projectRoot\out"
if (-not (Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir | Out-Null
}

$compileResult = & javac -d "$outDir" -sourcepath "$srcRoot" "$srcRoot\library\Main.java" 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "  COMPILATION FAILED:" -ForegroundColor Red
    Write-Host $compileResult -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
} else {
    Write-Host "      Compilation successful!" -ForegroundColor Green
}

# ── Run ────────────────────────────────────────────────────
Write-Host ""
Write-Host "==================================================" -ForegroundColor Green
Write-Host "  Launching Library Book Tracker..." -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green
Write-Host ""

& java -cp "$outDir" library.Main
