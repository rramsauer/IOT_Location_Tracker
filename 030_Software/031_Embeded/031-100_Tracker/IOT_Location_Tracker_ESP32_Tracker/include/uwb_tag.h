/** @file uwb_tag.h
 * @brief This file provides the function of uwb_tag.cpp
 *  ****************************************************************
 *        **Name**            : uwb_tag.h
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
/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef INC_UWB_SERVICE_H_
#define INC_UWB_SERVICE_H_

/* Includes ------------------------------------------------------------------*/
#include <Arduino.h>

/* Define -------------------------------------------------------------------*/
// Define Anchor Adress
#define ADRESS                      "6E:08:02:EC:52:40:3C:8D" // leftmost two bytes below will become the "short address"
// Settings Short Adr.
#define IS_RENDOM_SHORT_ADR         (true)

/* Prototypes ----------------------------------------------------------------*/
void uwbLoop(); /** This function init the uwb module as a Tag. */
void initUwb(); /** This function provides the functionalety of the uwb module. */

#endif /* INC_UWB_SERVICE_H_ */
