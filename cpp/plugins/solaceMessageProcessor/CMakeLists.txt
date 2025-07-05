cmake_minimum_required(VERSION 3.10)
project(TradeMessageProcessor)

set(CMAKE_CXX_STANDARD 17)

find_package(Protobuf REQUIRED)
include_directories(${Protobuf_INCLUDE_DIRS})

# Add Solace C API include and link directories here
# include_directories(/path/to/solace/include)
# link_directories(/path/to/solace/lib)

add_executable(TradeMessageProcessor main.cpp Producer.cpp Consumer.cpp TradeMessageBuilder.cpp)

target_link_libraries(TradeMessageProcessor ${Protobuf_LIBRARIES})
# target_link_libraries(solaceMessageProcessor solaceapi) # Uncomment and set correct Solace library