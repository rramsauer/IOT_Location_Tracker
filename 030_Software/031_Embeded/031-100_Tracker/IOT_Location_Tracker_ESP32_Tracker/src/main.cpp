/** @file main.c
 * @brief Main program body
 *  ****************************************************************
 *        **Name**            : main.c
 *  <br>  **Project name**    : IoT - Localization ESP32 (Tracker)
 *  <br>  **Short name**      : BIC_IoT_LT_(T)
 *  <br>  **Author**          : Ramsauer René
 *  <br>  **Version**         : V0.0
 *  <br>  **Created on**      : 05.02.2023
 *  <br>  **Last change**     : 05.19.2023
 *  ****************************************************************
 *  @author René Ramsauer <ic18b066@technikum-wien.at>
 *
 * @version 0.1
 */

/* Includes ------------------------------------------------------------------*/
/* Libary import*/
#include <Arduino.h>
#include <ArduinoLog.h>
#include <SPI.h>
#include "DW1000.h"
/* Header import */
#include "log_settings.h"
#include "uwb_tag.h"
#include "ble_server.h"
#include "batt_monitoring.h"

/* Variable ------------------------------------------------------------------*/
char shortAdress[128]; // uwb short Adr
char uniqueIdentifier[128]; // uwb unique identifier

/* Setup  --------------------------------------------------------------------*/
/**
 * @brief Set up program.
 *
 * @date                18.02.2023
 *
 * @version             V0.1
 *
 */
void setup()
{
    /* START init Logging */
    // Start serial
    Serial.begin(115200);
    delay(1000); // Deley for init serial.
    // Start logging.
    Log.begin(DEBUG_LV, &Serial); //DEBUG_LV is defined in log_settings.h
    /* END init Logging */

    /* START init */
    // Init UWB Module DW1000.
    initUwb();
    // Init BLE server with device name.
    initBLE("IOT-Location Tracker 2");
    /* END init */

    /* START init variable */
    #if UWB_SERVICE_AKTIVATE
        // Get short address and unique identifier to share it via ble.
        DW1000.getPrintableShortAddress(shortAdress);
        // Get short address and unique identifier to share it via ble.
        DW1000.getPrintableExtendedUniqueIdentifier(uniqueIdentifier);
    #else
        sprintf(shortAdress,"");
        sprintf(uniqueIdentifier,"");
    #endif
    /* END init variable */
}

void(* resetFunc) (void) = 0;

/* Main loop  ----------------------------------------------------------------*/
/**
 * @brief Main loop of program.
 *
 * @date                19.02.2023
 *
 * @version             V0.1
 *
 */
void loop()
{
    /* START loop call */
    uwbLoop();
    #if BETERRY_LVL_SERVICE_AKTIVATE
        int battState = batt_estimationStateOfBatteryCharge_LIPO(batt_reatVoltage(34, VOLTAGE_IO, RESOLUTION_ADC), 1);
        bleLoop(battState, shortAdress, uniqueIdentifier);
    #else
        bleLoop(0, shortAdress, uniqueIdentifier);
    #endif
    /* END loop call */

    /* START Define loop interval */
    delay(10);
    /* END Define loop interval */
}