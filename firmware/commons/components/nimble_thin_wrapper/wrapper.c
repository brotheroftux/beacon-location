#include "nimble_thin_wrapper.h"
#include "wrapper_utils.h"

#include "esp_nimble_hci.h"
#include "nimble/nimble_port.h"
#include "nimble/nimble_port_freertos.h"
#include "host/ble_hs.h"
#include "host/util/util.h"
#include "services/gap/ble_svc_gap.h"

static uint8_t *own_addr_type_ptr;
static struct nimble_thin_init_opts *init_opts_ptr;

static void stack_reset_handler(int reason) {
    NIMBLE_WRAPPER_LOG(ESP_LOG_ERROR, "NimBLE stack reset. Reason = %d", reason);
}

static void stack_sync_handler(void) {
    int rc = ble_hs_util_ensure_addr(false);
    assert(rc == 0);

    rc = ble_hs_id_infer_auto(false, own_addr_type_ptr);

    if (rc != 0) {
        NIMBLE_WRAPPER_LOG(ESP_LOG_ERROR, "Could not determine address type, reason = %d", rc);

        return;
    }

    init_opts_ptr->after_sync();
}

static void ble_host_task(__attribute__((unused)) void *args) {
    NIMBLE_WRAPPER_LOG(ESP_LOG_INFO, "Host task started.");

    nimble_port_run();
    nimble_port_freertos_deinit();
}

void nimble_thin_init(struct nimble_thin_init_opts *opts_ptr, uint8_t *out_own_addr_type) {
    NIMBLE_WRAPPER_LOG(ESP_LOG_INFO, "init");

    init_opts_ptr = opts_ptr;
    own_addr_type_ptr = out_own_addr_type;

    struct nimble_thin_init_opts opts = *init_opts_ptr;

    ESP_ERROR_CHECK(esp_nimble_hci_and_controller_init());

    nimble_port_init();

    ble_hs_cfg.sm_io_cap = opts.sm_io_caps;
    ble_hs_cfg.reset_cb = stack_reset_handler;
    ble_hs_cfg.sync_cb = stack_sync_handler;

    int rc = ble_svc_gap_device_name_set(opts.device_name);
    assert(rc == 0);

    nimble_port_freertos_init(ble_host_task);
}