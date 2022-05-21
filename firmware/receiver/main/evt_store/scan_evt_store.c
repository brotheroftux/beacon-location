#include "scan_evt_store.h"
#include "vector.h"
#include "schema.pb-c.h"
#include "eval_distance.h"

#include "esp_http_client.h"

#define SCAN_EVT_STORE_TAG "scan_evt_store"
#define SCAN_EVT_STORE_LOG(log_level, fmt, ...) ESP_LOG_LEVEL_LOCAL( \
    log_level,                                                      \
    SCAN_EVT_STORE_TAG,                                              \
    fmt,                                                            \
    ##__VA_ARGS__                                                   \
    )


#define RECEIVER_EVTDUMP_ENDPOINT CONFIG_RECEIVER_EVTDUMP_ENDPOINT

static esp_http_client_config_t http_client_config = {
        .url = RECEIVER_EVTDUMP_ENDPOINT,
        .method = HTTP_METHOD_PUT,
};

static esp_http_client_handle_t http_client_handle = NULL;
static basic_vector_t evt_storage = MAKE_VECTOR();

static ScanEventDescriptor *convert_to_desc(struct ble_gap_disc_desc *disc_evt, struct ble_hs_adv_fields *fields) {
    ScanEventDescriptor *desc = malloc(sizeof(ScanEventDescriptor));
    uint8_t *addr_copy = malloc(6 * sizeof(uint8_t));
    time_t current_ts;

    memcpy(addr_copy, disc_evt->addr.val, 6);

    ProtobufCBinaryData addr_field = {
            .data = addr_copy,
            .len = 6
    };

    scan_event_descriptor__init(desc);

    float distance = fields->tx_pwr_lvl_is_present
                     ? eval_distance(addr_copy, disc_evt->rssi, fields->tx_pwr_lvl)
                     : 0;

    time(&current_ts);

    desc->addr = addr_field;
    desc->ts = current_ts;
    desc->distance = distance;

    return desc;
}

static void free_desc(void *desc) {
    ScanEventDescriptor *desc_cast = desc;

    free(desc_cast->addr.data);
    free(desc_cast);
}

void scan_evt_store_init(void) {
    http_client_handle = esp_http_client_init(&http_client_config);

    if (http_client_handle == NULL) {
        esp_system_abort("Couldn't properly initialize HTTP client");
    }

    esp_http_client_set_header(http_client_handle, "Content-Type", "application/octet-stream");
    esp_http_client_set_header(http_client_handle, "Accept", "application/octet-stream");

    vector_reserve(&evt_storage, VECTOR_STARTING_CAPACITY);
}

void scan_evt_store_add(struct ble_gap_disc_desc *disc_evt) {
    struct ble_hs_adv_fields fields;
    ble_hs_adv_parse_fields(&fields, disc_evt->data, disc_evt->length_data);

    if (fields.name_is_complete && memcmp(fields.name, "nimble-beacon", fields.name_len) == 0) {
        ScanEventDescriptor *desc = convert_to_desc(disc_evt, &fields);

        vector_push_back(&evt_storage, desc);
    }
}

void scan_evt_store_sync(const uint8_t *own_addr) {
    ScanEventList evt_list;
    size_t packed_size;
    uint8_t *pack_buffer;
    time_t time_value;

    time(&time_value);

    ProtobufCBinaryData own_addr_field = {
            .data = (uint8_t *) own_addr,
            .len = 6,
    };

    scan_event_list__init(&evt_list);
    evt_list.events = (ScanEventDescriptor **) evt_storage.items;
    evt_list.n_events = evt_storage.size;
    evt_list.ts = time_value;
    evt_list.addr = own_addr_field;

    packed_size = scan_event_list__get_packed_size(&evt_list);

    pack_buffer = malloc(sizeof(uint8_t) * packed_size);
    scan_event_list__pack(&evt_list, pack_buffer);

    esp_http_client_set_post_field(http_client_handle, (char *) pack_buffer, (int) packed_size);

    esp_err_t rc = esp_http_client_perform(http_client_handle);
    free(pack_buffer);

    if (rc != ESP_OK) {
        SCAN_EVT_STORE_LOG(ESP_LOG_ERROR, "Failed to PUT accumulated scan events during scheduled sync procedure");
        SCAN_EVT_STORE_LOG(ESP_LOG_ERROR, "Endpoint responded with status = %d",
                           esp_http_client_get_status_code(http_client_handle));
        SCAN_EVT_STORE_LOG(ESP_LOG_WARN,
                           "ScanEvtStore will preserve currently accumulated records until the next sync");

        return;
    }

    SCAN_EVT_STORE_LOG(ESP_LOG_INFO, "Scheduled event sync: success");
    SCAN_EVT_STORE_LOG(ESP_LOG_INFO, "Endpoint responded with status = %d",
                       esp_http_client_get_status_code(http_client_handle));

    vector_clear(&evt_storage, free_desc);
}
