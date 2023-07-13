package com.rramsauer.iotlocatiotrackerapp.util.geo;

/**
 * This is a utility class that provides static methods for locating systems.
 * Information: Parts of the class were created with the help of "chat.openai.com". This was stated in the respective function headers.
 *
 * @author Ramsauer René
 * @version V0.1
 */
public class GeoHelper {
    public static final double RADIUS_OF_EARTH = 6371; // Earth's radius in kilometers

    /**
     * This method uses the Haversine formula to calculate the distance between two points on the Earth.
     * <p>
     * The input parameters are the longitude and latitude of the two coordinates you want to compare.
     * The method returns the distance between the coordinates in kilometers.
     * <p>
     * Information on the implementation of the calculation:
     * See Haversine formula. https://en.wikipedia.org/wiki/Haversine_formula
     *
     * @param latitude1  Pass the latitude of pos 1
     * @param longitude1 Pass the longitude of pos 1
     * @param latitude2  Pass the latitude of pos 2
     * @param longitude2 Pass the longitude of pos 1
     * @return distance in km.
     * @author Ramsauer René
     */
    public static double distanceOfTwoPositionWithEarthRadius(double latitude1, double longitude1, double latitude2, double longitude2) {
        double latitude1Radians = Math.toRadians(latitude1);
        double latitude2Radians = Math.toRadians(latitude2);
        double deltaLatitudeRadians = Math.toRadians(latitude2 - latitude1);
        double deltaLongitudeRadians = Math.toRadians(longitude2 - longitude1);

        double a = Math.sin(deltaLatitudeRadians / 2) * Math.sin(deltaLatitudeRadians / 2) +
                Math.cos(latitude1Radians) * Math.cos(latitude2Radians) *
                        Math.sin(deltaLongitudeRadians / 2) * Math.sin(deltaLongitudeRadians / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIUS_OF_EARTH * c;
    }

    /**
     * This method calculate the straight line between two coordinate.
     * <p>
     * The input parameters are the longitude and latitude of the two coordinates you want to compare.
     * The method returns the distance between the coordinates in kilometers.
     * It uses the Pythagorean theorem to calculate the straight-line distance between the two points, in meters.
     * <p>
     * Information on the implementation of the calculation:
     * We first calculate the difference in longitude between the two points and multiply it by the cosine of the average latitude to convert it to the X-axis distance in radians.
     * We then calculate the difference in latitude between the two points and convert it to the Y-axis distance in radians.
     * Finally, we use the Pythagorean theorem to calculate the straight-line distance between the two points on a flat surface, which is returned in radians.
     *
     * @param latitude1  Pass the latitude of pos 1
     * @param longitude1 Pass the longitude of pos 1
     * @param latitude2  Pass the latitude of pos 2
     * @param longitude2 Pass the longitude of pos 1
     * @return distance in m.
     * @author Ramsauer René
     */
    public static double distanceOfTwoPosition(double latitude1, double longitude1, double latitude2, double longitude2) {
        double x = Math.toRadians(longitude2 - longitude1) * Math.cos(Math.toRadians((latitude1 + latitude2) / 2));
        double y = Math.toRadians(latitude2 - latitude1);
        return Math.sqrt(x * x + y * y);
    }

    /**
     * This method calculates the direction between two points on the earth
     * with respect to north and returns the deviation of the direction to north.
     * <p>
     * The input parameters are the longitude and latitude of the two coordinates you want to compare.
     *
     * @param latitude1  Pass the latitude of pos 1
     * @param longitude1 Pass the longitude of pos 1
     * @param latitude2  Pass the latitude of pos 2
     * @param longitude2 Pass the longitude of pos 1
     * @return distance in m.
     * @author Ramsauer René
     */
    public static double getDirectionToNorth(double latitude1, double longitude1, double latitude2, double longitude2) {
        double longitudeDiff = Math.toRadians(longitude2 - longitude1);
        double y = Math.sin(longitudeDiff) * Math.cos(Math.toRadians(latitude2));
        double x = Math.cos(Math.toRadians(latitude1)) * Math.sin(Math.toRadians(latitude2)) -
                Math.sin(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) * Math.cos(longitudeDiff);
        double direction = Math.toDegrees(Math.atan2(y, x));
        return (direction + 360) % 360;
    }

    /**
     * This method uses the cosine theorem to calculate the direction to the tracker
     * with respect to north and returns the deviation of the direction from north.
     *
     * @param distanceBefore Pass the distance from pos1 to tracker
     * @param distanceActual Pass the distance from pos2 to tracker
     * @param distanceGeo    Pass the distance of the traveled path
     * @param degreeNGeo     Pass the degree to north of the traveled path
     * @return the tracker direction with respect to north
     * @author Ramsauer René
     */
    public static double CalculateDirectionToTheTracker(double distanceBefore, double distanceActual, double distanceGeo, double degreeNGeo) {
        double angleToGeoDistance = Math.acos((distanceGeo * distanceGeo + distanceActual * distanceActual - distanceBefore * distanceBefore) / (2 * distanceGeo * distanceActual));
        return degreeNGeo + angleToGeoDistance;
    }


}
