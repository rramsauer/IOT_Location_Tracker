package com.rramsauer.iotlocatiotrackerapp.ui.models;

/* Androidx imports */

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Class NavigateViewModel for Nav Fragment.
 * This class provides a live data model of the actual data ble container on top.
 *
 * @author Ramsauer René
 * @version V1.2
 * @see androidx.lifecycle.LiveData
 * @see androidx.lifecycle.MutableLiveData
 * @see androidx.lifecycle.ViewModel
 */
public class NavigateViewModel extends ViewModel {
    private static final int MODE_NAV_COMP = 0;
    private final MutableLiveData<String> navStatusViewTxt;
    private final MutableLiveData<Integer> navStatusInt;
    private final MutableLiveData<String> navCoordinate;
    private final MutableLiveData<String> navRssi;
    private final MutableLiveData<String> navDistance;
    private final MutableLiveData<String> navDegree;
    private final MutableLiveData<Float> navDegreePointer;

    /**
     * Constructor class NavigateViewModel
     *
     * @author Ramsauer René
     */
    public NavigateViewModel() {
        navStatusViewTxt = new MutableLiveData<>();
        navStatusViewTxt.setValue("Empty");

        navStatusInt = new MutableLiveData<>();
        navStatusInt.setValue(MODE_NAV_COMP);

        navCoordinate = new MutableLiveData<>();
        navCoordinate.setValue("Empty");

        navRssi = new MutableLiveData<>();
        navRssi.setValue("Empty");

        navDistance = new MutableLiveData<>();
        navDistance.setValue("Empty");

        navDegree = new MutableLiveData<>();
        navDegree.setValue("Empty");

        navDegreePointer = new MutableLiveData<>();
        navDegreePointer.setValue(0F);
    }

    /* Getter */
    public LiveData<String> getNavStatusViewText() {
        return navStatusViewTxt;
    }

    /* Setter */
    public void setNavStatusViewText(String input) {
        navStatusViewTxt.postValue(input);
    }

    public LiveData<Integer> getNavStatusView() {
        return navStatusInt;
    }

    public void setNavStatusView(Integer input) {
        navStatusInt.postValue(input);
    }

    public LiveData<String> getNavCoordinateText() {
        return navCoordinate;
    }

    public void setNavCoordinateText(String input) {
        navCoordinate.postValue(input);
    }

    public LiveData<String> getNavRssiText() {
        return navRssi;
    }

    public void setNavRssiText(String input) {
        navRssi.postValue(input);
    }

    public LiveData<String> getNavDistanceText() {
        return navDistance;
    }

    public void setNavDistanceText(String input) {
        navDistance.postValue(input);
    }

    public LiveData<String> getNavDegreeText() {
        return navDegree;
    }

    public void setNavDegreeText(String input) {
        navDegree.postValue(input);
    }

    public LiveData<Float> getNavDegreePointer() {
        return navDegreePointer;
    }

    public void setNavDegreePointer(Float input) {
        navDegreePointer.postValue(input);
    }
}
