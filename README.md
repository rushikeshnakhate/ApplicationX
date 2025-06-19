# ApplicationsX - Cross-Language Utilities

This project provides a collection of high-performance utilities implemented in Java, Python, and C++.

## Project Structure

```
├── README.md
├── benchmark_results/          # Store performance data, plots, comparisons
│
├── shared_configs/            # Common input data or YAMLs to be loaded by all languages
│   ├── sample.yaml
│   └── orders.json
│
├── utilities/
│   ├── quickfix/             # FIX Protocol Implementation
│   │   ├── java/            # QuickFIX/J Implementation
│   │   ├── python/          # QuickFIX/Python Implementation
│   │   └── cpp/             # QuickFIX/C++ Implementation
│   │
│   ├── yaml_loader/         # YAML Configuration Loader
│   │   ├── java/            # SnakeYAML Implementation
│   │   ├── python/          # PyYAML Implementation
│   │   └── cpp/             # yaml-cpp Implementation
│   │
│   ├── ringbuffer/          # High-Performance Ring Buffer
│   │   ├── java/            # LMAX Disruptor Implementation
│   │   ├── python/          # Custom asyncio.Queue Implementation
│   │   └── cpp/             # Lock-free Circular Buffer
│   │
│   └── ... (other utilities)
│
├── benchmarks/               # Performance Testing
│   ├── quickfix/
│   │   ├── java_benchmark.sh
│   │   ├── python_benchmark.sh
│   │   └── ...
│   └── ringbuffer/
│       ├── run_all.sh
│       └── parse_results.py
│
├── scripts/                  # Utility Scripts
│   ├── plot_results.py      # Performance Visualization
│   └── result_aggregator.py # Results Analysis
│
└── infra/                   # Infrastructure
    ├── docker/              # Containerization
    │   ├── Dockerfile.java
    │   ├── Dockerfile.python
    │   └── ...
    └── run_benchmark_in_container.sh
```

## Quick Start

### Prerequisites

- Java 11 or higher
- Python 3.8 or higher
- C++17 compatible compiler
- CMake 3.15 or higher
- Docker (optional)

### Building and Running

#### QuickFIX Implementation

1. Java:
```bash
# Build
cd utilities/quickfix/java
./gradlew build

# Run server
./gradlew run

# Run client (in another terminal)
./gradlew run --args="client"

# Run tests
./gradlew test
```

2. Python:
```bash
# Install dependencies
cd utilities/quickfix/python
pip install -r requirements.txt

# Run server
python quickfix_app.py

# Run client (in another terminal)
python quickfix_client.py

# Run tests
python -m unittest test_quickfix.py
```

3. C++:
```bash
# Build
cd utilities/quickfix/cpp
mkdir build && cd build
cmake ..
make

# Run server
./quickfix_app

# Run client (in another terminal)
./quickfix_app --client

# Run tests
./quickfix_test
```

#### RingBuffer Implementation

1. Java (LMAX Disruptor):
```bash
cd utilities/ringbuffer/java
./gradlew build
./gradlew run
```

2. Python (asyncio.Queue):
```bash
cd utilities/ringbuffer/python
pip install -r requirements.txt
python ringbuffer_app.py
```

3. C++ (Lock-free):
```bash
cd utilities/ringbuffer/cpp
mkdir build && cd build
cmake ..
make
./ringbuffer_app
```

### Running Benchmarks

```bash
# Run all benchmarks
cd benchmarks
./run_all.sh

# Generate performance plots
cd scripts
python plot_results.py
```

### Docker Support

```bash
# Build and run Java container
cd infra/docker
docker build -f Dockerfile.java -t applicationsx-java .
docker run applicationsx-java

# Build and run Python container
docker build -f Dockerfile.python -t applicationsx-python .
docker run applicationsx-python

# Build and run C++ container
docker build -f Dockerfile.cpp -t applicationsx-cpp .
docker run applicationsx-cpp
```

## Docker: All-in-One Development Environment

This project provides a Dockerfile (`infra/docker/Dockerfile.all`) and a helper script (`infra/run.sh`) to build a Linux image with Java, Python, and C++ toolchains, plus all dependencies for this repository.

### Prerequisites
- [Docker](https://docs.docker.com/get-docker/) installed on your system
- Bash shell (for running `infra/run.sh`)

### Build and Run the Docker Image

1. **Build and start the container:**
   ```sh
   bash infra/run.sh
   ```
   This will:
   - Build the Docker image from `infra/docker/Dockerfile.all`
   - Remove any previous container named `crosslang-all-container`
   - Start a new container, mounting your current project directory to `/app` inside the container

2. **Access your environment:**
   You will be dropped into a bash shell inside the container, with all tools and dependencies ready for Java, Python, and C++ development and testing.

3. **Example usage inside the container:**
   - Build Java: `cd /app/java && ./gradlew build`
   - Build C++: `cd /app/cpp && mkdir -p build && cd build && cmake .. && make`
   - Run Python: `cd /app/python && python3 ...`

4. **Stop the container:**
   Exit the shell (`exit`) or stop the container from another terminal:
   ```sh
   docker stop crosslang-all-container
   ```

## Design Patterns Used

1. QuickFIX Implementation:
   - Factory Pattern for message creation
   - Strategy Pattern for message handling
   - Observer Pattern for session events
   - Singleton Pattern for session management

2. RingBuffer Implementation:
   - Producer-Consumer Pattern
   - Lock-free Design (C++)
   - Event-driven Architecture (Java)
   - Asynchronous Processing (Python)

## Performance Considerations

- Java: Uses LMAX Disruptor for high-performance ring buffer
- Python: Implements custom asyncio.Queue with multiprocessing support
- C++: Implements lock-free circular buffer using atomic operations

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 