package com.rramsauer.iotlocatiotrackerapp.util.geo;

/**
 * The TrackerDirectionCalculator class is used to calculate the direction of a moving object
 * based on a series of distance and geolocation measurements.
 *
 * @author Ramsauer René
 * @version 0.1
 */
public class TrackerDirectionCalculator {
    private static final double MIN_MOVEMENT_IN_M = 0.1; // adjust as needed
    private final int measurementCount;
    private int measurementPointer;
    private double[] measurements;
    private boolean initialized;
    private double trackerLatitude;
    private double trackerLongitude;
    private double lastDirection;

    /**
     * Constructs a new TrackerDirectionCalculator with the specified number of measurements.
     *
     * @param measurementCount the number of measurements to use for direction calculation
     * @author Ramsauer René
     */
    public TrackerDirectionCalculator(int measurementCount) {
        this.measurementCount = measurementCount;
        this.measurements = new double[measurementCount];
        this.measurementPointer = 0;
        this.initialized = false;
    }

    /**
     * Adds a new distance and geolocation measurement to the calculator and returns the calculated direction.
     *
     * @param distance  the distance traveled since the last measurement
     * @param latitude  the current latitude of the object being tracked
     * @param longitude the current longitude of the object being tracked
     * @return the direction in degrees, based on the current and previous measurements
     * @author Ramsauer René
     */
    public double addGeo(double distance, double latitude, double longitude) {
        if (!initialized) {
            trackerLatitude = latitude;
            trackerLongitude = longitude;
            initialized = true;
            return 0.0;
        }

        double direction = Math.toDegrees(Math.atan2(
                Math.sin(Math.toRadians(longitude - trackerLongitude)) * Math.cos(Math.toRadians(latitude)),
                Math.cos(Math.toRadians(trackerLatitude)) * Math.sin(Math.toRadians(latitude)) - Math.sin(Math.toRadians(trackerLatitude)) * Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(longitude - trackerLongitude))
        ));

        if (measurementPointer > 0) {
            double lastMeasurement = measurements[measurementPointer - 1];
            double distanceDiff = Math.abs(distance - lastMeasurement);
            if (distanceDiff < MIN_MOVEMENT_IN_M) {
                return lastDirection;
            }
        }

        measurements[measurementPointer] = distance;
        lastDirection = direction;
        measurementPointer = (measurementPointer + 1) % measurementCount;
        return direction;
    }
}
