#pragma once

#include "esp_log.h"

#define BEACON_FIRM_TAG "beacon_fw"
#define BEACON_FIRM_LOG(log_level, fmt, ...) ESP_LOG_LEVEL_LOCAL( \
    log_level,                                                    \
    BEACON_FIRM_TAG,                                              \
    fmt,                                                          \
    ##__VA_ARGS__                                                 \
    )
