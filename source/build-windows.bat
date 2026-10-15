@echo off
cd /d "%~dp0"
call gradlew.bat clean build
if errorlevel 1 (
  echo Build failed. Copy the error above when asking for help.
) else (
  echo Finished. Find the mod JAR in build\libs.
)
pause
