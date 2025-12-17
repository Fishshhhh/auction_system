@echo off
echo 正在清理端口占用...

echo 清理8080端口...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
    if not "%%a"=="" (
        echo 终止PID为 %%a 的进程...
        taskkill /PID %%a /F >nul 2>&1
    )
)

echo 清理3000端口...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3000') do (
    if not "%%a"=="" (
        echo 终止PID为 %%a 的进程...
        taskkill /PID %%a /F >nul 2>&1
    )
)

echo 端口清理完成！

echo 启动后端服务...
start "Backend" /D "D:\Desktop\Test1215" D:\program\apache-maven-3.8.6\bin\mvn.cmd spring-boot:run

timeout /t 10 /nobreak >nul

echo 启动前端服务...
start "Frontend" /D "D:\Desktop\Test1215\auction-frontend\public" cmd /c "python -m http.server 3000"

echo 项目启动完成！
echo 后端服务地址: http://localhost:8080
echo 前端服务地址: http://localhost:3000
echo 接口文档地址: http://localhost:8080/swagger-ui/index.html

pause