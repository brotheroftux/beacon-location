#include "scan_evt_store.h"
#include "vector.h"

static basic_vector_t evt_storage = MAKE_VECTOR();

static scan_evt_descriptor_t *convert_to_desc(struct ble_gap_disc_desc *disc_evt) {
    struct ble_hs_adv_fields fields;
    scan_evt_descriptor_t *desc = malloc(sizeof(scan_evt_descriptor_t));

    uint8_t *addr_copy = malloc(6 * sizeof(uint8_t));
    char *local_name = "__unspecified";

    ble_hs_adv_parse_fields(&fields, disc_evt->data, disc_evt->length_data);

    memcpy(addr_copy, disc_evt->addr.val, 6);

    if (fields.name_len > 0 && fields.name_is_complete && fields.name != NULL) {
        local_name = malloc(sizeof(char) * (fields.name_len + 1));
        memcpy(local_name, fields.name, fields.name_len);
        local_name[fields.name_len] = '\0';
    }

    desc->addr = addr_copy;
    desc->name = local_name;
    desc->rssi = disc_evt->rssi;

    return desc;
}

void scan_evt_store_init() {
    vector_reserve(&evt_storage, VECTOR_STARTING_CAPACITY);
}

void scan_evt_store_add(struct ble_gap_disc_desc *disc_evt) {
    scan_evt_descriptor_t *desc = convert_to_desc(disc_evt);

    if (strcmp(desc->name, "nimble-beacon") == 0) {
        vector_push_back(&evt_storage, desc);
    }
}

void scan_evt_store_get_evts(scan_evts_t *evts_out) {
    evts_out->count = evt_storage.size;
    evts_out->evts = (scan_evt_descriptor_t **) evt_storage.items;
}
