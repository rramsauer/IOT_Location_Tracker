package com.rramsauer.iotlocatiotrackerapp.ui.models;

/* Androidx imports */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Class UWBViewModel for UWB Fragment.
 * This class provides a live data model of the actual data uwb container on top.
 *
 * @author Ramsauer René
 * @version V1.2
 * @see androidx.lifecycle.LiveData
 * @see androidx.lifecycle.MutableLiveData
 * @see androidx.lifecycle.ViewModel
 */
public class UWBViewModel extends ViewModel {
    private final MutableLiveData<String> uwbDeviceShortAddress;
    private final MutableLiveData<String> uwbDeviceUid;
    private final MutableLiveData<String> uwbRSSI;
    private final MutableLiveData<String> uwbDistanceToa;
    private final MutableLiveData<String> uwbDistanceRssi;

    /**
     * Constructor class UWBViewModel
     *
     * @author Ramsauer René
     */
    public UWBViewModel() {
        uwbDeviceShortAddress = new MutableLiveData<>();
        uwbDeviceShortAddress.setValue("Empty");

        uwbDeviceUid = new MutableLiveData<>();
        uwbDeviceUid.setValue("Empty");

        uwbRSSI = new MutableLiveData<>();
        uwbRSSI.setValue("Empty");

        uwbDistanceToa = new MutableLiveData<>();
        uwbDistanceToa.setValue("Empty");

        uwbDistanceRssi = new MutableLiveData<>();
        uwbDistanceRssi.setValue("Empty");
    }

    /* Getter */
    public LiveData<String> getUwbDeviceShortAddressText() {
        return uwbDeviceShortAddress;
    }

    /* Setter */
    public void setUwbDeviceShortAddressText(String input) {
        uwbDeviceShortAddress.postValue(input);
    }

    public LiveData<String> getUwbDeviceUidText() {
        return uwbDeviceUid;
    }

    public void setUwbDeviceUidText(String input) {
        uwbDeviceUid.postValue(input);
    }

    public LiveData<String> getUwbRSSIText() {
        return uwbRSSI;
    }

    public void setUwbRSSIText(String input) {
        uwbRSSI.postValue(input);
    }

    public LiveData<String> getUwbDistanceToaText() {
        return uwbDistanceToa;
    }

    public void setUwbDistanceToaText(String input) {
        uwbDistanceToa.postValue(input);
    }

    public LiveData<String> getUwbDistanceRssiText() {
        return uwbDistanceRssi;
    }

    public void setUwbDistanceRssiText(String input) {
        uwbDistanceRssi.postValue(input);
    }

}
