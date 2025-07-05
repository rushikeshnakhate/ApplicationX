@echo off
setlocal enabledelayedexpansion

REM Java Docker Environment Manager for Windows
REM Usage: run.bat [command] [options]

REM Configuration
set IMAGE_NAME=applicationx-java21
set CONTAINER_NAME=applicationx-java-container
set VOLUME_NAME=applicationx-java-volume
set SSH_PORT=2222

REM Function to print colored output
:print_status
echo [INFO] %~1
goto :eof

:print_warning
echo [WARNING] %~1
goto :eof

:print_error
echo [ERROR] %~1
goto :eof

:print_header
echo ================================
echo %~1
echo ================================
goto :eof

REM Function to parse command line arguments
:parse_arguments
if "%~1"=="" (
    call :check_environment
    goto :eof
)

if "%~1"=="copy_repo" (
    if "%~2"=="" (
        call :print_error "Usage: %0 copy_repo ^<source_path^>"
        exit /b 1
    )
    call :copy_repo "%~2"
    goto :eof
)

if "%~1"=="create" (
    call :create_image_and_container
    goto :eof
)

if "%~1"=="start" (
    call :start_container
    goto :eof
)

if "%~1"=="stop" (
    call :stop_container
    goto :eof
)

if "%~1"=="restart" (
    call :restart_container
    goto :eof
)

if "%~1"=="remove" (
    call :remove_container_and_image
    goto :eof
)

if "%~1"=="shell" (
    call :shell_into_container
    goto :eof
)

if "%~1"=="build" (
    call :build_java_project
    goto :eof
)

if "%~1"=="logs" (
    call :show_logs
    goto :eof
)

if "%~1"=="status" (
    call :show_status
    goto :eof
)

if "%~1"=="help" (
    call :show_help
    goto :eof
)

call :print_error "Unknown command: %~1"
call :show_help
exit /b 1

REM Function to check if Docker image and container exist
:check_environment
call :print_header "Checking Docker Environment"

REM Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    call :print_error "Docker is not running. Please start Docker first."
    exit /b 1
)

REM Check if image exists
docker image inspect %IMAGE_NAME% >nul 2>&1
if errorlevel 1 (
    call :print_warning "Docker image '%IMAGE_NAME%' does not exist"
    call :print_status "Run '%0 create' to create the image and container"
    goto :eof
) else (
    call :print_status "Docker image '%IMAGE_NAME%' exists"
)

REM Check if container exists
docker ps -a --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if errorlevel 1 (
    call :print_warning "Docker container '%CONTAINER_NAME%' does not exist"
    call :print_status "Run '%0 create' to create the container"
    goto :eof
) else (
    call :print_status "Docker container '%CONTAINER_NAME%' exists"
    
    REM Check if container is running
    docker ps --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
    if errorlevel 1 (
        call :print_warning "Container exists but is not running"
        call :print_status "Run '%0 start' to start the container"
    ) else (
        call :print_status "Container is running"
    )
)
goto :eof

REM Function to create Docker volume
:create_volume
call :print_status "Creating Docker volume '%VOLUME_NAME%'"
docker volume inspect %VOLUME_NAME% >nul 2>&1
if errorlevel 1 (
    docker volume create %VOLUME_NAME%
    call :print_status "Volume created successfully"
) else (
    call :print_status "Volume already exists"
)
goto :eof

REM Function to copy repository to container
:copy_repo
call :print_header "Copying Repository to Container"

REM Check if source path exists
if not exist "%~1" (
    call :print_error "Source path '%~1' does not exist"
    exit /b 1
)

REM Check if container exists and is running
docker ps --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if errorlevel 1 (
    call :print_error "Container '%CONTAINER_NAME%' is not running"
    call :print_status "Run '%0 start' to start the container first"
    exit /b 1
)

call :print_status "Copying files from '%~1' to container..."
docker cp "%~1" %CONTAINER_NAME%:/app/
call :print_status "Repository copied successfully"

REM Set proper permissions
docker exec %CONTAINER_NAME% chown -R appuser:appuser /app/
call :print_status "Permissions updated"
goto :eof

REM Function to create Docker image and container
:create_image_and_container
call :print_header "Creating Docker Image and Container"

REM Create volume
call :create_volume

REM Build Docker image
call :print_status "Building Docker image '%IMAGE_NAME%'"
docker build -t %IMAGE_NAME% .

REM Remove existing container if it exists
docker ps -a --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if not errorlevel 1 (
    call :print_status "Removing existing container '%CONTAINER_NAME%'"
    docker rm -f %CONTAINER_NAME% >nul 2>&1
)

