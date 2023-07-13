/** @file uwb_anchor.h
 * @brief This file provides the function of uwb_anchor.cpp
 *  ****************************************************************
 *        **Name**            : uwb_anchor.h
 *  <br>  **Project name**    : IoT - Localization ESP32 (Galaxy Extension)
 *  <br>  **Short name**      : BIC_IoT_LT_ESP32_GE(A)
 *  <br>  **Author**          : Ramsauer René
 *  <br>  **Version**         : V0.2
 *  <br>  **Created on**      : 12.02.2023
 *  <br>  **Last change**     : 17.02.2023
 *  ****************************************************************
 *  @author René Ramsauer <ic18b066@technikum-wien.at>
 *
 * @version 0.1
 */
/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef INC_UWB_SERVICE_H_
#define INC_UWB_SERVICE_H_

/* Includes ------------------------------------------------------------------*/
#include <Arduino.h>

/* Define -------------------------------------------------------------------*/
// Define Anchor Adress
#define ADRESS                      "84:00:5B:D5:A9:9A:E2:9C" // leftmost two bytes below will become the "short address"
// Settings Short Adr.
#define IS_RENDOM_SHORT_ADR             (true)
// Write measuring data to seriel
#define WRITE_MEASURING_DATA_TO_SERIEL  (true)

/* Prototypes ----------------------------------------------------------------*/
void uwbLoop(); /** This function init the uwb module as a Anchor. */
void initUwb(); /** This function provides the functionalety of the uwb module. */

#if WRITE_MEASURING_DATA_TO_SERIEL
  #define writeMeasuringDataToSeriel(address,distance,rssi) ({ \
  Serial.print(";;");\
  Serial.print(address);\
  Serial.print(";;");\
  Serial.print(distance);\
  Serial.print(";;");\
  Serial.println(rssi);\
  })
#else
  #define writeMeasuringDataToSeriel(address,distance,rssi)
#endif


#endif /* INC_UWB_SERVICE_H_ */