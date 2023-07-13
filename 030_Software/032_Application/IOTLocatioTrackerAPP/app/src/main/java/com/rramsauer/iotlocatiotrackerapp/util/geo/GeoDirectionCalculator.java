package com.rramsauer.iotlocatiotrackerapp.util.geo;


/**
 * A utility class for calculating the direction of movement based on a series of GPS coordinates.
 *
 * @author Ramsauer René
 */
public class GeoDirectionCalculator {
    /**
     * The minimum distance (in meters) between two consecutive coordinates for movement to be considered.
     */
    public static final double MIN_MOVEMENT_WAY_M = 0.005;
    /**
     * The number of position measurements to store for calculating direction.
     */
    public static final int COUNT_OF_POS = 5;
    private DistanceAreaMeasurement[] distanceAreaMeasurements = new DistanceAreaMeasurement[COUNT_OF_POS];
    private int arrayPointer;
    private int countMeasuring;
    private double actualDirection;

    /**
     * Creates a new instance of the GeoDirectionCalculator class.
     *
     * @author Ramsauer René
     */
    public GeoDirectionCalculator() {
        this.countMeasuring = 0;
        this.arrayPointer = 0;
    }

    /**
     * Adds a new position measurement to the distanceAreaMeasurements array and calculates the new direction of movement.
     *
     * @param distanceToGoal The distance to the goal location, in meters.
     * @param posLatitude    The latitude of the new position measurement, in degrees.
     * @param posLongitude   The longitude of the new position measurement, in degrees.
     * @return The new direction of movement, in degrees.
     * @author Ramsauer René
     */
    public double addElement(double distanceToGoal, double posLatitude, double posLongitude) {
        if (countMeasuring == 0) {
            distanceAreaMeasurements[0] = new DistanceAreaMeasurement(distanceToGoal, posLatitude, posLongitude);
            countMeasuring = 1;
            arrayPointer = 1;
            actualDirection = 0;
            return actualDirection;
        } else {
            double tempDistanceDiv = GeoHelper.distanceOfTwoPosition(
                    distanceAreaMeasurements[(arrayPointer - 1) % COUNT_OF_POS].getPosLatitude(),
                    distanceAreaMeasurements[(arrayPointer - 1) % COUNT_OF_POS].getPosLongitude(),
                    posLatitude,
                    posLongitude
            );
            if (tempDistanceDiv >= MIN_MOVEMENT_WAY_M) {
                distanceAreaMeasurements[arrayPointer] = new DistanceAreaMeasurement(
                        distanceToGoal,
                        posLatitude,
                        posLongitude
                );
                double newDegree = calculateDegree();
                if (countMeasuring < COUNT_OF_POS) {
                    countMeasuring++;
                }
                arrayPointer = (arrayPointer + 1) % COUNT_OF_POS;
                return newDegree;
            } else {
                return actualDirection;
            }

        }
    }

    /**
     * Calculates the direction of movement based on the stored position measurements.
     *
     * @return The new direction of movement, in degrees.
     */
    private double calculateDegree() {
        double tempDegree = 0;
        for (int i = 0; i <= (countMeasuring - 2); i++) {
            int elementPointer = (i + arrayPointer + 1) % countMeasuring;
            double temDistance = GeoHelper.distanceOfTwoPosition(
                    distanceAreaMeasurements[arrayPointer].getPosLatitude(),
                    distanceAreaMeasurements[arrayPointer].getPosLongitude(),
                    distanceAreaMeasurements[elementPointer].getPosLatitude(),
                    distanceAreaMeasurements[elementPointer].getPosLongitude()
            );
            if (temDistance >= MIN_MOVEMENT_WAY_M) {
                if (tempDegree == 0) {
                    tempDegree = GeoHelper.CalculateDirectionToTheTracker(
                            distanceAreaMeasurements[arrayPointer].getDistanceToGoal(),
                            distanceAreaMeasurements[arrayPointer].getDistanceToGoal(),
                            temDistance,
                            GeoHelper.getDirectionToNorth(
                                    distanceAreaMeasurements[arrayPointer].getPosLatitude(),
                                    distanceAreaMeasurements[arrayPointer].getPosLongitude(),
                                    distanceAreaMeasurements[elementPointer].getPosLatitude(),
                                    distanceAreaMeasurements[elementPointer].getPosLongitude()
                            )
                    );
                } else {
                    tempDegree = GeoHelper.CalculateDirectionToTheTracker(
                            distanceAreaMeasurements[arrayPointer].getDistanceToGoal(),
                            distanceAreaMeasurements[arrayPointer].getDistanceToGoal(),
                            temDistance,
                            GeoHelper.getDirectionToNorth(
                                    distanceAreaMeasurements[arrayPointer].getPosLatitude(),
                                    distanceAreaMeasurements[arrayPointer].getPosLongitude(),
                                    distanceAreaMeasurements[elementPointer].getPosLatitude(),
                                    distanceAreaMeasurements[elementPointer].getPosLongitude()
                            )
                    ) + tempDegree / 2;
                }

            }
        }
        return tempDegree;
    }

    /**
     * The DistanceAreaMeasurement class represents a measurement of distance and location.
     * It contains the distance to a goal, as well as the latitude and longitude of a position.
     */
    public class DistanceAreaMeasurement {
        double distanceToGoal;
        double posLatitude;
        double posLongitude;

        /**
         * Constructs a new DistanceAreaMeasurement object with the given distance to goal, latitude, and longitude.
         *
         * @param distanceToGoal the distance to the goal
         * @param posLatitude    the latitude of the position
         * @param posLongitude   the longitude of the position
         */
        public DistanceAreaMeasurement(double distanceToGoal, double posLatitude, double posLongitude) {
            this.distanceToGoal = distanceToGoal;
            this.posLatitude = posLatitude;
            this.posLongitude = posLongitude;
        }

        public double getDistanceToGoal() {
            return distanceToGoal;
        }

        public void setDistanceToGoal(double distanceToGoal) {
            this.distanceToGoal = distanceToGoal;
        }

        public double getPosLongitude() {
            return posLongitude;
        }

        public void setPosLongitude(double posLongitude) {
            this.posLongitude = posLongitude;
        }

        public double getPosLatitude() {
            return posLatitude;
        }

        public void setPosLatitude(double posLatitude) {
            this.posLatitude = posLatitude;
        }
    }
}
