FROM ubuntu:18.06

RUN apt-get -qq update && apt-get -qq install \
    wget \
    build-essential \
    python3-pip \
    g++
