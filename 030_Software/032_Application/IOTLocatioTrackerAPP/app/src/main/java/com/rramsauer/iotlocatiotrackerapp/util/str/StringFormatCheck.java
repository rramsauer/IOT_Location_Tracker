package com.rramsauer.iotlocatiotrackerapp.util.str;

/**
 * Class String check the Format of specify strings.
 * This class is a helper class and provides some useful function for checking the format of strings.
 *
 * @author Ramsauer René
 * @version V0.0
 */
public class StringFormatCheck {

    /**
     * This function check the format of the bluetooth address string
     *
     * @param address passed the address string to check
     * @return Returns the result of the test.
     * @author Ramsauer René
     */
    public static boolean isBluetoothAddress(String address) {
        return address.matches("([a-zA-Z0-9]{2}:){5}[a-zA-Z0-9]{2}");
    }

    /**
     * This function check the format of the uwb uid string
     *
     * @param uid passed the address string to check
     * @return Returns the result of the test.
     * @author Ramsauer René
     */
    public static boolean isUltraWideBandUid(String uid) {
        return uid.matches("([a-zA-Z0-9]{2}:){7}[a-zA-Z0-9]{2}");
    }

    /**
     * This function check the format of the uwb short address string
     *
     * @param address passed the address string to check
     * @return Returns the result of the test.
     * @author Ramsauer René
     */
    public static boolean isUltraWideBandShortAddress(String address) {
        return address.matches("[a-zA-Z0-9]{4}");
    }
}
