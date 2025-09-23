@echo off
REM Stop all Vinyl System components

echo Stopping Vinyl System...
echo.

echo [1/2] Killing all Java processes...
taskkill /f /im java.exe >nul 2>&1
if %errorlevel% == 0 (
    echo ✓ Java processes stopped
) else (
    echo ✓ No Java processes were running
)

echo.
echo [2/2] Checking port availability...
netstat -an | findstr "8080\|8081\|9090" >nul 2>&1
if %errorlevel% == 0 (
    echo ! Some ports may still be in use - they will be released shortly
) else (
    echo ✓ All ports are available
)

echo.
echo ✓ Vinyl System stopped successfully
echo.
echo You can now restart with:
echo   1. .\run-directory.bat
echo   2. .\run-server.bat  
echo   3. .\run-client.bat
echo.