@echo off
title Frente de Caixa

REM === Limpa as variaveis para ignorar configuracoes quebradas do Windows ===
set JAVA_HOME=
set MAVEN_HOME=

REM === Define o Java automaticamente (usando os que aparecem no seu print) ===
for /d %%i in ("C:\Program Files\Java\jdk-21*") do set JAVA_HOME=%%i
if not defined JAVA_HOME (
    for /d %%i in ("C:\Program Files\Java\jdk-25*") do set JAVA_HOME=%%i
)

REM === Detecta Maven automaticamente (baseado na pasta que vc tentou no PowerShell) ===
for /d %%i in ("C:\Program Files\apache-maven-*") do set MAVEN_HOME=%%i
if not defined MAVEN_HOME (
    for /d %%i in ("C:\tools\apache-maven-*") do set MAVEN_HOME=%%i
)

REM === Valida se os arquivos executaveis realmente existem la dentro ===
if not exist "%JAVA_HOME%\bin\java.exe" (
    echo [ERRO] Executavel do Java nao encontrado em: %JAVA_HOME%\bin
    pause
    exit /b 1
)
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo [ERRO] Executavel do Maven nao encontrado em: %MAVEN_HOME%\bin
    echo Verifique se a pasta apache-maven nao ficou duplicada ao extrair o ZIP.
    pause
    exit /b 1
)

REM === Monta o PATH novo ===
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo ========================================
echo  JAVA_HOME: %JAVA_HOME%
echo  MAVEN_HOME: %MAVEN_HOME%
echo ========================================

cd /d %~dp0
echo Iniciando Frente de Caixa...

REM Usar "call" e fundamental para rodar comandos Maven dentro de um .bat
call mvn javafx:run

pause