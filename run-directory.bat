@echo off
REM Run Directory Server

echo Starting Directory Server...
echo TCP Port: 8080 (for vinyl server registrations)
echo UDP Port: 8081 (for client lookups)
echo.

java -cp "build\common;build\directory" com.vinylsystem.directory.DirectoryServer