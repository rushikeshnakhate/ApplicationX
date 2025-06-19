# Java Docker Environment

This directory contains Docker configuration for the Java project with RHEL7 base image and Java 21.

## 🐳 Docker Configuration

### **Dockerfile Features**
- **Base Image**: RHEL7 (Red Hat Enterprise Linux 7)
- **Java Version**: OpenJDK 21
- **SSH Access**: Configured for remote access
- **User**: `appuser` with sudo privileges
- **Working Directory**: `/app`

### **Container Details**
- **Image Name**: `applicationx-java21`
- **Container Name**: `applicationx-java-container`
- **Volume**: `applicationx-java-volume`
- **SSH Port**: `2222`
- **SSH Credentials**: `appuser` / `password123`

## 🚀 Quick Start

### **Prerequisites**
- Docker Desktop installed and running
- Git (for cloning repository)

### **Initial Setup**
```bash
# Check if Docker is running and environment status
./run.sh

# Create Docker image and container
./run.sh create

# Copy current repository to container
./run.sh copy_repo .

# Access container shell
./run.sh shell
```

## 📋 Available Commands

### **Environment Management**
```bash
# Check environment status (default behavior)
./run.sh

# Create image and container
./run.sh create

# Start container
./run.sh start

# Stop container
./run.sh stop

# Restart container
./run.sh restart

# Remove container and image
./run.sh remove
```

### **Repository Management**
```bash
# Copy repository to container
./run.sh copy_repo <source_path>

# Example: Copy current directory
./run.sh copy_repo .
```

### **Development Commands**
```bash
# Access container shell
./run.sh shell

# Build Java project in container
./run.sh build

# Show container logs
./run.sh logs

# Show container status
./run.sh status
```

### **Help**
```bash
# Show help information
./run.sh help
```

## 🔧 Windows Users

For Windows users, use the batch file version:

```cmd
# Check environment
run.bat

# Create image and container
run.bat create

# Copy repository
run.bat copy_repo .

# Access shell
run.bat shell
```

## 🔐 SSH Access

Once the container is running, you can access it via SSH:

```bash
# SSH into container
ssh appuser@localhost -p 2222

# Password: password123
```

## 📁 Volume Management

The Docker setup includes a persistent volume:

- **Volume Name**: `applicationx-java-volume`
- **Mount Point**: `/app/shared` (inside container)
- **Purpose**: Persistent storage for shared files

## 🏗️ Build Process

### **Manual Docker Commands**
```bash
# Build image
docker build -t applicationx-java21 .

# Create volume
docker volume create applicationx-java-volume

# Run container
docker run -d \
  --name applicationx-java-container \
  -p 2222:22 \
  -v applicationx-java-volume:/app/shared \
  applicationx-java21
```

### **Using Scripts**
```bash
# Full setup
./run.sh create
./run.sh copy_repo .
./run.sh shell

# Build Java project
./run.sh build
```

## 🔍 Troubleshooting

### **Common Issues**

#### **Docker Not Running**
```bash
# Error: Docker is not running
# Solution: Start Docker Desktop
```

#### **Port Already in Use**
```bash
# Error: Port 2222 already in use
# Solution: Change SSH_PORT in run.sh or stop conflicting service
```

#### **Permission Denied**
```bash
# Error: Permission denied on run.sh
# Solution: Make script executable (Linux/Mac)
chmod +x run.sh
```

#### **Container Won't Start**
```bash
# Check container logs
./run.sh logs

# Check container status
./run.sh status

# Restart container
./run.sh restart
```

### **Debug Commands**
```bash
# Check Docker status
docker info

# List all containers
docker ps -a

# List all images
docker images

# List all volumes
docker volume ls

# Check container logs
docker logs applicationx-java-container
```

## 📦 Container Contents

### **Installed Software**
- **Java**: OpenJDK 21 (set as default)
- **SSH**: OpenSSH Server & Client
- **Tools**: wget, curl, git, unzip, tar, gzip
- **User Management**: sudo configured

### **Directory Structure**
```
/app/
├── shared/          # Volume mount point
└── [project files]  # Copied repository
```

### **Environment Variables**
- `JAVA_HOME=/usr/lib/jvm/java-21-openjdk`
- `PATH=$JAVA_HOME/bin:$PATH`
- `JAVA_VERSION=21`

## 🔄 Development Workflow

### **Typical Development Session**
```bash
# 1. Start environment
./run.sh create

# 2. Copy latest code
./run.sh copy_repo .

# 3. Access container
./run.sh shell

# 4. Build project
./run.sh build

# 5. Run tests
docker exec -it applicationx-java-container bash -c "cd /app && ./gradlew test"

# 6. Stop when done
./run.sh stop
```

### **Continuous Development**
```bash
# Keep container running
./run.sh start

# Copy changes as needed
./run.sh copy_repo .

# Build and test
./run.sh build
```

## 🛡️ Security Notes

- **SSH Password**: Default password is `password123` - change in production
- **User**: Container runs as `appuser` with sudo privileges
- **Port**: SSH exposed on port 2222 (non-standard)
- **Volume**: Persistent volume for shared data

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [RHEL7 Documentation](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/7/)
- [OpenJDK 21 Documentation](https://openjdk.org/projects/jdk/21/)
- [Gradle Documentation](https://gradle.org/docs/)

## 🤝 Contributing

When contributing to the Docker setup:

1. Test changes on both Linux and Windows
2. Update this README if adding new features
3. Ensure backward compatibility
4. Test all script commands
5. Verify SSH access works correctly 