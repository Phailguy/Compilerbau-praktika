cmake_minimum_required(VERSION 4.1)
project(untitled)

set(CMAKE_CXX_STANDARD 20)

add_executable(untitled main.cpp
        Token.cpp
        Token.h
        Tokenizer.cpp
        RefCounter.cpp
        RefCounter.h
        SmartToken.cpp
        SmartToken.h
        RingBuffer.cpp
        RingBuffer.h
        Tokenizer.h)
