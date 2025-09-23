@echo off
REM Run Vinyl Server
REM Simple usage: run-server.bat [server-name]
REM Uses sensible defaults: localhost:9090, connects to localhost:8080

set SERVER_NAME=%1
if "%SERVER_NAME%"=="" set SERVER_NAME=defaultserver.group1.pro2x

echo Starting Vinyl Server: %SERVER_NAME%
echo Server IP: localhost:9090
echo Directory: localhost:8080
echo.

java -cp "build\common;build\server" com.vinylsystem.server.VinylServer %SERVER_NAME% localhost 9090