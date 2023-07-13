/** @file uwb_tag.cpp
 * @brief This file provides a UWB-Tag
 *  ****************************************************************
 *        **Name**            : uwb_tag.cpp
 *  <br>  **Project name**    : IoT - Localization ESP32 (Tracker)
 *  <br>  **Short name**      : BIC_IoT_LT_(T)
 *  <br>  **Author**          : Ramsauer René
 *  <br>  **Version**         : V0.0
 *  <br>  **Created on**      : 05.12.2022
 *  <br>  **Last change**     : 05.12.2022
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
#include "DW1000Ranging.h"
#include "DW1000.h"
/* Header import */
#include "log_settings.h"
#include "uwb_tag.h"

/* Define -------------------------------------------------------------------*/
// Define calibtatet param
#define ATENNEN_DELAY               ((uint16_t) 16607) //calibrated Antenna Delay setting for this anchor
#define CALIBRATION_DISTANCE_M      ((float)((285 - 1.75) * 0.0254)) // calibration distance
// Define SPI PIN
#define SPI_SCK                     (18)
#define SPI_MISO                    (19)
#define SPI_MOSI                    (23)
#define DW_CS                       (4)

/* Variable -----------------------------------------------------------------*/
// connection pins
const uint8_t PIN_RST = 27; // reset pin
const uint8_t PIN_IRQ = 34; // irq pin
const uint8_t PIN_SS = 4;   // spi select pin

/* Function prototypes ------------------------------------------------------*/
void newRange(); /** This function is called if a new distance is measured. */
void newDevice(DW1000Device *device); /** This function is called if a new device is detected. */
void inactiveDevice(DW1000Device *device); /** This function is called if a device was detected but does nothing. */


/* Function ----------------------------------------------------------------*/

/**
 * @brief This function init the uwb module as a Tag.
 *
 * This function init the uwb modul Dakewave DW1000 as a Tag.
 *
 * @date                18.02.2023
 *
 * @version             V0.1
 *
 */
void initUwb()
{
    // init SPI configuration
    SPI.begin(SPI_SCK, SPI_MISO, SPI_MOSI);

    //init the configuration of uwb
    DW1000Ranging.initCommunication(PIN_RST, PIN_SS, PIN_IRQ); //Reset, CS, IRQ pin
    DW1000Ranging.attachNewRange(newRange);
    DW1000Ranging.attachNewDevice(newDevice);
    DW1000Ranging.attachInactiveDevice(inactiveDevice);

    // Start as tag TODO: (do not assign random short address)
    DW1000Ranging.startAsTag(ADRESS, DW1000.MODE_LONGDATA_RANGE_LOWPOWER, IS_RENDOM_SHORT_ADR);

    // Print uwb device information
    char msg[128];
    DW1000.getPrintableDeviceIdentifier(msg);
    logUwbDeviceInfo("UWB Device ID:",msg);
    DW1000.getPrintableExtendedUniqueIdentifier(msg);
    logUwbDeviceInfo("UWB Unique ID:",msg);
    DW1000.getPrintableNetworkIdAndShortAddress(msg);
    logUwbDeviceInfo("Network ID & Device Address:",msg);
    DW1000.getPrintableDeviceMode(msg);
    logUwbDeviceInfo("Device mode:",msg);
}

/**
 * @brief This function provides the functionalety of the uwb module.
 *
 * This function provides the functionalety of the uwb module Dakewave DW1000.
 *
 * @date                18.02.2023
 *
 * @version             V0.1
 *
 */
void uwbLoop()
{
    DW1000Ranging.loop();
}

/**
 * @brief This function is called if a new distance is measured.
 *
 * This function is called when the function uwbLoop is called and a UWB anchor is reachable.
 *
 * @date                18.02.2023
 *
 * @version             V0.1
 *
 */
void newRange()
{
    logUwbDistance(DW1000Ranging.getDistantDevice()->getPrintableShortAddress(),DW1000Ranging.getDistantDevice()->getRange(),DW1000Ranging.getDistantDevice()->getRXPower());
}

/**
 * @brief This function is called if a new device is detected.
 *
 * This function is called when the function uwbLoop is called and a new device is detected.
 *
 * @param device        Pointer of DW1000Device opject. For getting device information of connected device.
 *
 * @date                18.02.2023
 *
 * @version             V0.1
 *
 */
void newDevice(DW1000Device *device)
{
    Log.verboseln("NEW UWB DEVICE: %lX", device->getPrintableShortAddress());
}

/**
 * @brief This function is called if a device was detected but does nothing.
 *
 * This function is called when the function uwbLoop is called and a device was detected but does nothing.
 *
 * @param device        Pointer of DW1000Device opject. For getting device information of connected device.
 * 
 * @date                18.02.2023
 *
 * @version             V0.1
 *
 */
void inactiveDevice(DW1000Device *device)
{
    Log.verboseln("DELETE INACTIVE UWB DEVICE: %X", device->getPrintableShortAddress());
}