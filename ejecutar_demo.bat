@echo off
title THEM BOTS - Combate en Vivo
echo 🤖 Compilando proyecto...
mvn compile -q

echo.
echo ▶️ Ejecutando combate épico...
echo.
mvn exec:java -Dexec.mainClass="dev.maiki.thembots.DemoSimple" -q

echo.
echo ✅ Combate finalizado. Presiona cualquier tecla para salir...
pause > nul