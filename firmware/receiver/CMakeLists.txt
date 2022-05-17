cmake_minimum_required(VERSION 3.5)

set(EXTRA_COMPONENT_DIRS $ENV{FW_SRC_ROOT}/commons/components/nimble_thin_wrapper)

include($ENV{IDF_PATH}/tools/cmake/project.cmake)
project(receiver C)

set(CMAKE_C_STANDARD 99)