package com.rramsauer.iotlocatiotrackerapp.ui.models;

/**
 * Class UWBToaTableListModel for UWB Fragment.
 * This class provides a live data model of an item of uwb toa measuring list.
 * Table 2
 *
 * @author Ramsauer René
 * @version V1.2
 * @see androidx.lifecycle.LiveData
 * @see androidx.lifecycle.MutableLiveData
 * @see androidx.lifecycle.ViewModel
 */
public class UwbToaTableListModel {
    String time;
    String toaDistanceValue;

    /**
     * Constructor class UwbToaTableListModel.
     *
     * @param time             Passed the time of measurement.
     * @param toaDistanceValue Passed the calculates TOA distance value.
     * @author Ramsauer René
     */
    public UwbToaTableListModel(String time, String toaDistanceValue) {
        this.time = time;
        this.toaDistanceValue = toaDistanceValue;
    }

    /* Getter */
    public String getTime() {
        return time;
    }

    public String getToaDistanceValue() {
        return toaDistanceValue;
    }
}
