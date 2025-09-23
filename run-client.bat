@echo off
REM Run Vinyl Client  
REM Simple usage: run-client.bat [server-name] [mode]
REM Uses sensible defaults: lookup mode, connects to localhost:8081

set SERVER_NAME=%1
set MODE=%2
if "%SERVER_NAME%"=="" set SERVER_NAME=defaultserver.group1.pro2x
if "%MODE%"=="" set MODE=lookup

echo Starting Vinyl Client...
echo Looking for server: %SERVER_NAME%
echo Directory: localhost:8081
echo Mode: %MODE%
echo.

java -cp "build" com.vinylsystem.client.VinylClient %SERVER_NAME% localhost 8081 %MODE%