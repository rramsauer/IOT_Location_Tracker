package com.rramsauer.iotlocatiotrackerapp.ui.view;

/* Android imports */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.rramsauer.iotlocatiotrackerapp.R;
import com.rramsauer.iotlocatiotrackerapp.databinding.FragmentUwbBinding;
import com.rramsauer.iotlocatiotrackerapp.storage.state.ActualMeasuringData;
import com.rramsauer.iotlocatiotrackerapp.ui.adapter.UwbRssiTableListAdapter;
import com.rramsauer.iotlocatiotrackerapp.ui.adapter.UwbToaTableListAdapter;
import com.rramsauer.iotlocatiotrackerapp.ui.models.UWBViewModel;
import com.rramsauer.iotlocatiotrackerapp.ui.models.UwbRssiTableListModel;
import com.rramsauer.iotlocatiotrackerapp.ui.models.UwbToaTableListModel;
import com.rramsauer.iotlocatiotrackerapp.util.notification.CustomUserInformation;
import com.rramsauer.iotlocatiotrackerapp.util.str.StringConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class UWB Fragment.
 * This class provides the ui for the fragment UWB. Which can be called via the button in the navigation bar.
 *
 * @author Ramsauer René
 * @version V1.2
 * @implNote This class was implemented with the current standards and proposed technologies of Android.
 * Furthermore, a fread for updating the UI was implemented.
 * @see androidx.fragment.app.Fragment
 * @see androidx.lifecycle.ViewModelProvider
 * @see android.content.SharedPreferences
 * @see android.os.Handler
 * @see android.widget
 * @see android.view
 */
public class UWBFragment extends Fragment {
    final static int UWB_RSSI_TABLE_MAX_ROW = 10;
    final static int UWB_TOA_TABLE_MAX_ROW = 20;
    final static int UWB_VIEW_UPDATE_UI_INTERVAL = 500;
    /* Testing Variable */
    private static int count;
    private final Handler handler = new Handler();
    private FragmentUwbBinding binding;
    /* UI TABLE UWB RSSI */
    private RecyclerView rssiRecyclerView;
    private UwbRssiTableListAdapter rssiAdapter;
    private List<UwbRssiTableListModel> listRssiUwbMeasure = new ArrayList<>();
    /* UI TABLE UWB TOA */
    private RecyclerView toaRecyclerView;
    private UwbToaTableListAdapter toaAdapter;
    private List<UwbToaTableListModel> listToaUwbMeasure = new ArrayList<>();
    /* UI CONTAINER */
    private UWBViewModel uwbViewModel;
    /* UI UPDATE */
    private Runnable uwbViewUpdateUiRunnable;

    /**
     * This function override onCreateView()
     *
     * @author Ramsauer René
     * @override onCreateView in class Fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        uwbViewModel = new ViewModelProvider(this).get(UWBViewModel.class);
        binding = FragmentUwbBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dataInitialize();
        return root;
    }

    /**
     * This function override onViewCreated()
     *
     * @author Ramsauer René
     * @override onViewCreated in class Fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUwbRssiTableView(view);
        setUwbToaTableView(view);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        handler.postDelayed(uwbViewUpdateUiRunnable, UWB_VIEW_UPDATE_UI_INTERVAL);
        uwbViewUpdateUiThread();
        if (!ActualMeasuringData.getInstance().isUwbDeviceIsConnected()) {
            CustomUserInformation.showSnackbarWarning(UWBFragment.this.getView(),
                    "UWB IS NOT CONNECTED ! \n" +
                            "Check your Galaxy uwb extension.\n",
                    Snackbar.LENGTH_SHORT, Gravity.BOTTOM, true
            );
        }
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * This function override onDestroyView()
     *
     * @author Ramsauer René
     * @override onDestroyView in class Fragment
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * This function override onStop()
     *
     * @author Ramsauer René
     * @Override onStop in class Fragment
     */
    @Override
    public void onStop() {
        handler.removeCallbacks(uwbViewUpdateUiRunnable);
        super.onStop();
    }

