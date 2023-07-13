/** @file ble_server.cpp
 * @brief This file provides a BLE server
 *  ****************************************************************
 *        **Name**            : ble_server.cpp
 *  <br>  **Project name**    : IoT - Localization ESP32 (Tracker)
 *  <br>  **Short name**      : BIC_IoT_LT_(T)
 *  <br>  **Author**          : Ramsauer René
 *  <br>  **Version**         : V0.2
 *  <br>  **Created on**      : 12.02.2023
 *  <br>  **Last change**     : 17.02.2023
 *  ****************************************************************
 *  @author René Ramsauer <ic18b066@technikum-wien.at>
 *
 * @version 0.1
 */

/* Includes ------------------------------------------------------------------*/
/* Libary import*/
#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <BLE2902.h>
/* Header import */
#include "ble_server.h"
#include "log_settings.h"

/* Define -------------------------------------------------------------------*/
//Battery Level
#if BETERRY_LVL_SERVICE_AKTIVATE
    #define BATTERY_LVL_SERVICE_UUID          BLEUUID((uint16_t)0x180F)
    #define BATTERY_LVL_CHARACTERISTIC_UUID   BLEUUID((uint16_t)0x2A19)
#endif
// UWB short Adr.
#if UWB_SERVICE_AKTIVATE
    #define UWB_SERVICE_UUID                  "69C94BB5-1F81-45FC-B4A9-46877EFB52FD"
    #define UWB_ID_SHORT_CHARACTERISTIC_UUID  "0F81C26B-1E6C-46DE-9D7A-15B5D54D2386"
    #define UWB_UID_CHARACTERISTIC_UUID       "7CC63E05-DCD5-4E67-A744-766267E363A1"
#endif

/* Variable -----------------------------------------------------------------*/
// BLE connect for Callback
bool _BLEClientConnected = false;
//Battery Level
#if BETERRY_LVL_SERVICE_AKTIVATE
    BLECharacteristic batteryLevelCharacteristic(BATTERY_LVL_CHARACTERISTIC_UUID, BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY);
    BLEDescriptor batteryLevelDescriptor(BLEUUID((uint16_t)0x2901));
#endif
// UWB service
#if UWB_SERVICE_AKTIVATE
    BLECharacteristic uwbShortIdCharacteristics(UWB_ID_SHORT_CHARACTERISTIC_UUID, BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY);
    BLEDescriptor uwbShortIdDescriptor(BLEUUID((uint16_t)0x2901));
    BLECharacteristic uwbUidCharacteristics(UWB_UID_CHARACTERISTIC_UUID, BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY);
    BLEDescriptor uwbUidDescriptor(BLEUUID((uint16_t)0x2901));
#endif
// UWB unique identifire
#if UWB_UID_SERVICE_UUID_AKTIVATE

#endif

/* BLE Callback --------------------------------------------------------------*/
/**
 * @brief This Class manage the Callback
 * 
 * This is an 3rd-Party impl.
 *
 * @see                     https://randomnerdtutorials.com/esp32-ble-server-client/
 * 
 * @date                    29.06.2022
 *
 * @version                 V0.1
 *
 */
class MyServerCallbacks : public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
        _BLEClientConnected = true;
    };
    void onDisconnect(BLEServer* pServer) {
        _BLEClientConnected = false;
    }
};

/* Function -----------------------------------------------------------------*/
/**
 * @brief This function initiates the BLE server with a specific name.
 *
 * @param bleDeviceName     BLE-Device-Name
 *
 * @date                    29.06.2022
 *
 * @version                 V0.1
 *
 */
void initBLE(std::__cxx11::string bleDeviceName) {
    BLEDevice::init(bleDeviceName);
    // Create the BLE Server
    BLEServer *pServer = BLEDevice::createServer();
    pServer->setCallbacks(new MyServerCallbacks());

    // Create BLE Service for Battery Level
    #if BETERRY_LVL_SERVICE_AKTIVATE
        BLEService *pBattery = pServer->createService(BATTERY_LVL_SERVICE_UUID);
        pBattery->addCharacteristic(&batteryLevelCharacteristic);
        batteryLevelDescriptor.setValue("Percentage 0 - 100");
        batteryLevelCharacteristic.addDescriptor(&batteryLevelDescriptor);
        batteryLevelCharacteristic.addDescriptor(new BLE2902());
        pServer->getAdvertising()->addServiceUUID(BATTERY_LVL_SERVICE_UUID);
        pBattery->start();
    #endif

    // Create BLE Service for uwb tag indification
    #if UWB_SERVICE_AKTIVATE
        BLEService *pUwb = pServer->createService(UWB_SERVICE_UUID);
        /* UWB Short Adress */
        pUwb->addCharacteristic(&uwbShortIdCharacteristics);
        uwbShortIdDescriptor.setValue("UWB Short Adr.");
        uwbShortIdCharacteristics.addDescriptor(&uwbShortIdDescriptor);
        uwbShortIdCharacteristics.addDescriptor(new BLE2902());
        /* UWB UID */
        pUwb->addCharacteristic(&uwbUidCharacteristics);
        uwbUidDescriptor.setValue("UWB UID");
        uwbUidCharacteristics.addDescriptor(&uwbUidDescriptor);
        uwbUidCharacteristics.addDescriptor(new BLE2902());

        pServer->getAdvertising()->addServiceUUID(UWB_SERVICE_UUID);
        pUwb->start();
    #endif

    // Start advertising
    pServer->getAdvertising()->start();
}

/**
 * @brief This function provides an lopp for update BLE notfication.
 *
 * @param battLevelValue     Pass the current value of the Battery LVL.
 * @param uwbShortID         Pass the aktual uwb short id  of the module.
 * @param uwbUID             Pass the aktual unique identifire of the module.
 *
 * @date                    17.02.2023
 *
 * @version                 V0.2
 *
 */
void bleLoop(uint8_t battLevelValue, std::__cxx11::string uwbShortID, std::__cxx11::string uwbUID) {
    // Set value of Battery Level
    #if BETERRY_LVL_SERVICE_AKTIVATE
        batteryLevelCharacteristic.setValue(&battLevelValue, 1);
        batteryLevelCharacteristic.notify();
    #endif
    // Set short adr. of uwb
    #if UWB_SERVICE_AKTIVATE
        uwbShortIdCharacteristics.setValue(uwbShortID);
        uwbUidCharacteristics.setValue(uwbUID);
        uwbUidCharacteristics.notify();
    #endif
}

bool getBleServiceConnection(){
    return _BLEClientConnected;
}