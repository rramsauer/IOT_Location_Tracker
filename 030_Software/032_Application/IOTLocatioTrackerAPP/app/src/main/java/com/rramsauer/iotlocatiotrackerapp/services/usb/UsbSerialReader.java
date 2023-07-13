package com.rramsauer.iotlocatiotrackerapp.services.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.IntDef;

import com.rramsauer.iotlocatiotrackerapp.storage.state.ActualMeasuringData;
import com.rramsauer.iotlocatiotrackerapp.util.calc.distance.CalculatorDistanceRSSI;
import com.rramsauer.iotlocatiotrackerapp.util.calc.filter.KalmanFilter;
import com.rramsauer.iotlocatiotrackerapp.util.geo.TrackerDirectionCalculator;
import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

/**
 * The UsbSerialReader class implements the ArduinoListener interface to handle data read from an Arduino device
 * connected via USB. The class is responsible for initializing the communication with the device, parsing
 * the data received from the device, and updating the ActualMeasuringData singleton object with the parsed data.
 */
public class UsbSerialReader implements ArduinoListener {
    public static final int BAUD_RATE_300 = 300;
    public static final int BAUD_RATE_1200 = 1200;
    public static final int BAUD_RATE_2400 = 2400;
    public static final int BAUD_RATE_4800 = 4800;
    public static final int BAUD_RATE_9600 = 9600;
    public static final int BAUD_RATE_19200 = 19200;
    public static final int BAUD_RATE_38400 = 38400;
    public static final int BAUD_RATE_57600 = 57600;
    public static final int BAUD_RATE_74880 = 74880;
    public static final int BAUD_RATE_115200 = 115200;
    public static final int BAUD_RATE_230400 = 230400;
    public static final int BAUD_RATE_250000 = 250000;
    /* Settings for RSSI distance calculation */
    public static final double UWB_MEASURED_TX_POWER = -62;
    public static final double UWB_PROPAGATION_EXPONENT = 0.5;
    private static final String TAG = "UsbSerialReader";
    private final Context context;
    KalmanFilter kalmanFilter = new KalmanFilter(0.125d, 0.50d);
    TrackerDirectionCalculator trackerDirectionCalculator = new TrackerDirectionCalculator(3);
    private Arduino arduino;
    private String uwbShortAddress;

    /**
     * Constructs a new UsbSerialReader object with the specified context and baud rate.
     *
     * @param context  The context to use.
     * @param baudRate The baud rate to use for the USB serial communication.
     */
    @SuppressWarnings("unused")
    public UsbSerialReader(android.content.Context context, @UsbSerialReader.USBSerialBaudRate final int baudRate) {
        this.context = context;
        init(context, baudRate);
        setUwbIsConnected(false);
    }

    /**
     * Constructs a new UsbSerialReader object with the specified context, baud rate and vendor ID.
     * This methode is used if you have not an arduino device.
     *
     * @param context  The context to use.
     * @param baudRate The baud rate to use for the USB serial communication.
     * @param vendorId The vendor ID of the USB device if it is not an arduino device.
     */
    public UsbSerialReader(android.content.Context context, @UsbSerialReader.USBSerialBaudRate final int baudRate, int vendorId) {
        this.context = context;
        init(context, baudRate, vendorId);
        setUwbIsConnected(false);
    }

    /**
     * Parses a message string and returns a UsbSerialMessageData object.
     *
     * @param msg the message string to parse
     * @return a UsbSerialMessageData object if the message string contains the correct format, otherwise null
     */
    public static UsbSerialMessageData getValueOfMeasuringString(String msg) {
        if (msg.split(";;").length == 4) {
            String[] elementsOfSerialData = msg.split(";;");
            try {
                return new UsbSerialMessageData(
                        Double.parseDouble(elementsOfSerialData[2]),
                        Double.parseDouble(elementsOfSerialData[3]),
                        elementsOfSerialData[1]
                );
            } catch (NullPointerException e) {
                Logger.e(TAG, "At parseDouble ->NullPointerException", Thread.currentThread().getStackTrace()[2]);
                return null;
            } catch (NumberFormatException e) {
                Logger.e(TAG, "At parseDouble ->NumberFormatException", Thread.currentThread().getStackTrace()[2]);
                return null;
            }
        }
        return null;
    }

    /**
     * Called when a USB device is attached to the system.
     *
     * @param device The attached USB device.
     */
    @Override
    public void onArduinoAttached(UsbDevice device) {
        //ToDo: Replace with customer message
        Toast.makeText(context, "USB device " + device.getDeviceName() + " was detected.", Toast.LENGTH_SHORT).show();
        arduino.open(device);
    }

    /**
     * Called when a USB device is detached from the system.
     */
    @Override
    public void onArduinoDetached() {
        //ToDo: Replace with customer message
        Toast.makeText(context, "USB device was disconnected.", Toast.LENGTH_SHORT).show();
        setUwbIsConnected(false);
    }

