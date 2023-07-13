package com.rramsauer.iotlocatiotrackerapp.util.str;

/* Application imports */

import com.rramsauer.iotlocatiotrackerapp.ui.view.BLEFragment;
/* JAVA imports */
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class String Converter.
 * This class is a helper class and provides some useful function for converting objects into specify strings.
 *
 * @author Ramsauer René
 * @version V1.2
 */
public class StringConverter {

    /**
     * This function returns an formatted standard string of coordinate (dg | gms | gmm)
     *
     * @param latitude  Passed the value of latitude.
     * @param longitude Passed the value of longitude.
     * @param format    Passed the format typ of your coordinate. Option: dg | gms | gmm
     * @return Returns an formatted string of the passed param.
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: coordinateToString(4684722.0, 1387744.0, "gms")
     */
    public static String coordinateToString(double latitude, double longitude, String format) {
        String coordinateString;
        String strDegreeLatitude;
        String strDegreeLongitude;
        switch (format) {
            case "DG":
            case "dg":
                coordinateString = Double.toString(latitude) + " " + Double.toString(longitude);
                break;
            case "GMS":
            case "gms":
                if (latitude < 0) {
                    strDegreeLatitude = Integer.toString((int) (0 - latitude)) + "°" + Integer.toString((int) ((0 - latitude) % 1 * 60)) + "'" + Double.toString(Math.round((((0 - latitude) % 1 * 60) % 1 * 60) * 10.0) / 10.0) + "\"S";
                } else {
                    strDegreeLatitude = Integer.toString((int) latitude) + "°" + Integer.toString((int) (latitude % 1 * 60)) + "'" + Double.toString(Math.round(((latitude % 1 * 60) % 1 * 60) * 10.0) / 10.0) + "\"N";
                }

                if (longitude < 0) {
                    strDegreeLongitude = Integer.toString((int) (0 - longitude)) + "°" + Integer.toString((int) ((0 - longitude) % 1 * 60)) + "'" + Double.toString(Math.round((((0 - longitude) % 1 * 60) % 1 * 60) * 10.0) / 10.0) + "\"W";
                } else {
                    strDegreeLongitude = Integer.toString((int) longitude) + "°" + Integer.toString((int) (longitude % 1 * 60)) + "'" + Double.toString(Math.round(((longitude % 1 * 60) % 1 * 60) * 10.0) / 10.0) + "\"E";
                }
                coordinateString = strDegreeLatitude + " " + strDegreeLongitude;
                break;
            case "GMM":
            case "gmm":
                strDegreeLatitude = Integer.toString((int) latitude) + " " + Double.toString(Math.round((latitude % 1 * 60) * 10000.0) / 10000.0);
                strDegreeLongitude = Integer.toString((int) longitude) + " " + Double.toString(Math.round((longitude % 1 * 60) * 10000.0) / 10000.0);
                coordinateString = strDegreeLatitude + " " + strDegreeLongitude;
                break;
            default:
                coordinateString = "";
                System.err.println("ImplErr: coordinateToString() False Format was passed. These formats can be passed in the form of a string : DG, GMS or GMM");
                break;
        }
        return coordinateString;
    }

    /**
     * This function returns an formatted standard string of coordinate (dg | gms | gmm)
     *
     * @param latitude  Passed the value of latitude.
     * @param longitude Passed the value of longitude.
     * @param format    Passed the format typ of your coordinate. Option: dg | gms | gmm
     * @return Returns an formatted string of the passed param.
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: coordinateToString(4684722.0, 1387744.0, "gms")
     */
    public static String coordinateToString(float latitude, float longitude, String format) {
        String coordinateString;
        String strDegreeLatitude;
        String strDegreeLongitude;
        switch (format) {
            case "DG":
            case "dg":
                coordinateString = Float.toString(latitude) + " " + Float.toString(longitude);
                break;
            case "GMS":
            case "gms":
                if (latitude < 0) {
                    strDegreeLatitude = Integer.toString((int) (0 - latitude)) + "°" + Integer.toString((int) ((0 - latitude) % 1 * 60)) + "'" + Float.toString(Math.round((((0 - latitude) % 1 * 60) % 1 * 60) * 10.0F) / 10.0F) + "\"S";
                } else {
                    strDegreeLatitude = Integer.toString((int) latitude) + "°" + Integer.toString((int) (latitude % 1 * 60)) + "'" + Float.toString(Math.round(((latitude % 1 * 60) % 1 * 60) * 10.0F) / 10.0F) + "\"N";
                }

                if (longitude < 0) {
                    strDegreeLongitude = Integer.toString((int) (0 - longitude)) + "°" + Integer.toString((int) ((0 - longitude) % 1 * 60)) + "'" + Float.toString(Math.round((((0 - longitude) % 1 * 60) % 1 * 60) * 10.0F) / 10.0F) + "\"W";
                } else {
                    strDegreeLongitude = Integer.toString((int) longitude) + "°" + Integer.toString((int) (longitude % 1 * 60)) + "'" + Float.toString(Math.round(((longitude % 1 * 60) % 1 * 60) * 10.0F) / 10.0F) + "\"E";
                }
                coordinateString = strDegreeLatitude + " " + strDegreeLongitude;
                break;
            case "GMM":
            case "gmm":
                strDegreeLatitude = Integer.toString((int) latitude) + " " + Float.toString(Math.round((latitude % 1 * 60) * 10000.0F) / 10000.0F);
                strDegreeLongitude = Integer.toString((int) longitude) + " " + Float.toString(Math.round((longitude % 1 * 60) * 10000.0F) / 10000.0F);
                coordinateString = strDegreeLatitude + "   " + strDegreeLongitude;
                break;
            default:
                coordinateString = "";
                System.err.println("ImplErr: coordinateToString() False Format was passed. These formats can be passed in the form of a string : DG, GMS or GMM");
                break;
        }
        return coordinateString;
    }

    /**
     * This function returns a defined formatted strong of a Data object.
     *
     * @param date      Passed device name.
     * @param strFormat Passed device ID.
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateContainerBLEVDate("Device Name", "Device ID")
     * @see BLEFragment
     * @since <Android API 32 Platform></Android>
     */
    public static String dateToString(Date date, String strFormat) {
        SimpleDateFormat format = new SimpleDateFormat(strFormat);
        return format.format(date);
    }

    /**
     * This function returned a defined formatted string of an double
     *
     * @param value   Transfer the value to be edited.
     * @param pattern Passed a string for define the format e.g.: "0.00"
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateContainerBLEVDate(56.4498, "0.00")
     * @see BLEFragment
     * @since <Android API 32 Platform></Android>
     */
    public static String decimalToString(double value, String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(value);
    }
}
