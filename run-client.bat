@echo off
REM Run Vinyl Client
REM Usage: run-client.bat [server-name] [directory-ip] [directory-port] [mode]

if "%1"=="" (
    echo Usage: run-client.bat ^<server-name^> [directory-ip] [directory-port] [mode]
    echo Example: run-client.bat myserver.group1.pro2x localhost 8081 interactive
    echo.
    echo Modes: lookup, connect, message, interactive
    echo Server name format: ^<string^>.group#.pro2[x^|y]
    exit /b 1
)

echo Starting Vinyl Client...
echo Looking for server: %1
echo Directory: %2:%3 (defaults: localhost:8081)
echo Mode: %4 (default: lookup)
echo.

java -cp "build\common;build\client" com.vinylsystem.client.VinylClient %*