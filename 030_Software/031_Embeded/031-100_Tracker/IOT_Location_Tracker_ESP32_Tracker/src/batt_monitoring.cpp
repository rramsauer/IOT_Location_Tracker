/** @file batt_monitoring.ccp
 * @brief Battery montitoring.
 *  ****************************************************************
 *        **Name**            : batt_monitoring.ccp
 *  <br>  **Project name**    : IoT - Localization ESP32 (Tracker)
 *  <br>  **Short name**      : BIC_IoT_LT_(T)
 *  <br>  **Author**          : Ramauer René
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
#include <SPI.h>
#include <ArduinoLog.h>
#include <Arduino.h>
/* Header import */
#include "batt_monitoring.h"
#include "log_settings.h"

/* Function -----------------------------------------------------------------*/

/**
 * @brief Return Voltage of IO
 *
 *  This function returns the measured voltage in volts.
 *  With the define SHOW_VOLTAGE_BAT the battery voltage can be output serially.
 *
 * @param i_pin Value of analog pins. (Define at: pins_arduino.h)
 * @param f_voltIO Value of IO-Voltage.
 * @param i_resolutionADConverter Max resolution of the analog input PIN.
 *
 * @return [float] The Value of the calculated Voltage.
 *
 * @date                26.11.2022
 *
 * @version             V0.0
 *
 */
float batt_reatVoltage(int i_pin, float f_voltIO, int i_resolutionADConverter)
{
  Log.verboseln("CALL: batt_reatVoltage(%d,%F,%d)", i_pin,f_voltIO,i_resolutionADConverter);
  int i_temp_readVal=analogRead(i_pin);
  float i_temp_voltage=((f_voltIO/i_resolutionADConverter)*i_temp_readVal)/VOLTAGE_DEVIDER;
  showVoltage(i_temp_readVal,i_temp_voltage);
  Log.verboseln("RETURN: batt_reatVoltage() -> %F", i_temp_voltage);
  return i_temp_voltage;
}

/**
 * @brief Estimation the state of the battery charge level of LIPO
 *
 *  This function estimates the battery charge level of a "Polymer Li-ion Battery".
 *
 * @param voltage Battery voltage.
 * @param countCell Count of used battery cells.
 *
 * @return [int] Renturn % of Battery state.
 *
 * @date                05.12.2022
 *
 * @version             V0.0
 *
 */
int batt_estimationStateOfBatteryCharge_LIPO(float voltage, int countCell)
{
    Log.verboseln("CALL: batt_estimationStateOfBatteryCharge_LIPO(%F,%d)", voltage,countCell);
    int temp = (((voltage -(3*(float)countCell))/(float)countCell)/1.19 *100.);
    showStateBatt(temp);
    Log.verboseln("RETURN: batt_estimationStateOfBatteryCharge_LIPO() -> %d", temp);
    return (temp);
}