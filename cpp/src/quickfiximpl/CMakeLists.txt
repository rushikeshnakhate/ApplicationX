add_library(quickfiximpl QuickFixApplication.cpp QuickFixClient.cpp)
target_include_directories(quickfiximpl PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/../../include/quickfiximpl) 