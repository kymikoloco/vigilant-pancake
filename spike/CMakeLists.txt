cmake_minimum_required(VERSION 3.15)

project( Spike )

add_executable( Spike 
    ${CMAKE_CURRENT_SOURCE_DIR}/main.cpp
)

message( "Testing changeset")