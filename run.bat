@echo off
echo Compiling Java files with Oracle JDBC driver...
javac -cp "lib/ojdbc8.jar;." *.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Starting Gym Management System...
java -cp "lib/ojdbc8.jar;." Dashboard
