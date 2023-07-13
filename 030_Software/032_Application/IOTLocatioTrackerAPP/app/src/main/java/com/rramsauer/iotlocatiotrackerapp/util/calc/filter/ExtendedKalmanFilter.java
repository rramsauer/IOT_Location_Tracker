package com.rramsauer.iotlocatiotrackerapp.util.calc.filter;

import org.ejml.simple.SimpleMatrix;

/**
 * Implementation of an extended Kalman filter for estimating the position of a mobile device
 * based on received signal strength indication (RSSI) measurements from a set of fixed beacons.
 * The filter maintains a state vector and a state covariance matrix, and uses these to predict
 * the position of the device at each time step based on a linear motion model and to update the
 * position estimate based on the RSSI measurements.
 *
 * @author Ramsauer René
 */
public class ExtendedKalmanFilter {
    private SimpleMatrix x; // state vector
    private SimpleMatrix P; // state covariance matrix
    private SimpleMatrix Q; // process noise covariance matrix
    private SimpleMatrix R; // measurement noise covariance matrix
    private SimpleMatrix F; // state transition matrix
    private SimpleMatrix H; // measurement matrix
    private SimpleMatrix I; // identity matrix

    /**
     * Constructs a new ExtendedKalmanFilter object with default initial values for the matrices.
     * The state vector is initialized to (0, 0), the state covariance matrix is initialized to
     * the identity matrix, and the process noise covariance matrix, measurement noise covariance
     * matrix, state transition matrix, and measurement matrix are initialized to specific values
     * that are appropriate for the given problem domain.
     *
     * @author Ramsauer René
     */
    public ExtendedKalmanFilter() {
        x = new SimpleMatrix(2, 1);
        P = new SimpleMatrix(2, 2);
        Q = new SimpleMatrix(2, 2);
        R = new SimpleMatrix(1, 1);
        F = new SimpleMatrix(2, 2);
        H = new SimpleMatrix(1, 2);
        I = SimpleMatrix.identity(2);
        // initialize the matrices
        x.set(0, 0, 0.0);
        x.set(1, 0, 0.0);
        P.set(0, 0, 1.0);
        P.set(1, 1, 1.0);
        Q.set(0, 0, 0.01);
        Q.set(1, 1, 0.01);
        R.set(0, 0, 1.0);
        F.set(0, 0, 1.0);
        F.set(0, 1, 1.0);
        F.set(1, 0, 0.0);
        F.set(1, 1, 1.0);
        H.set(0, 0, 1.0);
        H.set(0, 1, 0.0);
    }

    /**
     * Predicts the position of the mobile device based on the current state estimate and a linear
     * motion model, and then updates the state estimate based on the received RSSI measurement.
     *
     * @param rssi the received signal strength indication measurement
     * @return the estimated position of the mobile device
     * @author Ramsauer René
     */
    public double addRSSI(double rssi) {
        // predict
        x = F.mult(x);
        P = F.mult(P).mult(F.transpose()).plus(Q);
        // update
        SimpleMatrix z = new SimpleMatrix(1, 1);
        z.set(0, 0, rssi);
        SimpleMatrix y = z.minus(H.mult(x));
        SimpleMatrix S = H.mult(P).mult(H.transpose()).plus(R);
        SimpleMatrix K = P.mult(H.transpose()).mult(S.invert());
        x = x.plus(K.mult(y));
        P = (I.minus(K.mult(H))).mult(P);

        return x.get(0, 0);
    }
}

