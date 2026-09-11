@echo off
title Bateria de Tests POO - Java 21
echo ===================================================
echo   Compilando y Ejecutando Suite de Pruebas POO
echo ===================================================
javac -d out/test -sourcepath "src/main/java;src/test/java" src/test/java/com/juego/PruebasDominioTest.java
if %ERRORLEVEL% NEQ 0 (
    echo Error de compilacion en los tests.
    pause
    exit /b %ERRORLEVEL%
)
java -cp "out/production;out/test" com.juego.PruebasDominioTest
echo.
pause