    /**
     * This function implements the initialisation of dafoult ui Data
     *
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: dataInitialize();
     * @see androidx.fragment.app.Fragment
     * @see java.util.ArrayList
     * @see java.util.ArrayList
     * @see java.util.ArrayList
     * @since <Android API 32 Platform></Android>
     */
    private void dataInitialize() {
        /* INIT ARRAY LIST */
        listToaUwbMeasure = new ArrayList<>();
        listRssiUwbMeasure = new ArrayList<>();
        /* INIT TextView UWB DEVICE SHORT ADDRESS */
        uwbViewModel.setUwbDeviceShortAddressText("Not Connected");
        TextView uwbDeviceShortAddressText = binding.fragmentUwbContainerActualValueLabelTitleShortAddress;
        uwbViewModel.getUwbDeviceShortAddressText().observe(getViewLifecycleOwner(), uwbDeviceShortAddressText::setText);
        /* INIT TextView UWB DEVICE UID */
        uwbViewModel.setUwbDeviceUidText("No Date");
        TextView uwbDeviceUidText = binding.fragmentUwbContainerActualValueLabelTitleUWBDeviceUID;
        uwbViewModel.getUwbDeviceUidText().observe(getViewLifecycleOwner(), uwbDeviceUidText::setText);
        /* INIT TextView RSSI WITH NO DATA */
        uwbViewModel.setUwbRSSIText("No Date");
        TextView uwbBleRSSIText = binding.fragmentUwbContainerActualValueLabelRSSIValue;
        uwbViewModel.getUwbRSSIText().observe(getViewLifecycleOwner(), uwbBleRSSIText::setText);
        /* INIT TextView RSSI DISTANCE WITH NO DATA */
        uwbViewModel.setUwbDistanceRssiText("No Date");
        TextView uwbDistanceRssiText = binding.fragmentUwbContainerActualValueLabelDistanceRssiValue;
        uwbViewModel.getUwbDistanceRssiText().observe(getViewLifecycleOwner(), uwbDistanceRssiText::setText);
        /* INIT TextView TOA DISTANCE WITH NO DATA */
        uwbViewModel.setUwbDistanceToaText("No Date");
        TextView uwbDistanceToaText = binding.fragmentUwbContainerActualValueLabelDistanceTOAValue;
        uwbViewModel.getUwbDistanceToaText().observe(getViewLifecycleOwner(), uwbDistanceToaText::setText);
    }

    /**
     * This function implements the logic for upgrading the ui of uwb-fragment.
     *
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: uwbViewUpdateUiThread()
     * @see java.lang
     * @since <Android API 32 Platform></Android>
     */
    private void uwbViewUpdateUiThread() {
        if (uwbViewUpdateUiRunnable == null) {
            uwbViewUpdateUiRunnable = () -> {
                ActualMeasuringData actualMeasuringData = ActualMeasuringData.getInstance();
                updateContainerUwbDate(
                        actualMeasuringData.getUwbDeviceShortAddress(), /* Device short address : String */
                        actualMeasuringData.getUwbDeviceUid() /* Device uid : String */
                );
                if (actualMeasuringData.isUwbDeviceIsConnected()) {
                    updateContainerUwbMeasuringDate(
                            actualMeasuringData.getUwbRssi(), /* Rssi : Double */
                            actualMeasuringData.getUwbDistanceRssi(), /* Distance rssi : Double */
                            actualMeasuringData.getUwbDistanceToa() /* Distance toa : Double */
                    );
                    updateRssiTableSingleItem(new UwbRssiTableListModel(
                            StringConverter.dateToString(new Date(), "hh:mm:ss.SSS"), /* Time : String */
                            String.valueOf((int) actualMeasuringData.getUwbRssi()), /* Rssi : String */
                            StringConverter.decimalToString(actualMeasuringData.getUwbRssiFiltered(), "0.00"), /* Distance rssi filtered : String */
                            StringConverter.decimalToString(actualMeasuringData.getUwbDistanceRssi(), "0.00") /* Distance rssi : String */
                    ));
                    updateToaTableSingleItem(new UwbToaTableListModel(
                            StringConverter.dateToString(new Date(), "hh:mm:ss.SSS"), /* Time : String */
                            StringConverter.decimalToString(actualMeasuringData.getUwbDistanceToa(), "0.00") /* Distance toa : String */
                    ));
                }
                handler.postDelayed(uwbViewUpdateUiRunnable, UWB_VIEW_UPDATE_UI_INTERVAL);
            };
            handler.postDelayed(uwbViewUpdateUiRunnable, UWB_VIEW_UPDATE_UI_INTERVAL);
        }
    }

