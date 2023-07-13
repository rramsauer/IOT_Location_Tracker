package com.rramsauer.iotlocatiotrackerapp.util.calc.distance;

/**
 * The CalculatorDistanceRSSI class provides static methods to calculate distance using RSSI values.
 *
 * @author Ramsauer René
 */
public class CalculatorDistanceRSSI {

    /**
     * Calculates distance using the measured power (measuredPower), received signal strength indicator (RSSI),
     * and path-loss exponent (n).
     *
     * @param measuredPower the measured power
     * @param rssi          the received signal strength indicator (RSSI)
     * @param n             the path-loss exponent
     * @return the calculated distance
     * @author Ramsauer René
     */
    public static double calculateDistance(double measuredPower, double rssi, double n) {
        double distance = Math.pow(10, ((measuredPower - rssi) / (10 * n)));
        return distance;
    }

}
