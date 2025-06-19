#!/bin/bash

# Java Docker Environment Manager
# Usage: ./run.sh [command]

set -e

# Configuration
IMAGE_NAME="applicationx-java21"
CONTAINER_NAME="applicationx-java-container"
REPO_VOLUME_HOST="/d/ApplicationX/docker_persist/repo"
SETTINGS_VOLUME_HOST="/d/ApplicationX/docker_persist/settings"
SSH_PORT="2222"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Logging functions
info()    { echo -e "${GREEN}[INFO]${NC} $1"; }
warn()    { echo -e "${YELLOW}[WARN]${NC} $1"; }
error()   { echo -e "${RED}[ERROR]${NC} $1"; }

# Check Docker is running
check_docker() {
  if ! docker info >/dev/null 2>&1; then
    error "Docker is not running. Please start Docker Desktop."
    exit 1
  fi
}

# Convert Windows path for Docker bind mount
convert_path_for_docker() {
  local path="$1"
  if [[ "$(uname -s)" == MINGW* || "$(uname -s)" == MSYS* || "$(uname -s)" == CYGWIN* ]]; then
    path="$(echo "$path" | sed -E 's/^\/([a-zA-Z])\//\1:\//')"
    path="${path//\/\\}"
  fi
  echo "$path"
}

# Get parent directory (/d/ApplicationX/java)
get_parent_path() {
  local script_dir
  script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  local parent_path
  parent_path="$(cd "$script_dir/.." && pwd)"
  echo "$parent_path"
}

# Create host directories for persistence
ensure_host_dirs() {
  local repo_dir="$REPO_VOLUME_HOST"
  local settings_dir="$SETTINGS_VOLUME_HOST"
  if [[ "$(uname -s)" == MINGW* || "$(uname -s)" == MSYS* || "$(uname -s)" == CYGWIN* ]]; then
    repo_dir="$(convert_path_for_docker "$repo_dir")"
    settings_dir="$(convert_path_for_docker "$settings_dir")"
    mkdir -p "$repo_dir" "$settings_dir" || true
  else
    mkdir -p "$repo_dir" "$settings_dir"
  fi
  info "Ensured host directories: $repo_dir, $settings_dir"
}

# Build Docker image
build_image() {
  info "Building Docker image '$IMAGE_NAME'"
  docker build -t "$IMAGE_NAME" .
}

# Create and start container
create_container() {
  local repo_mount
  local settings_mount
  repo_mount="$(convert_path_for_docker "$REPO_VOLUME_HOST")"
  settings_mount="$(convert_path_for_docker "$SETTINGS_VOLUME_HOST")"

  info "Creating Docker container '$CONTAINER_NAME' with mounts:"
  info "- $repo_mount -> /root/java"
  info "- $settings_mount -> /root"

  docker run -d --name "$CONTAINER_NAME" \
    -p "$SSH_PORT:22" \
    -v "$repo_mount:/root/java" \
    -v "$settings_mount:/root" \
    "$IMAGE_NAME" >/dev/null

  info "Waiting for container to be ready..."
  sleep 5

  if docker ps --format "{{.Names}}" | grep -q "^$CONTAINER_NAME$"; then
    info "Container '$CONTAINER_NAME' created and started"
  else
    error "Container failed to start. Check logs with './run.sh logs'"
    exit 1
  fi
}

# Ensure image exists
ensure_image() {
  if ! docker image inspect "$IMAGE_NAME" >/dev/null 2>&1; then
    build_image
  else
    info "Image '$IMAGE_NAME' already exists"
  fi
}

# Ensure container is running
ensure_container() {
  if ! docker ps -a --format "{{.Names}}" | grep -q "^$CONTAINER_NAME$"; then
    create_container
  elif ! docker ps --format "{{.Names}}" | grep -q "^$CONTAINER_NAME$"; then
    docker start "$CONTAINER_NAME"
    info "Started existing container '$CONTAINER_NAME'"
  else
    info "Container '$CONTAINER_NAME' is already running"
  fi
}

# Copy parent directory to /root/java
copy_repo() {
  local parent_path
  parent_path="$(get_parent_path)"
  local parent_dir_name
  parent_dir_name="$(basename "$parent_path")"

  if ! docker ps --format "{{.Names}}" | grep -q "^$CONTAINER_NAME$"; then
    error "Container '$CONTAINER_NAME' is not running. Run './run.sh' first."
    exit 1
  fi

  if [ ! -d "$parent_path" ]; then
    error "Parent directory '$parent_path' does not exist"
    exit 1
  fi

  info "Copying '$parent_path' to container at '/root/$parent_dir_name'"

  # Create temporary tarball to handle Windows paths
  tar -c -C "$(dirname "$parent_path")" "$parent_dir_name" | docker cp - "$CONTAINER_NAME:/root/"

  docker exec "$CONTAINER_NAME" chown -R root:root "/root/$parent_dir_name"
  info "Copied and set permissions for '/root/$parent_dir_name'"
}

# Remove container
remove_container() {
  if docker ps -a --format "{{.Names}}" | grep -q "^$CONTAINER_NAME$"; then
    docker rm -f "$CONTAINER_NAME"
    info "Removed container '$CONTAINER_NAME'"
  fi
}

# Remove image
remove_image() {
  if docker image inspect "$IMAGE_NAME" >/dev/null 2>&1; then
    docker rmi "$IMAGE_NAME"
    info "Removed image '$IMAGE_NAME'"
  fi
}

# Show container logs
show_logs() {
  if docker ps -a --format "{{.Names}}" | grep -q "^$CONTAINER_NAME$"; then
    docker logs "$CONTAINER_NAME"
  else
    error "Container '$CONTAINER_NAME' does not exist"
    exit 1
  fi
}

# Main function
main() {
  check_docker
  ensure_host_dirs

  case "$1" in
    copy_repo)
      ensure_image
      ensure_container
      copy_repo
      ;;
    recreate)
      remove_container
      remove_image
      ensure_image
      ensure_container
      ;;
    clean_volumes)
      remove_container
      remove_image
      warn "Host directories '$REPO_VOLUME_HOST' and '$SETTINGS_VOLUME_HOST' are not deleted"
      ;;
    logs)
      show_logs
      ;;
    "")
      ensure_image
      ensure_container
      ;;
    *)
      error "Unknown command: $1"
      echo "Usage: ./run.sh [copy_repo | recreate | clean_volumes | logs]"
      exit 1
      ;;
  esac

  local parent_path
  parent_path="$(get_parent_path)"
  local parent_dir_name
  parent_dir_name="$(basename "$parent_path")"

  echo
  info "🎯 Docker environment ready!"
  echo "👉 Container: $CONTAINER_NAME"
  echo "👉 SSH: ssh root@localhost -p $SSH_PORT"
  echo "👉 Java directory mounted at /root/$parent_dir_name (persisted to $REPO_VOLUME_HOST)"
  echo "👉 Root settings persisted to $SETTINGS_VOLUME_HOST"
}

main "$@"