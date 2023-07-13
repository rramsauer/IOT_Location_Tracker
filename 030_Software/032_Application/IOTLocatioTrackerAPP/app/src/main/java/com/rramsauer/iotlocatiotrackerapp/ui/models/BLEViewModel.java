package com.rramsauer.iotlocatiotrackerapp.ui.models;

/* Androidx imports */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Class BLEViewModel for BLE Fragment.
 * This class provides a live data model of the actual data ble container on top.
 *
 * @author Ramsauer René
 * @version V1.2
 * @see androidx.lifecycle.LiveData
 * @see androidx.lifecycle.MutableLiveData
 * @see androidx.lifecycle.ViewModel
 */
public class BLEViewModel extends ViewModel {
    private final MutableLiveData<String> bleDevice;
    private final MutableLiveData<String> bleDeviceID;
    private final MutableLiveData<String> bleRSSI;
    private final MutableLiveData<String> bleDistance;

    /**
     * Constructor class BLEViewModel
     *
     * @author Ramsauer René
     */
    public BLEViewModel() {
        bleDevice = new MutableLiveData<>();
        bleDevice.setValue("Empty");
        bleDeviceID = new MutableLiveData<>();
        bleDeviceID.setValue("Empty");
        bleRSSI = new MutableLiveData<>();
        bleRSSI.setValue("Empty");
        bleDistance = new MutableLiveData<>();
        bleDistance.setValue("Empty");
    }

    /* Getter */
    public LiveData<String> getBleDeviceText() {
        return bleDevice;
    }

    /* Setter */
    public void setBleDeviceText(String input) {
        bleDevice.postValue(input);
    }

    public LiveData<String> getBleDeviceIDText() {
        return bleDeviceID;
    }

    public void setBleDeviceIDText(String input) {
        bleDeviceID.postValue(input);
    }

    public LiveData<String> getBleRSSIText() {
        return bleRSSI;
    }

    public void setBleRSSIText(String input) {
        bleRSSI.postValue(input);
    }

    public LiveData<String> getBleDistanceText() {
        return bleDistance;
    }

    public void setBleDistanceText(String input) {
        bleDistance.postValue(input);
    }

}
