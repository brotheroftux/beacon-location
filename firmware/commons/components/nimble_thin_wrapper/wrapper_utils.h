#pragma once

#include "esp_log.h"

#define NIMBLE_WRAPPER_TAG "nimble_thin_wrapper"
#define NIMBLE_WRAPPER_LOG(log_level, fmt, ...) ESP_LOG_LEVEL_LOCAL( \
    log_level,                                                    \
    NIMBLE_WRAPPER_TAG,                                              \
    fmt,                                                          \
    ##__VA_ARGS__                                                 \
    )
