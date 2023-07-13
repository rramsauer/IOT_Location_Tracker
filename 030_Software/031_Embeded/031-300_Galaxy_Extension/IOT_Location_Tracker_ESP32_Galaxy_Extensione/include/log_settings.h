/** @file log_settings.h
 * @brief This file is used to configure the log level. 
 *  ****************************************************************
 *        **Name**            : log_settings.h
 *  <br>  **Project name**    : IoT - Localization ESP32 (Galaxy Extension)
 *  <br>  **Short name**      : BIC_IoT_LT_ESP32_GE(A)
 *  <br>  **Author**          : Ramauer René
 *  <br>  **Version**         : V0.0
 *  <br>  **Created on**      : 05.12.2022
 *  <br>  **Last change**     : 05.12.2022
 *  ****************************************************************
 *  @author René Ramsauer <ic18b066@technikum-wien.at>
 *
 * @version 0.1
 */

/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __LOG_SETTINGS_H
#define __LOG_SETTINGS_H

/* Includes ------------------------------------------------------------------*/
#include <ArduinoLog.h>

/*Start User Settings*/
/*
 * 0 - LOG_LEVEL_SILENT     no output
 * 1 - LOG_LEVEL_FATAL      fatal errors
 * 2 - LOG_LEVEL_ERROR      all errors
 * 3 - LOG_LEVEL_WARNING    errors and warnings
 * 4 - LOG_LEVEL_INFO       errors, warnings and notices
 * 4 - LOG_LEVEL_NOTICE     Same as INFO, kept for backward compatibility
 * 5 - LOG_LEVEL_TRACE      errors, warnings, notices, traces
 * 6 - LOG_LEVEL_VERBOSE    all
 */
#define DEBUG_LV                        (LOG_LEVEL_VERBOSE)
#define SHOW_UWB_DEVICE_INFO            (true)
/*End User Settings*/

/*Preprocessor stadement  ----------------------------------------------------*/
// UWB DEVICE INFO
#if SHOW_UWB_DEVICE_INFO
  #define logUwbDeviceInfo(msg,data) Log.verboseln("DEVICE INFO: %s: %s ", msg, data)
#else
  #define logDistance(msg,data)
#endif

#endif /* __LOG_SETTINGS_H */