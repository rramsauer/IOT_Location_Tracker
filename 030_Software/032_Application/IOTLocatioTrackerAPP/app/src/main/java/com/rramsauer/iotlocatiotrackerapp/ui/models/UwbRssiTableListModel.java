package com.rramsauer.iotlocatiotrackerapp.ui.models;

/**
 * Class UwbRssiTableListModel for UWB Fragment.
 * This class provides a live data model of an item of uwb rssi measuring list.
 * Table 1
 *
 * @author Ramsauer René
 * @version V1.2
 * @see androidx.lifecycle.LiveData
 * @see androidx.lifecycle.MutableLiveData
 * @see androidx.lifecycle.ViewModel
 */
public class UwbRssiTableListModel {
    String time;
    String rssiValue;
    String filterRssiValue;
    String distanceRssiValue;


    /**
     * Constructor class UwbRssiTableListModel.
     *
     * @param time              Passed the time of measurement.
     * @param rssiValue         Passed the measured RSSI value.
     * @param filterRssiValue   Passed the filtered RSSI value.
     * @param distanceRssiValue Passed the calculates distance value based on RSSI.
     * @author Ramsauer René
     */
    public UwbRssiTableListModel(String time, String rssiValue, String filterRssiValue, String distanceRssiValue) {
        this.time = time;
        this.rssiValue = rssiValue;
        this.filterRssiValue = filterRssiValue;
        this.distanceRssiValue = distanceRssiValue;
    }

    /* Getter */
    public String getTime() {
        return time;
    }

    public String getRssiValue() {
        return rssiValue;
    }

    public String getFilterRssiValue() {
        return filterRssiValue;
    }

    public String getDistanceRssiValue() {
        return distanceRssiValue;
    }
}