    /**
     * This function provides UWB RSSI-TableView
     *
     * @param view main View
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: setUwbRssiTableView(view)
     * @see androidx.recyclerview.widget.RecyclerView
     * @see java.util.Date
     * @since <Android API 32 Platform></Android>
     */
    private void setUwbRssiTableView(View view) {
        rssiRecyclerView = view.findViewById(R.id.recycler_view_uwb_rssi);
        rssiRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rssiRecyclerView.setHasFixedSize(true);
        rssiAdapter = new UwbRssiTableListAdapter(getContext(), listRssiUwbMeasure);
        rssiRecyclerView.setAdapter(rssiAdapter);
        rssiAdapter.notifyDataSetChanged();
    }

    /**
     * This function provides UWB TOA-TableView
     *
     * @param view main View
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: setUwbToaTableView(view)
     * @see androidx.recyclerview.widget.RecyclerView
     * @see java.util.Date
     * @since <Android API 32 Platform></Android>
     */
    private void setUwbToaTableView(View view) {
        toaRecyclerView = view.findViewById(R.id.recycler_view_uwb_toa);
        toaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        toaRecyclerView.setHasFixedSize(true);
        toaAdapter = new UwbToaTableListAdapter(getContext(), listToaUwbMeasure);
        toaRecyclerView.setAdapter(toaAdapter);
        toaAdapter.notifyDataSetChanged();
    }

    /**
     * This function update the uwb-rssi-table with one element and remove the last element if "UWB_RSSI_TABLE_MAX_ROW" was reached.
     *
     * @param item Passing the table List Module
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateRssiTableSingleItem(tableListItem)
     * @since <Android API 32 Platform></Android>
     */
    private void updateRssiTableSingleItem(UwbRssiTableListModel item) {
        int insertIndex = 0;
        if (listRssiUwbMeasure.size() >= UWB_RSSI_TABLE_MAX_ROW) {
            listRssiUwbMeasure.remove(listRssiUwbMeasure.size() - 1);
            rssiAdapter.notifyItemRemoved(listRssiUwbMeasure.size() - 1);
        }
        if (!(rssiRecyclerView.getScrollState() == rssiRecyclerView.SCROLL_STATE_DRAGGING)) {
            rssiRecyclerView.scrollToPosition(0);
        }
        listRssiUwbMeasure.add(insertIndex, item);
        rssiAdapter.notifyItemInserted(insertIndex);
    }

    /**
     * This function update the uwb-toa-table with one element and remove the last element if "UWB_TOA_TABLE_MAX_ROW" was reached.
     *
     * @param item Passing the table List Module
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateToaTableSingleItem(tableListItem)
     * @since <Android API 32 Platform></Android>
     */
    private void updateToaTableSingleItem(UwbToaTableListModel item) {
        int insertIndex = 0;
        if (listToaUwbMeasure.size() >= UWB_TOA_TABLE_MAX_ROW) {
            listToaUwbMeasure.remove(listToaUwbMeasure.size() - 1);
            toaAdapter.notifyItemRemoved(listToaUwbMeasure.size() - 1);
        }
        if (!(toaRecyclerView.getScrollState() == toaRecyclerView.SCROLL_STATE_DRAGGING)) {
            toaRecyclerView.scrollToPosition(0);
        }
        listToaUwbMeasure.add(insertIndex, item);
        toaAdapter.notifyItemInserted(insertIndex);
    }

    /**
     * This function update device information of Container
     *
     * @param deviceName Passed device name.
     * @param deviceID   Passed device ID.
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateContainerUwbDate("Device Name", "Device ID")
     * @see BLEFragment
     * @since <Android API 32 Platform></Android>
     */
    private void updateContainerUwbDate(String deviceName, String deviceID) {
        uwbViewModel.setUwbDeviceShortAddressText(deviceName);
        uwbViewModel.setUwbDeviceUidText(deviceID);
    }

    /**
     * This function update actual value of Container
     *
     * @param rssi         Passed rssi value.
     * @param distanceRssi Passed the value of the actual position.(calculated by RSSI)
     * @param distanceToa  Passed the value of the actual position.(calculated by TOA)
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateContainerUwbMeasuringDate(80.4, 100.45, 34.5)
     * @see BLEFragment
     * @since <Android API 32 Platform></Android>
     */
    private void updateContainerUwbMeasuringDate(double rssi, double distanceRssi, double distanceToa) {
        uwbViewModel.setUwbRSSIText(String.valueOf(rssi));
        uwbViewModel.setUwbDistanceRssiText(StringConverter.decimalToString(distanceRssi, "0.00"));
        uwbViewModel.setUwbDistanceToaText(StringConverter.decimalToString(distanceToa, "0.00"));
    }
}