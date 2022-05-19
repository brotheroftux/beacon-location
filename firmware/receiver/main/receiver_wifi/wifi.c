#include "freertos/FreeRTOS.h"
#include "freertos/event_groups.h"

#include "esp_wifi.h"
#include "esp_log.h"
#include "esp_event.h"

#define WIFI_LOG_TAG "receiver_wifi"
#define WIFI_LOG(log_level, fmt, ...) ESP_LOG_LEVEL_LOCAL( \
    log_level,                                                      \
    WIFI_LOG_TAG,                                              \
    fmt,                                                            \
    ##__VA_ARGS__                                                   \
    )

#define RECEIVER_WIFI_SSID CONFIG_RECEIVER_WIFI_SSID
#define RECEIVER_WIFI_PASSWORD CONFIG_RECEIVER_WIFI_PASSWORD
#define RECEIVER_WIFI_MAX_RETRIES 2

// WiFi connection routine states
#define WIFI_STA_STARTED_BIT  BIT0
#define WIFI_OBTAINED_IP_BIT  BIT1
#define WIFI_DISCONNECTED_BIT BIT2

static EventGroupHandle_t wifi_event_group_handle;

static void wifi_evt_handler(__attribute__((unused)) void *arg, esp_event_base_t event_base, int32_t event_id,
                             __attribute__((unused)) void *event_data) {
    if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_START) {
        WIFI_LOG(ESP_LOG_INFO, "WIFI_EVENT_STA_START");

        return (void) xEventGroupSetBits(wifi_event_group_handle, WIFI_STA_STARTED_BIT);
    }

    if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_DISCONNECTED) {
        WIFI_LOG(ESP_LOG_WARN, "WIFI_EVENT_STA_DISCONNECTED");

        return (void) xEventGroupSetBits(wifi_event_group_handle, WIFI_DISCONNECTED_BIT);
    }

    if (event_base == IP_EVENT && event_id == IP_EVENT_STA_GOT_IP) {
        WIFI_LOG(ESP_LOG_INFO, "IP_EVENT_STA_GOT_IP");

        return (void) xEventGroupSetBits(wifi_event_group_handle, WIFI_OBTAINED_IP_BIT);
    }
}

static void sta_connect(void) {
    static int connect_retry_count = 0;

    esp_wifi_connect();

    EventBits_t bits = xEventGroupWaitBits(
            wifi_event_group_handle,
            WIFI_OBTAINED_IP_BIT | WIFI_DISCONNECTED_BIT,
            pdFALSE,
            pdFALSE,
            portMAX_DELAY);

    if (bits & WIFI_OBTAINED_IP_BIT) {
        connect_retry_count = 0;

        return;
    }

    if (bits & WIFI_DISCONNECTED_BIT && ++connect_retry_count < RECEIVER_WIFI_MAX_RETRIES) {
        sta_connect();
    }

    esp_system_abort("Couldn't connect to AP");
}

void receiver_wifi_connect(void) {
    wifi_init_config_t init_config = WIFI_INIT_CONFIG_DEFAULT();
    esp_event_handler_instance_t wifi_evt_handler_instance, ip_evt_handler_instance;

    wifi_event_group_handle = xEventGroupCreate();

    ESP_ERROR_CHECK(esp_netif_init());
    ESP_ERROR_CHECK(esp_event_loop_create_default());
    esp_netif_create_default_wifi_sta();

    ESP_ERROR_CHECK(esp_wifi_init(&init_config));

    ESP_ERROR_CHECK(esp_event_handler_instance_register(
            WIFI_EVENT,
            ESP_EVENT_ANY_ID,
            wifi_evt_handler,
            NULL,
            &wifi_evt_handler_instance));

    ESP_ERROR_CHECK(esp_event_handler_instance_register(
            IP_EVENT,
            IP_EVENT_STA_GOT_IP,
            wifi_evt_handler,
            NULL,
            &ip_evt_handler_instance));

    wifi_config_t wifi_config = {
            .sta = {
                    .ssid = RECEIVER_WIFI_SSID,
                    .password = RECEIVER_WIFI_PASSWORD,
                    .threshold.authmode = WIFI_AUTH_WPA2_PSK,
            },
    };

    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
    ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_STA, &wifi_config));
    ESP_ERROR_CHECK(esp_wifi_start());

    WIFI_LOG(ESP_LOG_INFO, "Waiting for STA to start..");

    xEventGroupWaitBits(
            wifi_event_group_handle,
            WIFI_STA_STARTED_BIT,
            pdFALSE,
            pdFALSE,
            portMAX_DELAY);

    sta_connect();
    WIFI_LOG(ESP_LOG_INFO, "Successfully connected to the specified AP");

    vEventGroupDelete(wifi_event_group_handle);
}
