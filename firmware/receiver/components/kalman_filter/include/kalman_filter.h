#pragma once

#ifndef RECEIVER_KALMAN_FILTER_H
#define RECEIVER_KALMAN_FILTER_H

#include "stdint.h"

#ifdef __cplusplus
extern "C" {
#endif

float kalman_filter_predict(const uint8_t *ble_addr, float mea);

#ifdef __cplusplus
}
#endif

#endif //RECEIVER_KALMAN_FILTER_H
