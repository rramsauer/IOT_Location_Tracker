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
import com.rramsauer.iotlocatiotrackerapp.databinding.FragmentBleBinding;
import com.rramsauer.iotlocatiotrackerapp.storage.state.ActualMeasuringData;
import com.rramsauer.iotlocatiotrackerapp.ui.adapter.BLETableListAdapter;
import com.rramsauer.iotlocatiotrackerapp.ui.models.BLETableListModel;
import com.rramsauer.iotlocatiotrackerapp.ui.models.BLEViewModel;
import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;
import com.rramsauer.iotlocatiotrackerapp.util.notification.CustomUserInformation;
import com.rramsauer.iotlocatiotrackerapp.util.str.StringConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class BLE Fragment.
 * This class provides the ui for the fragment BLE. Which can be called via the button in the navigation bar.
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
 * @see com.rramsauer.iotlocatiotrackerapp.ui.models
 * @see com.rramsauer.iotlocatiotrackerapp.ui.adapter
 */
public class BLEFragment extends Fragment {
    final static int BLE_TABLE_MAX_ROW = 20;
    final static int BLE_VIEW_UPDATE_UI_INTERVAL = 500;
    /* Testing Variable */
    private static int count;
    private final Handler handler = new Handler();
    private FragmentBleBinding binding;
    /* UI TABLE */
    private RecyclerView recyclerView;
    private BLETableListAdapter adapter;
    private List<BLETableListModel> listBLEMeasure = new ArrayList<>();
    /* UI CONTAINER */
    private BLEViewModel bleViewModel;
    /* UI UPDATE */
    private Runnable bleViewUpdateUiRunnable;

    /**
     * This function override onCreateView()
     *
     * @author Ramsauer René
     * @override onCreateView in class Fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("CLASS BLEFragment", "onCreateView");
        bleViewModel = new ViewModelProvider(this).get(BLEViewModel.class);
        binding = FragmentBleBinding.inflate(inflater, container, false);
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
        Logger.d("CLASS BLEFragment", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        setBLETableView(view);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        handler.postDelayed(bleViewUpdateUiRunnable, BLE_VIEW_UPDATE_UI_INTERVAL);
        bleViewUpdateUiThread();
        if (!ActualMeasuringData.getInstance().isBleDeviceIsConnected()) {
            CustomUserInformation.showSnackbarWarning(BLEFragment.this.getView(),
                    "NO BLE DEVICE CONNECTED ! \n" +
                            "Go to the settings-tab and and connect your IOT-LocationTracker.",
                    Snackbar.LENGTH_SHORT, Gravity.BOTTOM, true
            );
        }
    }

    /**
     * This function override onDestroyView()
     *
     * @author Ramsauer René
     * @override onDestroyView in class Fragment
     */
    @Override
    public void onDestroyView() {
        Logger.d("CLASS BLEFragment", "onDestroyView");
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
        handler.removeCallbacks(bleViewUpdateUiRunnable);
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
        Logger.d("CLASS BLEFragment", "Initialize date ...");
        /* INIT ARRAY LIST */
        listBLEMeasure = new ArrayList<>();
        /* INIT TextView BLE DEVICE NAME */
        bleViewModel.setBleDeviceText("Not Connected");
        TextView bleDeviceText = binding.fragmentBleContainerActualValueLabelTitleBLEDevice;
        bleViewModel.getBleDeviceText().observe(getViewLifecycleOwner(), bleDeviceText::setText);
        /* INIT TextView BLE DEVICE ID */
        bleViewModel.setBleDeviceIDText("No Date");
        TextView bleDeviceIDText = binding.fragmentBleContainerActualValueLabelTitleBLEDeviceID;
        bleViewModel.getBleDeviceIDText().observe(getViewLifecycleOwner(), bleDeviceIDText::setText);
        /* INIT TextView RSSI WITH NO DATA */
        bleViewModel.setBleRSSIText("No Date");
        TextView bleBleRSSIText = binding.fragmentBleContainerActualValueLabelRSSI;
        bleViewModel.getBleRSSIText().observe(getViewLifecycleOwner(), bleBleRSSIText::setText);
        /* INIT TextView DISTANCE WITH NO DATA */
        bleViewModel.setBleDistanceText("No Date");
        TextView BleDistanceText = binding.fragmentBleContainerActualValueLabelDistance;
        bleViewModel.getBleDistanceText().observe(getViewLifecycleOwner(), BleDistanceText::setText);
    }

