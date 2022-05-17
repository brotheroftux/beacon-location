#include "fw_utils.h"

#include "host/ble_gap.h"
#include "services/gap/ble_svc_gap.h"

#include "nimble_thin_wrapper.h"

uint8_t beacon_own_addr_type;

static void beacon_adv_start(void);

static int gap_event_handler(struct ble_gap_event *event, __attribute__((unused)) void *arg) {
    switch (event->type) {
        case BLE_GAP_EVENT_ADV_COMPLETE:
            BEACON_FIRM_LOG(ESP_LOG_INFO, "ADV complete w/ reason %d", event->adv_complete.reason);
            beacon_adv_start();

            return 0;

        default:
            return 0;
    }
}

static void beacon_adv_start(void) {
    BEACON_FIRM_LOG(ESP_LOG_INFO, "Starting ADV...");

    const char *name = ble_svc_gap_device_name();

    struct ble_gap_adv_params adv_params = {
            .conn_mode = BLE_GAP_CONN_MODE_NON,
            .disc_mode = BLE_GAP_DISC_MODE_GEN,
    };

    struct ble_hs_adv_fields fields = {
            .name = (uint8_t *) name,
            .name_len = strlen(name),
            .name_is_complete = 1,
    };

    int rc = ble_gap_adv_set_fields(&fields);

    if (rc != 0) {
        BEACON_FIRM_LOG(ESP_LOG_ERROR, "Failed to set ADV data, reason %d", rc);

        return;
    }

    rc = ble_gap_adv_start(
            beacon_own_addr_type,
            NULL,
            BLE_HS_FOREVER,
            &adv_params,
            gap_event_handler,
            NULL
    );

    if (rc != 0) {
        BEACON_FIRM_LOG(ESP_LOG_ERROR, "Failed to start ADV, reason %d", rc);

        return;
    }
}

static struct nimble_thin_init_opts wrapper_init_opts = {
        .sm_io_caps = BLE_SM_IO_CAP_NO_IO,
        .device_name = "nimble-beacon",
        .after_sync = beacon_adv_start,
};

void app_main(void) {
    nimble_thin_init(&wrapper_init_opts, &beacon_own_addr_type);
}
