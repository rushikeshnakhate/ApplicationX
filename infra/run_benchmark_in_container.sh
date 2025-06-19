#!/bin/bash

# Run benchmarks in Docker containers
echo "Running Java benchmarks in container..."
docker build -t crosslang-java -f infra/docker/Dockerfile.java .
docker run crosslang-java ./gradlew benchmark

echo "Running C++ benchmarks in container..."
docker build -t crosslang-cpp -f infra/docker/Dockerfile.cpp .
docker run crosslang-cpp ./build/benchmark

echo "Running Python benchmarks in container..."
docker build -t crosslang-python -f infra/docker/Dockerfile.python .
docker run crosslang-python python -m pytest benchmarks/

echo "All container benchmarks completed!"
