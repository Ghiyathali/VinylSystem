@echo off
REM Gentle shutdown for Vinyl System

echo Vinyl System - Gentle Shutdown
echo.
echo This will attempt to close components gracefully.
echo For each running terminal window with a Java process:
echo.
echo 1. Switch to the terminal window
echo 2. Press Ctrl+C to stop the process
echo 3. If prompted "Terminate batch job (Y/N)?", press Y
echo.

echo Current Java processes:
tasklist | findstr java
echo.

echo After manually stopping all processes, you can verify with:
echo   .\stop-all.bat
echo.
pause