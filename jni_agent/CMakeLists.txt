cmake_minimum_required(VERSION 3.15)
project(rpc_cpp)

set(CMAKE_CXX_STANDARD 14)

add_executable(rpc_cpp main.cpp main.h)

set(THREADS_PREFER_PTHREAD_FLAG ON)
find_package(Threads REQUIRED)
target_link_libraries(rpc_cpp Threads::Threads)