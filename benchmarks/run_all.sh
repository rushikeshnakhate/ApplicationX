#!/bin/bash

# Run benchmarks for all languages
echo "Running Java benchmarks..."
cd ../java && ./gradlew benchmark

echo "Running C++ benchmarks..."
cd ../cpp && mkdir -p build && cd build && cmake .. && make benchmark

echo "Running Python benchmarks..."
cd ../../python && python -m pytest benchmarks/

echo "All benchmarks completed!"
