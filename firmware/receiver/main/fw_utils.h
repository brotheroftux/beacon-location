#pragma once

#include "esp_log.h"
#include "nimble/ble.h"

#define RECEIVER_FIRM_TAG "receiver_fw"
#define RECEIVER_FIRM_LOG(log_level, fmt, ...) ESP_LOG_LEVEL_LOCAL( \
    log_level,                                                      \
    RECEIVER_FIRM_TAG,                                              \
    fmt,                                                            \
    ##__VA_ARGS__                                                   \
    )

void print_addr(uint8_t *addr) {
    for (uint8_t i = 0; i < 6; i++) {
        printf(i == 5 ? "%02x" : "%02x:", addr[i]);
    }

    printf("\n");
}

void print_bytestr(const uint8_t *byte_str, const uint8_t length) {
    char *char_str = malloc((length + 1) * sizeof(char));

    memcpy(char_str, byte_str, length);
    char_str[length] = '\0';

    printf("%s\n", char_str);

    free(char_str);
}
