@echo off
chcp 65001
title 个人收支记账系统

echo ======================================
echo      个人收支记账系统启动脚本
echo ======================================
echo.

echo [INFO] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] 未找到Java环境，请安装JDK 8或以上版本！
    pause
    exit /b 1
)

echo [INFO] 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] 未找到Maven环境，请安装Maven并配置环境变量！
    echo [INFO] 正在尝试使用内嵌Maven...
    goto check_jar
)

echo [INFO] 正在启动项目...
echo [INFO] 请等待项目启动完成后，在浏览器中访问: http://localhost:8080
echo [INFO] 按 Ctrl+C 停止项目
echo.

mvn spring-boot:run
goto end

:check_jar
if exist "target\accounting-system-1.0.0.jar" (
    echo [INFO] 找到已打包的Jar文件，正在启动...
    java -jar target\accounting-system-1.0.0.jar
) else (
    echo [ERROR] 无法启动项目，请使用IDE（如IDEA）打开项目并运行！
    echo [INFO] 项目入口类: src/main/java/com/example/accounting/AccountingSystemApplication.java
    pause
)

:end
echo.
echo [INFO] 项目已停止
pause
