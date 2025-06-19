cmake_minimum_required(VERSION 3.15)
project(CrossLangUtilsCpp)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find required packages
find_package(QuickFIX REQUIRED)
find_package(GTest REQUIRED)

# Add executable
add_executable(quickfix_app
    src/main.cpp
    src/QuickFixApplication.cpp
    src/QuickFixClient.cpp
)

# Add test executable
add_executable(quickfix_test
    test/QuickFixTest.cpp
    src/QuickFixApplication.cpp
    src/QuickFixClient.cpp
)

# Include directories
target_include_directories(quickfix_app PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${QuickFIX_INCLUDE_DIRS}
)

target_include_directories(quickfix_test PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${QuickFIX_INCLUDE_DIRS}
    ${GTEST_INCLUDE_DIRS}
)

# Link libraries
target_link_libraries(quickfix_app PRIVATE
    ${QuickFIX_LIBRARIES}
)

target_link_libraries(quickfix_test PRIVATE
    ${QuickFIX_LIBRARIES}
    ${GTEST_LIBRARIES}
    pthread
)

# Copy configuration files
configure_file(
    ${CMAKE_CURRENT_SOURCE_DIR}/quickfix.cfg
    ${CMAKE_CURRENT_BINARY_DIR}/quickfix.cfg
    COPYONLY
)

configure_file(
    ${CMAKE_CURRENT_SOURCE_DIR}/quickfix_client.cfg
    ${CMAKE_CURRENT_BINARY_DIR}/quickfix_client.cfg
    COPYONLY
)

# Enable testing
enable_testing()
add_test(NAME quickfix_test COMMAND quickfix_test)

add_subdirectory(src/quickfiximpl)
add_subdirectory(src/yaml_loader)
add_subdirectory(src/ringbuffer) 