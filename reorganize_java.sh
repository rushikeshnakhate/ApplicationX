#!/bin/bash

# Create new directory structure
mkdir -p src/main/java/com/applicationsx/{quickfiximpl,ringbuffer,yaml_loader}
mkdir -p src/test/java/com/applicationsx/{quickfiximpl,ringbuffer,yaml_loader}

# Create build.gradle files in their new locations
cat > src/main/java/com/applicationsx/ringbuffer/build.gradle << 'EOL'
plugins {
    id 'java'
}

group = 'com.applicationsx.ringbuffer'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.lmax:disruptor:4.0.0'
    implementation 'org.slf4j:slf4j-api:1.7.36'
    implementation 'ch.qos.logback:logback-classic:1.2.11'
    
    testImplementation 'junit:junit:4.13.2'
}

sourceSets {
    main {
        java {
            srcDirs = ['src/main/java']
        }
    }
    test {
        java {
            srcDirs = ['src/test/java']
        }
    }
}

test {
    useJUnit()
}
EOL

cat > src/main/java/com/applicationsx/quickfiximpl/build.gradle << 'EOL'
plugins {
    id 'java'
}

group = 'com.applicationsx.quickfiximpl'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.quickfixj:quickfixj-all:2.3.1'
    implementation 'org.slf4j:slf4j-api:1.7.36'
    implementation 'ch.qos.logback:logback-classic:1.2.11'
    
    testImplementation 'junit:junit:4.13.2'
}

sourceSets {
    main {
        java {
            srcDirs = ['src/main/java']
        }
    }
    test {
        java {
            srcDirs = ['src/test/java']
        }
    }
}

test {
    useJUnit()
}
EOL

cat > src/main/java/com/applicationsx/yaml_loader/build.gradle << 'EOL'
plugins {
    id 'java'
}

group = 'com.applicationsx.yaml_loader'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.yaml:snakeyaml:2.0'
    implementation 'org.slf4j:slf4j-api:1.7.36'
    implementation 'ch.qos.logback:logback-classic:1.2.11'
    
    testImplementation 'junit:junit:4.13.2'
}

sourceSets {
    main {
        java {
            srcDirs = ['src/main/java']
        }
    }
    test {
        java {
            srcDirs = ['src/test/java']
        }
    }
}

test {
    useJUnit()
}
EOL

# Create a root build.gradle
cat > build.gradle << 'EOL'
plugins {
    id 'java'
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply plugin: 'java'
    
    sourceCompatibility = '11'
    targetCompatibility = '11'
    
    dependencies {
        implementation 'org.slf4j:slf4j-api:1.7.36'
        implementation 'ch.qos.logback:logback-classic:1.2.11'
        testImplementation 'junit:junit:4.13.2'
    }
}
EOL

# Create settings.gradle
cat > settings.gradle << 'EOL'
include 'ringbuffer'
include 'quickfiximpl'
include 'yaml_loader'

project(':ringbuffer').projectDir = file('src/main/java/com/applicationsx/ringbuffer')
project(':quickfiximpl').projectDir = file('src/main/java/com/applicationsx/quickfiximpl')
project(':yaml_loader').projectDir = file('src/main/java/com/applicationsx/yaml_loader')
EOL

# Move source files
mv java/src/main/java/ringbuffer/* src/main/java/com/applicationsx/ringbuffer/
mv java/src/main/java/quickfiximpl/* src/main/java/com/applicationsx/quickfiximpl/
mv java/src/main/java/yaml_loader/* src/main/java/com/applicationsx/yaml_loader/

# Move test files
mv java/src/test/java/ringbuffer/* src/test/java/com/applicationsx/ringbuffer/
mv java/src/test/java/quickfiximpl/* src/test/java/com/applicationsx/quickfiximpl/
mv java/src/test/java/yaml_loader/* src/test/java/com/applicationsx/yaml_loader/

# Move build.gradle files
mv java/src/main/java/ringbuffer/build.gradle src/main/java/com/applicationsx/ringbuffer/
mv java/src/main/java/quickfiximpl/build.gradle src/main/java/com/applicationsx/quickfiximpl/
mv java/src/main/java/yaml_loader/build.gradle src/main/java/com/applicationsx/yaml_loader/

# Clean up old directories
rm -rf java/src

echo "Java project structure has been reorganized successfully!" 