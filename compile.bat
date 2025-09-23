@echo off
REM Simple build script for Vinyl System

echo Building Vinyl System...

REM Create output directories
if not exist "build" mkdir build
if not exist "build\common" mkdir build\common
if not exist "build\directory" mkdir build\directory
if not exist "build\server" mkdir build\server
if not exist "build\client" mkdir build\client

echo.
echo [1/4] Compiling Common module...
javac -d build\common ^
      common\src\main\java\com\vinylsystem\common\*.java

if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to compile Common module
    exit /b 1
)

echo [2/4] Compiling Directory Server...
javac -cp build\common -d build\directory ^
      directory\src\main\java\com\vinylsystem\directory\*.java

if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to compile Directory Server
    exit /b 1
)

echo [3/4] Compiling Vinyl Server...
javac -cp build\common -d build\server ^
      server\src\main\java\com\vinylsystem\server\*.java

if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to compile Vinyl Server
    exit /b 1
)

echo [4/4] Compiling Vinyl Client...
javac -cp build\common -d build\client ^
      client\src\main\java\com\vinylsystem\client\*.java

if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to compile Vinyl Client
    exit /b 1
)

echo.
echo BUILD SUCCESSFUL!
echo.
echo To run the components:
echo   Directory Server: run-directory.bat
echo   Vinyl Server:     run-server.bat [name] [ip] [port]
echo   Vinyl Client:     run-client.bat [server-name] [mode]