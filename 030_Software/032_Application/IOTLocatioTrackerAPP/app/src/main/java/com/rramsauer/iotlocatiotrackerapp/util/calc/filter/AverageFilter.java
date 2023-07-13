package com.rramsauer.iotlocatiotrackerapp.util.calc.filter;

/**
 * AverageFilter is a class that implements a simple moving average filter to smooth out a stream of values.
 * The filter uses a sliding window of a specified size to calculate the average of the last n values in the stream.
 *
 * @author Ramsauer René
 * @version 0.1
 */
public class AverageFilter {
    private double[] values;
    private int arrayPosition;
    private int countOfValues;

    /**
     * Constructor for AverageFilter class. Initializes the sliding window with a specified size
     * and fills it with a given start value.
     *
     * @param countOfValues the size of the sliding window
     * @param startValue    the initial value to fill the sliding window with
     * @author Ramsauer René
     */
    public AverageFilter(int countOfValues, double startValue) {
        this.countOfValues = countOfValues;
        values = new double[countOfValues];
        for (double value : values) {
            value = startValue;
        }
        arrayPosition = 0;
    }

    /**
     * Method to add a new value to the sliding window and calculate the average of the values in the window.
     *
     * @param Rssi the new value to add to the sliding window
     * @return the average of the values in the sliding window
     * @author Ramsauer René
     */
    public double addRSSI(double Rssi) {
        int tempRssiSum = 0;
        values[arrayPosition] = Rssi;
        arrayPosition = (arrayPosition + 1) % countOfValues;
        for (double value : values) {
            tempRssiSum += value;
        }
        return tempRssiSum / countOfValues;
    }
}
