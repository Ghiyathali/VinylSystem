@echo off
REM Run Vinyl Server
REM Usage: run-server.bat [server-name] [server-ip] [server-port] [directory-ip] [directory-port] [ttl]

if "%1"=="" (
    echo Usage: run-server.bat ^<server-name^> ^<server-ip^> ^<server-port^> [directory-ip] [directory-port] [ttl]
    echo Example: run-server.bat myserver.group1.pro2x 192.168.1.100 9090
    echo.
    echo Server name format: ^<string^>.group#.pro2[x^|y]
    exit /b 1
)

echo Starting Vinyl Server: %1
echo Server IP: %2:%3
echo Directory: %4:%5 (defaults: localhost:8080)
echo.

java -cp "build\common;build\server" com.vinylsystem.server.VinylServer %*