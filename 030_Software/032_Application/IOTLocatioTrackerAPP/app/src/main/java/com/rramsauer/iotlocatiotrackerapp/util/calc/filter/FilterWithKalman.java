package com.rramsauer.iotlocatiotrackerapp.util.calc.filter;

import org.ejml.simple.SimpleMatrix;

/**
 * A class that implements a filter for RSSI values using both a moving average filter and a Kalman
 * filter. The implementation of this class is an amalgamation of several implementation solutions.
 * For this reason it is possible that parts of this class are similar to other implementations.
 * <p>
 * Exdended Describtion:
 * This Java class implements a filter with a moving average and a Kalman filter to filter RSSI
 * (Received Signal Strength Indicator) values. The filter applies the moving average to smooth
 * the RSSI values and then applies the Kalman filter to estimate the true RSSI value based on
 * the noisy measurements.
 * <p>
 * The class has several constants, such as DEFAULT_MOVING_AVERAGE_COUNT, MIN_MOVING_AVERAGE_COUNT,
 * and MAX_MOVING_AVERAGE_COUNT, which define the parameters of the moving average filter.
 * There are also constants for the Kalman filter:
 * KALMAN_PROCESS_NOISE and KALMAN_MEASUREMENT_NOISE.
 * <p>
 * The class has a constructor that initializes the variables, including the RSSI buffer,
 * the moving average count, the Kalman estimate, the Kalman error, and the Kalman gain.
 * The addRssi method is used to add new RSSI values to the buffer and apply the moving average
 * and Kalman filters to the buffer to estimate the true RSSI value. The setMovingAverageCount
 * method is used to set the moving average count, and the getMovingAverageCount method is used
 * to get the current moving average count.
 * <p>
 * The class has two private methods, getMovingAverage and applyKalmanFilter,
 * which calculate the moving average of the RSSI values in the buffer and apply the Kalman filter
 * to the filtered value, respectively. The applyKalmanFilter method uses the SimpleMatrix class
 * from the EJML (Efficient Java Matrix Library) library to perform the Kalman filter calculations.
 * <p>
 * In summary, this Java class implements a filter with a moving average and a Kalman filter to
 * filter RSSI values. It can be used to improve the accuracy of RSSI measurements in wireless
 * communication systems.
 *
 * @author Ramsauer René
 */
public class FilterWithKalman {

    // Constants for the moving average filter
    private static final int DEFAULT_MOVING_AVERAGE_COUNT = 5;
    private static final int MIN_MOVING_AVERAGE_COUNT = 5;
    private static final int MAX_MOVING_AVERAGE_COUNT = 20;
    // Constants for the Kalman filter
    private static final double KALMAN_PROCESS_NOISE = 0.125d;
    private static final double KALMAN_MEASUREMENT_NOISE = 0.5d;

    private double[] rssiBuffer;
    private int bufferIndex;
    private int movingAverageCount;
    private double kalmanEstimate;
    private double kalmanError;
    private double kalmanGain;

    /**
     * Constructor that initializes the filter with default values.
     *
     * @author Ramsauer René
     */
    public FilterWithKalman() {
        rssiBuffer = new double[MAX_MOVING_AVERAGE_COUNT];
        bufferIndex = 0;
        movingAverageCount = DEFAULT_MOVING_AVERAGE_COUNT;
        kalmanEstimate = 0;
        kalmanError = 1;
        kalmanGain = 0;
    }

    /**
     * Adds an RSSI value to the filter and returns the filtered value.
     *
     * @param rssi the RSSI value to add
     * @return the filtered RSSI value
     * @author Ramsauer René
     */
    public double addRssi(double rssi) {
        // Add the RSSI value to the buffer
        rssiBuffer[bufferIndex] = rssi;
        bufferIndex = (bufferIndex + 1) % movingAverageCount;
        // Calculate the moving average of the buffer
        double sum = 0;
        for (double rssiValue : rssiBuffer) {
            sum += rssiValue;
        }
        double movingAverage = sum / movingAverageCount;
        // Apply the Kalman filter to the moving average value
        return applyKalmanFilter(movingAverage);
    }

    /**
     * Returns the number of values used in the moving average filter.
     *
     * @return the number of values used in the moving average filter
     * @author Ramsauer René
     */
    public double getMovingAverageCount() {
        return movingAverageCount;
    }

    /**
     * Sets the number of values used in the moving average filter.
     *
     * @param count the number of values to use
     * @author Ramsauer René
     */
    public void setMovingAverageCount(int count) {
        if (count < MIN_MOVING_AVERAGE_COUNT) {
            movingAverageCount = MIN_MOVING_AVERAGE_COUNT;
        } else if (count > MAX_MOVING_AVERAGE_COUNT) {
            movingAverageCount = MAX_MOVING_AVERAGE_COUNT;
        } else {
            movingAverageCount = count;
        }
        bufferIndex = 0;
        rssiBuffer = new double[movingAverageCount];
    }

    /**
     * Calculates the moving average of the RSSI values in the buffer.
     *
     * @return the moving average of the RSSI values
     * @author Ramsauer René
     */
    private double getMovingAverage() {
        double sum = 0;
        double count = Math.min(bufferIndex, movingAverageCount);
        for (int i = 0; i < count; i++) {
            sum += rssiBuffer[i];
        }
        return sum / count;
    }

    /**
     * Applies a Kalman filter to the input value.
     *
     * @param value the input value to filter
     * @return the filtered value
     * @author Ramsauer René
     */
    private double applyKalmanFilter(double value) {
        // Create matrices for state, covariance, measurement, and noise
        SimpleMatrix x = new SimpleMatrix(1, 1);
        x.set(0, 0, value);
        SimpleMatrix p = new SimpleMatrix(1, 1);
        p.set(0, 0, kalmanError);
        SimpleMatrix f = new SimpleMatrix(new double[][]{{1}});
        SimpleMatrix q = new SimpleMatrix(new double[][]{{KALMAN_PROCESS_NOISE}});
        SimpleMatrix h = new SimpleMatrix(new double[][]{{1}});
        SimpleMatrix r = new SimpleMatrix(new double[][]{{KALMAN_MEASUREMENT_NOISE}});
        // measurement noise
        SimpleMatrix y = new SimpleMatrix(1, 1);
        SimpleMatrix k = new SimpleMatrix(1, 1);
        // Loop over past measurements to update filter
        for (int i = 0; i < movingAverageCount; i++) {
            // Predict
            x = f.mult(x);
            p = f.mult(p).mult(f.transpose()).plus(q);
            // Update
            y.set(0, 0, rssiBuffer[i] - x.get(0, 0));
            k = p.mult(h.transpose()).mult((h.mult(p).mult(h.transpose()).plus(r)).invert());
            x = x.plus(k.mult(y));
            p = p.minus(k.mult(h).mult(p));
        }
        // Set the filter's estimate and error covariance, and return the estimate
        kalmanEstimate = x.get(0, 0);
        kalmanError = p.get(0, 0);
        return kalmanEstimate;
    }

}
