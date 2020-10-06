FROM ubuntu:18.04

RUN apt-get -qq update && apt-get -qq install \
    build-essential \
    python3-pip \
    g++

RUN apt-get update && apt-get install -y --no-install-recommends \
    libssl-dev

ENV CMAKE_VERSION=3.18.1
RUN cd /tmp && wget --quiet --output-document=cmake.tar.gz https://github.com/Kitware/CMake/releases/download/v${CMAKE_VERSION}/cmake-${CMAKE_VERSION}.tar.gz \
    && tar -xzf cmake.tar.gz \
    && cd cmake-${CMAKE_VERSION} \
    && ./bootstrap --parallel=4 \
    && make -j4 \
    && make install \
    && rm -rf /tmp/cmake*

