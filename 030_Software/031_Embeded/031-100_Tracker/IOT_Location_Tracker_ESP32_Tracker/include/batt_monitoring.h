/** @file batt_monitoring.h
 * @brief This header share an Battery montitoring service.
 *  ****************************************************************
 *        **Name**            : batt_monitoring.h
 *  <br>  **Project name**    : IoT - Localization ESP32 (Tracker)
 *  <br>  **Short name**      : BIC_IoT_LT_(T)
 *  <br>  **Author**          : Ramauer René
 *  <br>  **Version**         : V0.0
 *  <br>  **Created on**      : 05.12.2022
 *  <br>  **Last change**     : 18.02.2023
 *  ****************************************************************
 *  @author René Ramsauer <ic18b066@technikum-wien.at>
 *
 * @version 0.1
 */

/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef INC_BATT_MONITORING_H_
#define INC_BATT_MONITORING_H_

/* Define --------------------------------------------------------------------*/
/*Start Hardware Settings*/
#define RESOLUTION_ADC 1023
#define VOLTAGE_IO 3.3
#define VOLTAGE_DEVIDER 0.625
/*End Hardware Settings*/

/* Function prototypes ------------------------------------------------------*/
float batt_reatVoltage(int i_pin, float f_voltIO, int i_resolutionADConverter); /* Return Voltage of IO */
//Bsp.: batt_reatVoltage(34, VOLTAGE_IO, RESOLUTION_ADC);
int batt_estimationStateOfBatteryCharge_LIPO(float voltage, int countCell); /* Estimation the state of the battery charge level of LIPO */
//Bsp.: batt_estimationStateOfBatteryCharge_LIPO(batt_reatVoltage(34, VOLTAGE_IO, RESOLUTION_ADC),1);

#endif /* INC_BATT_MONITORING_H_ */