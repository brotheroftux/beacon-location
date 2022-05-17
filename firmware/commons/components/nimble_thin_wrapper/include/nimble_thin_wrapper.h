#pragma once

#include <stdint-gcc.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef void after_sync_handler(void);

struct nimble_thin_init_opts {
    uint8_t sm_io_caps;
    char *device_name;
    after_sync_handler *after_sync;
};

void nimble_thin_init(struct nimble_thin_init_opts *opts, uint8_t *out_own_addr_type);

#ifdef __cplusplus
}
#endif