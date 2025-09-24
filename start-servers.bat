@echo off
echo Starting VinylSystem Servers...
echo.

echo Starting Directory Server...
start "Directory Server" java -cp "build" com.vinylsystem.directory.DirectoryServer

echo Waiting 3 seconds...
timeout /t 3 /nobreak >nul

echo Starting Vinyl Server with Discogs API...
start "Vinyl Server" java -cp "build" com.vinylsystem.server.VinylServer rock.group1.pro2x 127.0.0.1 9001

echo Waiting 5 seconds for servers to initialize...
timeout /t 5 /nobreak >nul

echo.
echo === SERVERS STARTED! ===
echo Directory Server: TCP 8080, UDP 8081
echo Vinyl Server: TCP 9001 (with Discogs API)
echo.
pause