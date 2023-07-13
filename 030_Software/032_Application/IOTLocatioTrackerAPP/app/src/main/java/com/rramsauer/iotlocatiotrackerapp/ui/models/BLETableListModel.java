package com.rramsauer.iotlocatiotrackerapp.ui.models;

/**
 * Class BLE Fragment.
 * This class provides a live data model of an item of ble measuring list.
 *
 * @author Ramsauer René
 * @version V1.2
 * @see androidx.lifecycle.LiveData
 * @see androidx.lifecycle.MutableLiveData
 * @see androidx.lifecycle.ViewModel
 */
public class BLETableListModel {
    String time;
    String rssiValue;
    String filterRssiValue;
    String distanceValue;

    /**
     * Constructor class BLETableListModel.
     *
     * @param time            Passed the time of measurement.
     * @param rssiValue       Passed the measured RSSI value.
     * @param filterRssiValue Passed the filtered RSSI value.
     * @param distanceValue   Passed the calculates distance value.
     * @author Ramsauer René
     */
    public BLETableListModel(String time, String rssiValue, String filterRssiValue, String distanceValue) {
        this.time = time;
        this.rssiValue = rssiValue;
        this.filterRssiValue = filterRssiValue;
        this.distanceValue = distanceValue;
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

    public String getDistanceValue() {
        return distanceValue;
    }
}