REM Create and start container
call :print_status "Creating and starting container '%CONTAINER_NAME%'"
docker run -d --name %CONTAINER_NAME% -p %SSH_PORT%:22 -v %VOLUME_NAME%:/app/shared %IMAGE_NAME%

REM Wait for container to be ready
call :print_status "Waiting for container to be ready..."
timeout /t 5 /nobreak >nul

REM Verify container is running
docker ps --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if not errorlevel 1 (
    call :print_status "Container created and started successfully"
    call :print_status "SSH access: ssh appuser@localhost -p %SSH_PORT% (password: password123)"
    call :print_status "Container name: %CONTAINER_NAME%"
) else (
    call :print_error "Failed to start container"
    exit /b 1
)
goto :eof

REM Function to start container
:start_container
call :print_header "Starting Container"

docker ps --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if not errorlevel 1 (
    call :print_status "Container is already running"
    goto :eof
)

docker ps -a --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if not errorlevel 1 (
    docker start %CONTAINER_NAME%
    call :print_status "Container started successfully"
) else (
    call :print_error "Container '%CONTAINER_NAME%' does not exist"
    call :print_status "Run '%0 create' to create the container first"
    exit /b 1
)
goto :eof

REM Function to stop container
:stop_container
call :print_header "Stopping Container"

docker ps --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if not errorlevel 1 (
    docker stop %CONTAINER_NAME%
    call :print_status "Container stopped successfully"
) else (
    call :print_status "Container is not running"
)
goto :eof

REM Function to restart container
:restart_container
call :print_header "Restarting Container"
call :stop_container
timeout /t 2 /nobreak >nul
call :start_container
goto :eof

REM Function to remove container and image
:remove_container_and_image
call :print_header "Removing Container and Image"

REM Stop and remove container
docker ps -a --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if not errorlevel 1 (
    docker rm -f %CONTAINER_NAME% >nul 2>&1
    call :print_status "Container removed"
)

REM Remove image
docker image inspect %IMAGE_NAME% >nul 2>&1
if not errorlevel 1 (
    docker rmi %IMAGE_NAME%
    call :print_status "Image removed"
)
goto :eof

REM Function to shell into container
:shell_into_container
call :print_header "Accessing Container Shell"

docker ps --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if errorlevel 1 (
    call :print_error "Container '%CONTAINER_NAME%' is not running"
    call :print_status "Run '%0 start' to start the container first"
    exit /b 1
)

call :print_status "Opening shell in container..."
docker exec -it %CONTAINER_NAME% /bin/bash
goto :eof

REM Function to build Java project
:build_java_project
call :print_header "Building Java Project"

docker ps --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if errorlevel 1 (
    call :print_error "Container '%CONTAINER_NAME%' is not running"
    call :print_status "Run '%0 start' to start the container first"
    exit /b 1
)

call :print_status "Building Java project in container..."
docker exec -it %CONTAINER_NAME% bash -c "cd /app && ./gradlew build"
call :print_status "Build completed"
goto :eof

REM Function to show container logs
:show_logs
call :print_header "Container Logs"

docker ps -a --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if not errorlevel 1 (
    docker logs %CONTAINER_NAME%
) else (
    call :print_error "Container '%CONTAINER_NAME%' does not exist"
    exit /b 1
)
goto :eof

REM Function to show status
:show_status
call :print_header "Container Status"

docker ps -a --format "table {{.Names}}" | findstr /c:"%CONTAINER_NAME%" >nul
if not errorlevel 1 (
    docker ps -a --filter "name=%CONTAINER_NAME%"
) else (
    call :print_status "Container '%CONTAINER_NAME%' does not exist"
)
goto :eof

REM Function to show help
:show_help
call :print_header "Java Docker Environment Manager"
echo Usage: %0 [command] [options]
echo.
echo Commands:
echo   (no args)     Check if image and container exist (default behavior)
echo   copy_repo ^<path^>  Copy repository from local path to container
echo   create        Create Docker image and container
echo   start         Start the container
echo   stop          Stop the container
echo   restart       Restart the container
echo   remove        Remove container and image
echo   shell         Open shell in running container
echo   build         Build Java project in container
echo   logs          Show container logs
echo   status        Show container status
echo   help          Show this help message
echo.
echo Examples:
echo   %0                    # Check environment
echo   %0 create             # Create image and container
echo   %0 copy_repo .        # Copy current directory to container
echo   %0 shell              # Access container shell
echo   %0 build              # Build Java project
echo.
echo SSH Access:
echo   ssh appuser@localhost -p %SSH_PORT%
echo   Password: password123
goto :eof

REM Main execution
:main
call :print_header "Java Docker Environment Manager"
call :parse_arguments %*
goto :eof

REM Run main function with all arguments
call :main %* 