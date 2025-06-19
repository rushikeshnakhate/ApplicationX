cmake_minimum_required(VERSION 3.15)
project(FixDictionaryGenerator VERSION 1.0)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find required packages
find_package(PkgConfig REQUIRED)
pkg_check_modules(TINYXML2 REQUIRED tinyxml2)

# Include directories
include_directories(include)

# Main executable
add_executable(fixdictionarygenerator 
    src/main.cpp
    src/FixDictionaryGenerator.cpp
)

# Link libraries
target_link_libraries(fixdictionarygenerator ${TINYXML2_LIBRARIES})

# Test executable
add_executable(fixdictionarygenerator_test 
    test/FixDictionaryGeneratorTest.cpp
    src/FixDictionaryGenerator.cpp
)

target_link_libraries(fixdictionarygenerator_test ${TINYXML2_LIBRARIES})

# Enable testing
enable_testing()
add_test(NAME FixDictionaryGeneratorTest COMMAND fixdictionarygenerator_test) 