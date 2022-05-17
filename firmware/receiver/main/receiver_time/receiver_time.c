#include "freertos/FreeRTOS.h"
#include "freertos/event_groups.h"

#include "esp_sntp.h"

#define SNTP_SYNC_COMPLETE_BIT BIT0

static EventGroupHandle_t sync_event_group_handle;

void sync_cb(__attribute__((unused)) struct timeval *tv) {
    return (void) xEventGroupSetBits(sync_event_group_handle, SNTP_SYNC_COMPLETE_BIT);
}

void receiver_sync_time(void) {
    setenv("TZ", "UTC", 1);
    tzset();

    sync_event_group_handle = xEventGroupCreate();

    sntp_set_time_sync_notification_cb(sync_cb);
    sntp_setoperatingmode(SNTP_OPMODE_POLL);
    sntp_setservername(0, "pool.ntp.org");
    sntp_init();

    EventBits_t bits = xEventGroupWaitBits(
            sync_event_group_handle,
            SNTP_SYNC_COMPLETE_BIT,
            pdFALSE,
            pdFALSE,
            10000 / portTICK_PERIOD_MS);

    if (!(bits & SNTP_SYNC_COMPLETE_BIT)) {
        esp_system_abort("Couldn't sync time via SNTP... in time");
    }

    sntp_set_time_sync_notification_cb(NULL);
}
