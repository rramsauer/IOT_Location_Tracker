package com.rramsauer.iotlocatiotrackerapp.storage.csv;

import com.opencsv.bean.CsvBindByName;

import java.util.Date;

/**
 * This class represents a row of data in a CSV file containing measuring data.
 * It contains all fields of the measuring data of the Application.
 * The class also includes a constant representing the size of a row in bytes.
 */
public class CsvMeasuringData {
    public final static double SIZE_OF_ROW = 1.0578947368;
    /* Time */
    @CsvBindByName(column = "TIME")
    private Date time;
    /* Bluetooth LE */
    @CsvBindByName(column = "BLE DEVICE NAME")
    private String bleDeviceName;
    @CsvBindByName(column = "BLE DEVICE ID")
    private String bleDeviceId;
    @CsvBindByName(column = "BLE RSSI")
    private Double bleRssi;
    @CsvBindByName(column = "BLE RSSI FILTERED")
    private Double bleRssiFiltered;
    @CsvBindByName(column = "BLE DISTANCE")
    private Double bleDistance;
    @CsvBindByName(column = "BLE DIRECTION")
    private Double bleDirection;
    /* UWB */
    @CsvBindByName(column = "UWB SHORT ADDRESS")
    private String uwbDeviceShortAddress;
    @CsvBindByName(column = "UWB DEVICE UID")
    private String uwbDeviceUid;
    @CsvBindByName(column = "UWB RSSI")
    private Double uwbRssi;
    @CsvBindByName(column = "UWB RSSI FILTERED")
    private Double uwbRssiFiltered;
    @CsvBindByName(column = "UWB DISTANCE TOA")
    private Double uwbDistanceToa;
    @CsvBindByName(column = "UWB DISTANCE RSSI")
    private Double uwbDistanceRssi;
    @CsvBindByName(column = "UWB DIRECTION")
    private Double uwbDirection;

    @CsvBindByName(column = "UWB DISTANCE RSSI FILTERED")
    private Double uwbDistanceRssiFiltered;
    /* DEVICE POS */
    @CsvBindByName(column = "DEVICE DIRECTION")
    private Double deviceDirection;
    @CsvBindByName(column = "DEVICE GEO DIRECTION")
    private Double deviceGeoDirection;
    @CsvBindByName(column = "DEVICE MOVEMENT DIRECTION")
    private Double deviceMovementDirection;
    @CsvBindByName(column = "DEVICE COORDINATE LONGITUDE")
    private Double coordinateLongitudeDG;
    @CsvBindByName(column = "DEVICE COORDINATE LATITUDE")
    private Double coordinateLatitudeDG;
    /* Settings */
    @CsvBindByName(column = "UWB CONNECTED")
    private Boolean uwbConnected;
    @CsvBindByName(column = "BLE CONNECTED")
    private Boolean bleConnected;


    /**
     * Constructs a new CsvMeasuringData object.
     */
    public CsvMeasuringData(Date time, /* TIME */
                            String bleDeviceName, String bleDeviceId, Double bleRssi, Double bleRssiFiltered, Double bleDistance, Double bleDirection, /* Bluetooth LE */
                            String uwbDeviceShortAddress, String uwbDeviceUid, Double uwbRssi, Double uwbRssiFiltered, Double uwbDistanceToa, Double uwbDistanceRssi, Double uwbDirection, /* UWB */
                            Double deviceDirection, Double deviceGeoDirection, Double deviceMovementDirection, /* DEVICE */
                            Double coordinateLongitudeDG, Double coordinateLatitudeDG,  /* COORDINATE */
                            Boolean uwbConnected, Boolean bleConnected /* STATUS */
    ) {
        /* Bluetooth LE */
        this.time = time;
        this.bleDeviceName = bleDeviceName;
        this.bleDeviceId = bleDeviceId;
        this.bleRssi = bleRssi;
        this.bleRssiFiltered = bleRssiFiltered;
        this.bleDistance = bleDistance;
        this.bleDirection = bleDirection;
        /* Ultra-Wide-band */
        this.uwbDeviceShortAddress = uwbDeviceShortAddress;
        this.uwbDeviceUid = uwbDeviceUid;
        this.uwbRssi = uwbRssi;
        this.uwbRssiFiltered = uwbRssiFiltered;
        this.uwbDistanceToa = uwbDistanceToa;
        this.uwbDistanceRssi = uwbDistanceRssi;
        this.uwbDirection = uwbDirection;
        /* Navigation DEVICE */
        this.deviceDirection = deviceDirection;
        this.deviceGeoDirection = deviceGeoDirection;
        this.deviceMovementDirection = deviceMovementDirection;
        /* Navigation COORDINATE */
        this.coordinateLongitudeDG = coordinateLongitudeDG;
        this.coordinateLatitudeDG = coordinateLatitudeDG;
        /* Status */
        this.uwbConnected = uwbConnected;
        this.bleConnected = bleConnected;
    }
}