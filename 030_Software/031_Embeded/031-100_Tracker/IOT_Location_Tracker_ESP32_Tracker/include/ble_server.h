/** @file ble_server.h
 * @brief This file provides the function of ble_server.cpp
 *  ****************************************************************
 *        **Name**            : ble_server.h
 *  <br>  **Project name**    : IoT - Localization ESP32 (Tracker)
 *  <br>  **Short name**      : BIC_IoT_LT_(T)
 *  <br>  **Author**          : Ramsauer René
 *  <br>  **Version**         : V0.1
 *  <br>  **Created on**      : 12.02.2023
 *  <br>  **Last change**     : 17.02.2023
 *  ****************************************************************
 *  @author René Ramsauer <ic18b066@technikum-wien.at>
 *
 * @version 0.1
 */
/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef INC_BLE_SERVER_H_
#define INC_BLE_SERVER_H_

/* Includes ------------------------------------------------------------------*/
#include <Arduino.h>

/* Define --------------------------------------------------------------------*/
/* Define which service is enabled. */
#define BETERRY_LVL_SERVICE_AKTIVATE (false)
#define UWB_SERVICE_AKTIVATE (true)

/* Prototypes ----------------------------------------------------------------*/
void initBLE(std::__cxx11::string bleDeviceName);  /** This function initiates the BLE server with a specific name. */
void bleLoop(uint8_t battLevelValue, std::__cxx11::string uwbShortID, std::__cxx11::string uwbUID);  /** This function provides an lopp for update BLE notfication. */
bool getBleServiceConnection();

#endif /* INC_BLE_SERVER_H_ */
