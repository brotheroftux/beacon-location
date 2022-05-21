#pragma once

#include "host/ble_gap.h"

void scan_evt_store_init(void);
void scan_evt_store_add(struct ble_gap_disc_desc *disc_evt);
void scan_evt_store_sync(const uint8_t *own_addr);

