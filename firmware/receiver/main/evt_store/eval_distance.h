#pragma once

#include "math.h"
#include "kalman_filter.h"

float eval_distance(const uint8_t *addr, int8_t rssi, int8_t rssi_1m) {
    printf("rssi_1m = %d, rssi = %d\n", rssi_1m, rssi);

    float raw_distance = pow10f((float) (rssi_1m -  rssi) / 100.0f);

    printf("Raw distance = %f\n", raw_distance);

    return kalman_filter_predict(addr, raw_distance);
}
