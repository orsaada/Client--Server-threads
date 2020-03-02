cmake_minimum_required(VERSION 3.9)
project(spl_net_client)

link_libraries(boost_system boost_thread)

set(CMAKE_CXX_STANDARD 11)

add_executable(spl_net_client
        include/connectionHandler.h
        src/connectionHandler.cpp
        src/BBclient.cpp
        makefile src/BBclient.cpp)

find_package(Boost)
if (Boost_FOUND)
    include_directories(${Boost_INCLUDE_DIR})
endif()