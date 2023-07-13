package com.rramsauer.iotlocatiotrackerapp.util.calc.filter;

import java.io.Serializable;
import java.text.ParseException;

/**
 * Class Kalman Filter
 *
 * @author <a href="https://stackoverflow.com/users/9993413/z3r0">StackOverflow z3r0</a>
 * @version V0.0
 * @implNote Example usage::
 * <pre>
 * {@code
 * private KalmanFilter mKalmanFilter; // Property of your class to store kalman filter values
 * mKalmanFilter = new KalmanFilter(KALMAN_R, KALMAN_Q); // init Kalman Filter
 * // Method Apply Filter
 * private void applyKalmanFilterToRssi(){
 * mFilteredRSSI = mKalmanFilter.applyFilter(mRSSI);
 * }
 * }
 * </pre>
 * Constants Values:
 * <pre>
 * {@code
 * // Kalman R & Q
 * private static final double KALMAN_R = 0.125d;
 * private static final double KALMAN_Q = 0.5d;
 * }
 * </pre>
 * <ul>
 * <li>KALMAN_Q is the Measurement Noise
 * <li>KALMAN_Q is the Measurement Noise
 * </ul>
 * You Should Change These 2 Values looking to your measurements and you case of use.
 * Changing these 2 values you will change how fast the filtered measurement value will change from a value to another.
 * So if you have values with a lot of noise and you want to slow down how fast the measurement value changes
 * (to smooth the Gaussian) you should try to increment KALMAN_R & KALMAN_Q values. The values here for KALMAN_R & KALMAN_Q,
 * if I remember right, were the one I was using when I was programming for BLE Devices so these 2 values
 * for KALMAN_R & KALMAN_Q are already "big" because the RSSI of BLE Devices changes a lot.
 * p>
 * @throws ParseException what kind of exception does this method throw
 * @see java.io.Serializable
 * @see java.text.ParseException
 * @see <a href="https://stackoverflow.com/questions/36399927/distance-calculation-from-rssi-ble-android">StackOverflow</a>
 * @see <a href="https://stackoverflow.com/questions/36399927/distance-calculation-from-rssi-ble-android">Author</a>
 */
public class KalmanFilter implements Serializable {

    private double R;   //  Process Noise
    private double Q;   //  Measurement Noise
    private double A;   //  State Vector
    private double B;   //  Control Vector
    private double C;   //  Measurement Vector

    private Double x;   //  Filtered Measurement Value (No Noise)
    private double cov; //  Covariance

    /**
     * Constructor for the KalmanFilter class.
     *
     * @param r The process noise.
     * @param q The measurement noise.
     * @param a The state vector.
     * @param b The control vector.
     * @param c The measurement vector.
     */
    public KalmanFilter(double r, double q, double a, double b, double c) {
        R = r;
        Q = q;
        A = a;
        B = b;
        C = c;
    }

    /**
     * Constructor for the KalmanFilter class that assumes default values for A, B, and C.
     *
     * @param r The process noise.
     * @param q The measurement noise.
     */
    public KalmanFilter(double r, double q) {
        R = r;
        Q = q;
        A = 1;
        B = 0;
        C = 1;
    }

    /**
     * Applies the Kalman filter to a measurement with default control value of 0.0.
     *
     * @param rssi The measurement value to be filtered.
     * @return The filtered value.
     */
    public double applyFilter(double rssi) {
        return applyFilter(rssi, 0.0d);
    }

    /**
     * Applies the Kalman filter to a measurement with a control value.
     *
     * @param measurement The measurement value to be filtered.
     * @param u           The controlled input value.
     * @return The filtered value.
     */
    public double applyFilter(double measurement, double u) {
        double predX;           //  Predicted Measurement Value
        double K;               //  Kalman Gain
        double predCov;         //  Predicted Covariance
        if (x == null) {
            x = (1 / C) * measurement;
            cov = (1 / C) * Q * (1 / C);
        } else {
            predX = predictValue(u);
            predCov = getUncertainty();
            K = predCov * C * (1 / ((C * predCov * C) + Q));
            x = predX + K * (measurement - (C * predX));
            cov = predCov - (K * C * predCov);
        }
        return x;
    }

    /**
     * Predicts the next value based on the current state and control input.
     *
     * @param control The control input value.
     * @return The predicted value.
     */
    private double predictValue(double control) {
        return (A * x) + (B * control);
    }

    /**
     * Calculates the uncertainty of the next predicted value.
     *
     * @return The uncertainty of the predicted value.
     */
    private double getUncertainty() {
        return ((A * cov) * A) + R;
    }

    /**
     * Returns a string representation of the KalmanFilter object.
     *
     * @return A string representation of the KalmanFilter object.
     */
    @Override
    public String toString() {
        return "KalmanFilter{" +
                "R=" + R +
                ", Q=" + Q +
                ", A=" + A +
                ", B=" + B +
                ", C=" + C +
                ", x=" + x +
                ", cov=" + cov +
                '}';
    }
}