    /**
     * Callback method that gets called when an Arduino message is received over USB serial connection
     *
     * @param bytes the message received from the Arduino device
     */
    @Override
    public void onArduinoMessage(byte[] bytes) {
        UsbSerialMessageData usbSerialMessageData = getValueOfMeasuringString(new String(bytes));
        if (usbSerialMessageData != null) {
            if (usbSerialMessageData.getUwbShortAddress().equals(uwbShortAddress)) {
                setUwbIsConnected(true);
                ActualMeasuringData.getInstance().setUwbRssi(usbSerialMessageData.getUwbRssi());
                double tempFilteredRssi = kalmanFilter.applyFilter(usbSerialMessageData.getUwbRssi());
                ActualMeasuringData.getInstance().setUwbRssiFiltered(tempFilteredRssi);
                ActualMeasuringData.getInstance().
                        setUwbDistanceRssi(CalculatorDistanceRSSI.calculateDistance(
                                UWB_MEASURED_TX_POWER,
                                tempFilteredRssi,
                                UWB_PROPAGATION_EXPONENT
                        ));
                ActualMeasuringData.getInstance().setUwbDistanceToa(usbSerialMessageData.getUwbDistanceToa());
                ActualMeasuringData.getInstance().setUwbDirection(trackerDirectionCalculator.addGeo(usbSerialMessageData.getUwbDistanceToa(),
                        ActualMeasuringData.getInstance().getDeviceCoordinateLatitudeDG(),
                        ActualMeasuringData.getInstance().getDeviceCoordinateLongitudeDG()
                ));
            } else {
                setUwbIsConnected(false);
            }
        }
    }

    /**
     * Callback method that gets called when the USB serial connection to the Arduino device is successfully opened
     */
    @Override
    public void onArduinoOpened() {
        //ToDo: Replace with customer message
        Toast.makeText(context, "Connection to the usb device was successful.", Toast.LENGTH_SHORT).show();
        String str = "Open connection...";
        arduino.send(str.getBytes());
    }

    /**
     * Callback method that gets called when the user denies permission to use the USB device
     */
    @SuppressWarnings("Convert2Lambda")
    @Override
    public void onUsbPermissionDenied() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arduino.reopen();
            }
        }, 3000);
    }

    /**
     * Initializes the Arduino object with the given context and baud rate.
     *
     * @param context  the context to use for initialization
     * @param baudRate the baud rate to use for communication
     */
    private void init(android.content.Context context, int baudRate) {
        arduino = new Arduino(context);
        arduino.setBaudRate(baudRate);
    }

    /**
     * Initializes the Arduino object with the given context, baud rate, and vendor ID.
     * This methode is used if you have not an arduino device.
     *
     * @param context  the context to use for initialization
     * @param baudRate the baud rate to use for communication
     * @param vendorId the vendor ID for usb devices witch is not an arduino devvice
     */
    private void init(android.content.Context context, int baudRate, int vendorId) {
        arduino = new Arduino(context);
        arduino.setBaudRate(baudRate);
        arduino.addVendorId(vendorId);
    }

    /**
     * Sets the listener for read serial data from usb device.
     */
    public void setListener() {
        arduino.setArduinoListener(this);
    }


    /**
     * Closes the UsbSerialReader and sets the UWB device as disconnected.
     */
    public void close() {
        arduino.unsetArduinoListener();
        arduino.close();
        setUwbIsConnected(false);
    }

    /**
     * Parses the new status to the ActualMeasuringData object.
     *
     * @param isConnected set status uwb
     */
    private void setUwbIsConnected(boolean isConnected) {
        if (isConnected) {
            ActualMeasuringData.getInstance().setUwbDeviceIsConnected(true);
        } else {
            ActualMeasuringData.getInstance().setUwbDistanceRssi(0);
            ActualMeasuringData.getInstance().setUwbDistanceToa(0);
            ActualMeasuringData.getInstance().setUwbRssiFiltered(0);
            ActualMeasuringData.getInstance().setUwbRssi(0);
            ActualMeasuringData.getInstance().setUwbDirection(0);
            ActualMeasuringData.getInstance().setUwbDeviceIsConnected(false);
        }
    }

    /**
     * Sets the UWB short address of the follower.
     * To filter the correspondent ultra wide-band tag
     *
     * @param uwbShortAddress the UWB short address to set for the follower
     */
    public void setUwbShortAddress(String uwbShortAddress) {
        this.uwbShortAddress = uwbShortAddress;
    }

    /**
     * Defines the available baud rates for USB serial communication.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({BAUD_RATE_300, BAUD_RATE_1200, BAUD_RATE_2400, BAUD_RATE_4800, BAUD_RATE_9600, BAUD_RATE_19200, BAUD_RATE_38400, BAUD_RATE_57600, BAUD_RATE_74880, BAUD_RATE_115200, BAUD_RATE_230400, BAUD_RATE_250000})
    public @interface USBSerialBaudRate {
    }

    /**
     * Represents a data object for a USB serial message.
     */
    public static class UsbSerialMessageData {
        private double uwbDistanceToa;
        private double uwbRssi;
        private String uwbShortAddress;

        /**
         * Constructs a UsbSerialMessageData object with the given data values.
         *
         * @param uwbDistanceToa the distance value to set for UWB Distance Toa
         * @param uwbRssi        the value to set for UWB RSSI
         * @param shortAddress   the short address to set for the message
         */
        public UsbSerialMessageData(double uwbDistanceToa, double uwbRssi, String shortAddress) {
            this.uwbDistanceToa = uwbDistanceToa;
            this.uwbRssi = uwbRssi;
            this.uwbShortAddress = shortAddress;
        }

        /* Getter */
        public double getUwbDistanceToa() {
            return uwbDistanceToa;
        }

        /* Setter */
        public void setUwbDistanceToa(double uwbDistanceToa) {
            this.uwbDistanceToa = uwbDistanceToa;
        }

        public double getUwbRssi() {
            return uwbRssi;
        }

        public void setUwbRssi(double uwbRssi) {
            this.uwbRssi = uwbRssi;
        }

        public String getUwbShortAddress() {
            return uwbShortAddress;
        }

        public void setUwbShortAddress(String uwbShortAddress) {
            this.uwbShortAddress = uwbShortAddress;
        }
    }
}
