#pragma once

#include "host/ble_gap.h"

typedef struct scan_evt_descriptor {
    uint8_t *addr;
    char *name;
    int8_t rssi;
} scan_evt_descriptor_t;

typedef struct scan_evts {
    scan_evt_descriptor_t **evts;
    size_t count;
} scan_evts_t;

void scan_evt_store_init(void);
void scan_evt_store_add(struct ble_gap_disc_desc *disc_evt);
void scan_evt_store_get_evts(scan_evts_t *evts_out);
void scan_evt_store_sync(const uint8_t *own_addr);

