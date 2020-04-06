FROM ubuntu:18.06

RUN apt-get -qq update && apt-get -qq install \
    build-essential \
    g++ \
    python3-pip \
    nettools \
    wget
