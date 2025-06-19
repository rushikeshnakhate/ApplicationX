# Java Utilities - Multi-Module Project

This project contains a collection of Java utilities organized as Gradle multi-module plugins. Each plugin is categorized as either a **library** (for code reuse) or an **executable** (standalone application).

## 📁 Project Structure

```
java/
├── build.gradle                    # Root build configuration
├── settings.gradle                 # Multi-module settings
├── gradlew                         # Gradle wrapper
├── gradlew.bat                     # Gradle wrapper (Windows)
└── plugins/
    ├── fixdictionarygenerator/     # Library - FIX XML parser & code generator
    ├── yamlloader/                 # Library - YAML configuration loader
    ├── quickfiximpl/               # Executable - QuickFIX client/server
    └── ringbuffer/                 # Executable - High-performance event processing
```

## 📚 Plugin Categories

### **Library Plugins** (for reuse in other projects)

#### **fixdictionarygenerator**
- **Purpose**: Parse FIX XML dictionaries and generate Java code for encoding/decoding FIX messages
- **Dependencies**: Apache Commons Lang
- **Main Class**: `FixDictionaryGenerator`
- **Usage**: Code generation utility

#### **yamlloader**
- **Purpose**: Load and parse YAML configuration files
- **Dependencies**: SnakeYAML
- **Main Class**: `YamlLoader`
- **Usage**: Configuration management utility

### **Executable Plugins** (standalone applications)

#### **quickfiximpl**
- **Purpose**: QuickFIX client and server implementation
- **Dependencies**: QuickFIX/J
- **Main Class**: `QuickFixDemo`
- **Usage**: FIX protocol trading applications

#### **ringbuffer**
- **Purpose**: High-performance event processing using LMAX Disruptor
- **Dependencies**: LMAX Disruptor
- **Main Class**: `RingBufferApplication`
- **Usage**: Event-driven applications

## 🚀 Quick Start

### **Build All Projects**
```bash
cd java
./gradlew build
```

### **Build Specific Plugin**
```bash
# Build library
./gradlew :fixdictionarygenerator:build
./gradlew :yamlloader:build

# Build executable
./gradlew :quickfiximpl:build
./gradlew :ringbuffer:build
```

## 📚 Library Usage

### **Using Libraries in Other Projects**
```gradle
dependencies {
    implementation project(':fixdictionarygenerator')
    implementation project(':yamlloader')
}
```

### **Publish Libraries Locally**
```bash
./gradlew :fixdictionarygenerator:publishToMavenLocal
./gradlew :yamlloader:publishToMavenLocal
```

## 🚀 Executable Usage

### **Run Applications**
```bash
# Run QuickFIX server
./gradlew :quickfiximpl:run --args="server"

# Run QuickFIX client
./gradlew :quickfiximpl:run --args="client"

# Run RingBuffer demo
./gradlew :ringbuffer:run
```

### **Create Distributions**
```bash
# Create executable distributions
./gradlew :quickfiximpl:distZip
./gradlew :ringbuffer:distZip
```

### **Install Distributions**
```bash
# Install to build/install/
./gradlew :quickfiximpl:installDist
./gradlew :ringbuffer:installDist

# Run from installed distribution
./build/install/quickfiximpl/bin/quickfiximpl server
./build/install/ringbuffer/bin/ringbuffer
```

## 🧪 Testing

### **Run All Tests**
```bash
./gradlew test
```

### **Run Specific Plugin Tests**
```bash
./gradlew :fixdictionarygenerator:test
./gradlew :yamlloader:test
./gradlew :quickfiximpl:test
./gradlew :ringbuffer:test
```

## 📦 Build Artifacts

### **Libraries**
- **JAR**: `build/libs/<plugin>-<version>.jar`
- **Sources JAR**: `build/libs/<plugin>-<version>-sources.jar`
- **Javadoc JAR**: `build/libs/<plugin>-<version>-javadoc.jar`

### **Executables**
- **Fat JAR**: `build/libs/<plugin>-<version>.jar` (includes dependencies)
- **Distribution ZIP**: `build/distributions/<plugin>-<version>.zip`
- **Installed Distribution**: `build/install/<plugin>/`

## 🔧 Configuration

### **Java Version**
- **Source/Target**: Java 22
- **Compatibility**: Java 11+

### **Dependencies**
- **Build Tool**: Gradle 8.x
- **Testing**: JUnit 4
- **Logging**: SLF4J + Logback

## 📋 Plugin Details

### **fixdictionarygenerator**
```bash
# Generate code from FIX XML
java -cp build/libs/fixdictionarygenerator-1.0-SNAPSHOT.jar \
     com.applicationsx.fixdictionarygenerator.FixDictionaryGenerator \
     src/main/resources/fix44_sample.xml \
     generated/FixMessageCodec.java
```

### **yamlloader**
```java
// Load YAML configuration
YamlLoader loader = new YamlLoader();
Map<String, Object> config = loader.loadFromFile("config.yaml");
```

### **quickfiximpl**
```bash
# Server mode
./gradlew :quickfiximpl:run --args="server"

# Client mode  
./gradlew :quickfiximpl:run --args="client"
```

### **ringbuffer**
```bash
# Run event processing demo
./gradlew :ringbuffer:run
```

## 🛠️ Development

### **Add New Library Plugin**
1. Create directory: `plugins/newlibrary/`
2. Add to `settings.gradle`:
   ```gradle
   include 'newlibrary'
   project(':newlibrary').projectDir = file('plugins/newlibrary')
   ```
3. Create `build.gradle` with `java-library` plugin

### **Add New Executable Plugin**
1. Create directory: `plugins/newexecutable/`
2. Add to `settings.gradle`
3. Create `build.gradle` with `java` + `application` plugins

## 📄 License

This project is part of the ApplicationX utilities collection.

## 🤝 Contributing

1. Follow the existing project structure
2. Add appropriate tests for new functionality
3. Update this README for new plugins
4. Ensure proper categorization (library vs executable) 