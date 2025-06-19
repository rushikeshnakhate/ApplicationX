add_library(ringbuffer RingBuffer.cpp)
target_include_directories(ringbuffer PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/../../include/ringbuffer) 