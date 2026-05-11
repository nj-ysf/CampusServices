@echo off
REM Lance CampusServices via Maven (Windows)
REM Prérequis : Java 11+ et Maven installes
cd /d "%~dp0"
mvn javafx:run
pause
