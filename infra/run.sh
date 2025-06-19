#!/bin/bash

IMAGE_NAME=crosslang-all:latest
CONTAINER_NAME=crosslang-all-container

# Build the Docker image
docker build -f infra/docker/Dockerfile.all -t $IMAGE_NAME .

# Remove any existing container with the same name
docker rm -f $CONTAINER_NAME 2>/dev/null || true

# Run the container, mounting the current directory
# (so you can access your code and results from inside the container)
docker run -it --name $CONTAINER_NAME -v $(pwd):/app $IMAGE_NAME 