    /**
     * This function implements the logic for upgrading the ui of ble-fragment.
     *
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: bleViewUpdateUiThread()
     * @see java.lang
     * @since <Android API 32 Platform></Android>
     */
    private void bleViewUpdateUiThread() {
        if (bleViewUpdateUiRunnable == null) {
            bleViewUpdateUiRunnable = () -> {
                ActualMeasuringData actualMeasuringData = ActualMeasuringData.getInstance();
                updateContainerBLEVDate(
                        actualMeasuringData.getBleDeviceName(), /* Device name : String */
                        actualMeasuringData.getBleDeviceId() /* Device id : String */
                );
                if (ActualMeasuringData.getInstance().isBleDeviceIsConnected()) {
                    updateTableSingleItem(new BLETableListModel(
                            StringConverter.dateToString(new Date(), "hh:mm:ss.SSS"), /* Time : String */
                            String.valueOf((int) actualMeasuringData.getBleRssi()), /* Rssi : String */
                            StringConverter.decimalToString(actualMeasuringData.getBleRssiFiltered(), "0.00"), /* Rssi Filtered: String */
                            StringConverter.decimalToString(actualMeasuringData.getBleDistance(), "0.00") /* Distance : String */
                    ));
                    updateContainerBLEVMeasuringDate(
                            actualMeasuringData.getBleRssi(), /* Rssi : Double */
                            actualMeasuringData.getBleDistance()); /* Distance : Double */
                }
                handler.postDelayed(bleViewUpdateUiRunnable, BLE_VIEW_UPDATE_UI_INTERVAL);
            };
            handler.postDelayed(bleViewUpdateUiRunnable, BLE_VIEW_UPDATE_UI_INTERVAL);
        }
    }

    /**
     * This function provides BLE TableView
     *
     * @param view main View
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: setBLETableView(view)
     * @see androidx.recyclerview.widget.RecyclerView
     * @see java.util.Date
     * @since <Android API 32 Platform></Android>
     */
    private void setBLETableView(View view) {
        Logger.d("CLASS BLEFragment", "Set Table View ...");
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new BLETableListAdapter(getContext(), listBLEMeasure);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * This function update the table with one element and remove the last element if "BLE_TABLE_MAX_ROW" was reached.
     *
     * @param item Passing the table List Module
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateTableSingleItem(tableListItem)
     * @see com.rramsauer.iotlocatiotrackerapp.ui.models
     * @see com.rramsauer.iotlocatiotrackerapp.ui.adapter
     * @since <Android API 32 Platform></Android>
     */
    private void updateTableSingleItem(BLETableListModel item) {
        int insertIndex = 0;
        if (listBLEMeasure.size() >= BLE_TABLE_MAX_ROW) {
            listBLEMeasure.remove(listBLEMeasure.size() - 1);
            adapter.notifyItemRemoved(listBLEMeasure.size() - 1);
        }
        if (!(recyclerView.getScrollState() == recyclerView.SCROLL_STATE_DRAGGING)) {
            recyclerView.scrollToPosition(0);
        }
        listBLEMeasure.add(insertIndex, item);
        adapter.notifyItemInserted(insertIndex);
    }

    /**
     * This function update device information of Container
     *
     * @param deviceName Passed device name.
     * @param deviceID   Passed device ID.
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateContainerBLEVDate("Device Name", "Device ID")
     * @see BLEFragment
     * @since <Android API 32 Platform></Android>
     */
    private void updateContainerBLEVDate(String deviceName, String deviceID) {
        bleViewModel.setBleDeviceText(deviceName);
        bleViewModel.setBleDeviceIDText(deviceID);
    }

    /**
     * This function update actual value of Container
     *
     * @param rssi     Passed rssi value.
     * @param distance Passed the value of the actual position.
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateContainerBLEVMeasuringDate(80.4F, 100.45F)
     * @see BLEFragment
     * @since <Android API 32 Platform></Android>
     */
    private void updateContainerBLEVMeasuringDate(double rssi, double distance) {
        bleViewModel.setBleRSSIText(String.valueOf((int) rssi));
        bleViewModel.setBleDistanceText(StringConverter.decimalToString(distance, "0.00"));
    }
}