cmake_minimum_required(VERSION 3.5)

set(PROTO_COMPILER "protoc")
set(PROTO_C_COMPILER "protoc-c")
set(C_OUT_PATH "${CMAKE_CURRENT_LIST_DIR}/../components/proto_c")

set(PROTO_SRCS "schema.proto")

add_custom_target(c_proto
        COMMAND ${PROTO_C_COMPILER} --c_out=${C_OUT_PATH} -I . ${PROTO_SRCS}
        VERBATIM
        WORKING_DIRECTORY ${CMAKE_CURRENT_LIST_DIR}
        )
