#!/bin/bash

# Create multi-module structure
mkdir -p java/quickfiximpl/src/main/java/com/applicationsx/quickfiximpl
mkdir -p java/quickfiximpl/src/test/java/com/applicationsx/quickfiximpl
mkdir -p java/ringbuffer/src/main/java/com/applicationsx/ringbuffer
mkdir -p java/ringbuffer/src/test/java/com/applicationsx/ringbuffer
mkdir -p java/yaml_loader/src/main/java/com/applicationsx/yaml_loader
mkdir -p java/yaml_loader/src/test/java/com/applicationsx/yaml_loader

# Move source files
if [ -d src/main/java/com/applicationsx/quickfiximpl ]; then
  mv src/main/java/com/applicationsx/quickfiximpl/* java/quickfiximpl/src/main/java/com/applicationsx/quickfiximpl/
fi
if [ -d src/main/java/com/applicationsx/ringbuffer ]; then
  mv src/main/java/com/applicationsx/ringbuffer/* java/ringbuffer/src/main/java/com/applicationsx/ringbuffer/
fi
if [ -d src/main/java/com/applicationsx/yaml_loader ]; then
  mv src/main/java/com/applicationsx/yaml_loader/* java/yaml_loader/src/main/java/com/applicationsx/yaml_loader/
fi

# Move test files
if [ -d src/test/java/com/applicationsx/quickfiximpl ]; then
  mv src/test/java/com/applicationsx/quickfiximpl/* java/quickfiximpl/src/test/java/com/applicationsx/quickfiximpl/
fi
if [ -d src/test/java/com/applicationsx/ringbuffer ]; then
  mv src/test/java/com/applicationsx/ringbuffer/* java/ringbuffer/src/test/java/com/applicationsx/ringbuffer/
fi
if [ -d src/test/java/com/applicationsx/yaml_loader ]; then
  mv src/test/java/com/applicationsx/yaml_loader/* java/yaml_loader/src/test/java/com/applicationsx/yaml_loader/
fi

# Move build.gradle files
if [ -f src/main/java/com/applicationsx/quickfiximpl/build.gradle ]; then
  mv src/main/java/com/applicationsx/quickfiximpl/build.gradle java/quickfiximpl/
fi
if [ -f src/main/java/com/applicationsx/ringbuffer/build.gradle ]; then
  mv src/main/java/com/applicationsx/ringbuffer/build.gradle java/ringbuffer/
fi
if [ -f src/main/java/com/applicationsx/yaml_loader/build.gradle ]; then
  mv src/main/java/com/applicationsx/yaml_loader/build.gradle java/yaml_loader/
fi

# Move root build files if present
if [ -f build.gradle ]; then
  mv build.gradle java/
fi
if [ -f settings.gradle ]; then
  mv settings.gradle java/
fi

# Clean up old src structure if empty
rmdir --ignore-fail-on-non-empty -p src/main/java/com/applicationsx/quickfiximpl
rmdir --ignore-fail-on-non-empty -p src/main/java/com/applicationsx/ringbuffer
rmdir --ignore-fail-on-non-empty -p src/main/java/com/applicationsx/yaml_loader
rmdir --ignore-fail-on-non-empty -p src/main/java/com/applicationsx
rmdir --ignore-fail-on-non-empty -p src/main/java
rmdir --ignore-fail-on-non-empty -p src/test/java/com/applicationsx/quickfiximpl
rmdir --ignore-fail-on-non-empty -p src/test/java/com/applicationsx/ringbuffer
rmdir --ignore-fail-on-non-empty -p src/test/java/com/applicationsx/yaml_loader
rmdir --ignore-fail-on-non-empty -p src/test/java/com/applicationsx
rmdir --ignore-fail-on-non-empty -p src/test/java
rmdir --ignore-fail-on-non-empty -p src/test
rmdir --ignore-fail-on-non-empty -p src/main
rmdir --ignore-fail-on-non-empty -p src

echo "Java project has been restructured to a Gradle multi-module layout!" 