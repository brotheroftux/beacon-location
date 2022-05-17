#include "fw_utils.h"
#include "evt_store/scan_evt_store.h"
#include "receiver_wifi/wifi.h"
#include "receiver_time/receiver_time.h"

#include "nvs_flash.h"
#include "nimble_thin_wrapper.h"
#include "host/ble_gap.h"

static void scan(void);

static uint8_t own_addr_type;
static struct nimble_thin_init_opts wrapper_init_opts = {
        .after_sync = scan,
        .device_name = "nimble-receiver",
        .sm_io_caps = BLE_SM_IO_CAP_NO_IO,
};

static void log_disc_evts(scan_evts_t *list) {
    size_t evt_count = list->count;

    RECEIVER_FIRM_LOG(ESP_LOG_INFO, "Got %d scan event(s)", evt_count);

    for (size_t i = 0; i < evt_count; i++) {
        RECEIVER_FIRM_LOG(ESP_LOG_INFO, "Event #%d, rssi = %d dBm", i + 1, list->evts[i]->rssi);
        print_addr(list->evts[i]->addr);
    }
}

static int scan_callback(struct ble_gap_event *event, __attribute__((unused)) void *arg) {
    switch (event->type) {
        case BLE_GAP_EVENT_DISC:
            RECEIVER_FIRM_LOG(ESP_LOG_INFO, "BLE_GAP_EVENT_DISC");
            scan_evt_store_add(&event->disc);

            return 0;
        case BLE_GAP_EVENT_DISC_COMPLETE:
            RECEIVER_FIRM_LOG(ESP_LOG_INFO, "BLE_GAP_EVENT_DISC_COMPLETE");
            scan_evts_t evts;

            scan_evt_store_get_evts(&evts);
            log_disc_evts(&evts);

            return 0;
        default:
            return 0;
    }
}

static void scan(void) {
    struct ble_gap_disc_params disc_params = {
            .itvl = 10,
            .window = 10,
            .passive = true,
            .limited = false,
            .filter_duplicates = true,
            .filter_policy = BLE_HCI_CONN_FILT_NO_WL,
    };

    ble_gap_disc(own_addr_type, 0, &disc_params, scan_callback, NULL);
}

void app_main(void) {
    RECEIVER_FIRM_LOG(ESP_LOG_INFO, "Starting receiver FW...");

    RECEIVER_FIRM_LOG(ESP_LOG_INFO, "Initializing NVS...");
    esp_err_t nvs_init_rc = nvs_flash_init();

    if (nvs_init_rc == ESP_ERR_NVS_NO_FREE_PAGES || nvs_init_rc == ESP_ERR_NVS_NEW_VERSION_FOUND) {
        ESP_ERROR_CHECK(nvs_flash_erase());
        nvs_init_rc = nvs_flash_init();
    }

    ESP_ERROR_CHECK(nvs_init_rc);

    RECEIVER_FIRM_LOG(ESP_LOG_INFO, "Initializing wireless connectivity...");
    receiver_wifi_connect();

    RECEIVER_FIRM_LOG(ESP_LOG_INFO, "receiver_sync_time in progress");
    receiver_sync_time();
    RECEIVER_FIRM_LOG(ESP_LOG_INFO, "receiver_sync_time done");

    scan_evt_store_init();

    RECEIVER_FIRM_LOG(ESP_LOG_INFO, "nimble_thin_init");
    nimble_thin_init(&wrapper_init_opts, &own_addr_type);
}
