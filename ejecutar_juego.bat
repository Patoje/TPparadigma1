@echo off
title Batalla Elemental - POO 2026
echo ===================================================
echo   Iniciando Batalla Elemental (Agua, Tierra, Fuego)
echo ===================================================
if exist JuegoElemental_nuevo.jar (
    move /y JuegoElemental_nuevo.jar JuegoElemental.jar >nul 2>&1
)
if exist JuegoElemental.jar (
    java -jar JuegoElemental.jar
) else (
    java -cp "out/production" com.juego.Main
)
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Ejecutando desde clases compiladas...
    java -cp "out/production" com.juego.Main
)
