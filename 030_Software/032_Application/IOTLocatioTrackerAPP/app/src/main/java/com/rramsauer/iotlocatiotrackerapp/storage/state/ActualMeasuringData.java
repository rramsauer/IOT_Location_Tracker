package com.rramsauer.iotlocatiotrackerapp.storage.state;

import android.location.Location;

import java.io.Serializable;

public class ActualMeasuringData implements Serializable {
    private static ActualMeasuringData INSTANCE = null;
    /* Bluetooth LE */
    private String bleDeviceName;
    private String bleDeviceId;
    private double bleRssi;
    private double bleRssiFiltered;
    private double bleDistance;
    private double bleDirection;
    private boolean bleDeviceIsConnected;
    /* Ultra-Wide-band */
    private String uwbDeviceShortAddress;
    private String uwbDeviceUid;
    private double uwbRssi;
    private double uwbRssiFiltered;
    private double uwbDistanceToa;
    private double uwbDistanceRssi;
    private double uwbDirection;
    private boolean uwbDeviceIsConnected;
    /* Navigation */
    private double deviceDirection;
    private double deviceGeoDirection;
    private double deviceMovementDirection;
    private double deviceCoordinateLongitudeDG; /* N - S */
    private double deviceCoordinateLatitudeDG; /* E - W */
    private Location deviceLocation;

    public static ActualMeasuringData getInstance() {
        if (INSTANCE == null) {
            synchronized (ActualMeasuringData.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ActualMeasuringData();
                }
            }
        }
        return INSTANCE;
    }

    /* Getter */
    /* Bluetooth LE */
    public String getBleDeviceName() {
        return bleDeviceName;
    }

    /* Setter */
    /* Bluetooth LE */
    public void setBleDeviceName(String bleDeviceName) {
        this.bleDeviceName = bleDeviceName;
    }

    public String getBleDeviceId() {
        return bleDeviceId;
    }

    public void setBleDeviceId(String bleDeviceId) {
        this.bleDeviceId = bleDeviceId;
    }

    public double getBleRssi() {
        return bleRssi;
    }

    public void setBleRssi(double bleRssi) {
        this.bleRssi = bleRssi;
    }

    public double getBleRssiFiltered() {
        return bleRssiFiltered;
    }

    public void setBleRssiFiltered(double bleRssiFiltered) {
        this.bleRssiFiltered = bleRssiFiltered;
    }

    public double getBleDistance() {
        return bleDistance;
    }

    public void setBleDistance(double bleDistance) {
        this.bleDistance = bleDistance;
    }

    public double getBleDirection() {
        return bleDirection;
    }

    public void setBleDirection(double bleDirection) {
        this.bleDirection = bleDirection;
    }

    public boolean isBleDeviceIsConnected() {
        return bleDeviceIsConnected;
    }

    public void setBleDeviceIsConnected(boolean bleDeviceIsConnected) {
        this.bleDeviceIsConnected = bleDeviceIsConnected;
    }

    /* Ultra-Wide-band */
    public String getUwbDeviceShortAddress() {
        return uwbDeviceShortAddress;
    }

    /* Ultra-Wide-band */
    public void setUwbDeviceShortAddress(String uwbDeviceShortAddress) {
        this.uwbDeviceShortAddress = uwbDeviceShortAddress;
    }

    public String getUwbDeviceUid() {
        return uwbDeviceUid;
    }

    public void setUwbDeviceUid(String uwbDeviceUid) {
        this.uwbDeviceUid = uwbDeviceUid;
    }

    public double getUwbRssi() {
        return uwbRssi;
    }

    public void setUwbRssi(double uwbRssi) {
        this.uwbRssi = uwbRssi;
    }

    public double getUwbRssiFiltered() {
        return uwbRssiFiltered;
    }

    public void setUwbRssiFiltered(double uwbRssiFiltered) {
        this.uwbRssiFiltered = uwbRssiFiltered;
    }

    public double getUwbDistanceToa() {
        return uwbDistanceToa;
    }

    public void setUwbDistanceToa(double uwbDistanceToa) {
        this.uwbDistanceToa = uwbDistanceToa;
    }

    public double getUwbDistanceRssi() {
        return uwbDistanceRssi;
    }

    public void setUwbDistanceRssi(double uwbDistanceRssi) {
        this.uwbDistanceRssi = uwbDistanceRssi;
    }

    public double getUwbDirection() {
        return uwbDirection;
    }

    public void setUwbDirection(double uwbDirection) {
        this.uwbDirection = uwbDirection;
    }

    public boolean isUwbDeviceIsConnected() {
        return uwbDeviceIsConnected;
    }

    public void setUwbDeviceIsConnected(boolean uwbDeviceIsConnected) {
        this.uwbDeviceIsConnected = uwbDeviceIsConnected;
    }

    /* Navigation */
    public double getDeviceDirection() {
        return deviceDirection;
    }

    /* Navigation */
    public void setDeviceDirection(double deviceDirection) {
        this.deviceDirection = deviceDirection;
    }

    public double getDeviceGeoDirection() {
        return deviceGeoDirection;
    }

    public void setDeviceGeoDirection(double deviceGeoDirection) {
        this.deviceGeoDirection = deviceGeoDirection;
    }

    public double getDeviceMovementDirection() {
        return deviceMovementDirection;
    }

    public void setDeviceMovementDirection(double deviceMovementDirection) {
        this.deviceMovementDirection = deviceMovementDirection;
    }

    public double getDeviceCoordinateLongitudeDG() {
        return deviceCoordinateLongitudeDG;
    }

    public void setDeviceCoordinateLongitudeDG(double deviceCoordinateLongitudeDG) {
        this.deviceCoordinateLongitudeDG = deviceCoordinateLongitudeDG;
    }

    public double getDeviceCoordinateLatitudeDG() {
        return deviceCoordinateLatitudeDG;
    }

    public void setDeviceCoordinateLatitudeDG(double deviceCoordinateLatitudeDG) {
        this.deviceCoordinateLatitudeDG = deviceCoordinateLatitudeDG;
    }

    public Location getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(Location deviceLocation) {
        this.deviceLocation = deviceLocation;
    }
}