#include <map>
#include <string>
#include <memory>

#include "SimpleKalmanFilter/src/SimpleKalmanFilter.h"

#include "kalman_filter.h"

#define DEFAULT_MEA_E 1
#define DEFAULT_EST_E 1
#define DEFAULT_Q 0.01

static std::map<std::string, std::shared_ptr<SimpleKalmanFilter>> bleAddressToFilter;

static void kalman_filter_init(const std::string &bleAddStr) {
    std::shared_ptr<SimpleKalmanFilter> filter(
            new SimpleKalmanFilter(DEFAULT_MEA_E, DEFAULT_EST_E, DEFAULT_Q));

    bleAddressToFilter[bleAddStr] = filter;
}

float kalman_filter_predict(const uint8_t *bleAddr, float mea) {
    std::string bleAddrStr(bleAddr, bleAddr + 6);

    if (bleAddressToFilter.count(bleAddrStr) == 0) {
        kalman_filter_init(bleAddrStr);
    }

    auto val = bleAddressToFilter.at(bleAddrStr);

    return val->updateEstimate(mea);
}
