package com.rramsauer.iotlocatiotrackerapp.ui.view;

/* Android imports */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.rramsauer.iotlocatiotrackerapp.R;
import com.rramsauer.iotlocatiotrackerapp.databinding.FragmentNavigateBinding;
import com.rramsauer.iotlocatiotrackerapp.storage.state.ActualMeasuringData;
import com.rramsauer.iotlocatiotrackerapp.ui.models.NavigateViewModel;
import com.rramsauer.iotlocatiotrackerapp.util.compassAssistant.CompassAssistant;
import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;
import com.rramsauer.iotlocatiotrackerapp.util.str.StringConverter;

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
 * @see com.rramsauer.iotlocatiotrackerapp.ui.adapter
 * @see com.rramsauer.iotlocatiotrackerapp.ui.models
 */
public class NavigateFragment extends Fragment {
    final static int NAV_VIEW_UPDATE_UI_INTERVAL = 5;
    /* UI STATUS */
    private final static int MODE_NAV_COMP = 0;
    private final static int MODE_NAV_BLE = 1;
    private final static int MODE_NAV_UWB = 2;
    private final Handler handler = new Handler();
    /* FRAGMENT */
    private FragmentNavigateBinding binding;
    private NavigateViewModel navViewModel;
    /* UI UPDATE */
    private Runnable navViewUpdateUiRunnable;
    /* Compass */
    private CompassAssistant compassAssistant;
    private ImageView navDegreePointer;
    /* Coordinate */
    private String coordinateFormat;

    /**
     * This function override onCreateView()
     *
     * @author Ramsauer René
     * @override onCreateView in class Fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        navViewModel = new ViewModelProvider(this).get(NavigateViewModel.class);
        binding = FragmentNavigateBinding.inflate(inflater, container, false);
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
        coordinateFormat = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getString(R.string.settings_nav_coordinate_format_key), "");
        handler.postDelayed(navViewUpdateUiRunnable, NAV_VIEW_UPDATE_UI_INTERVAL);
        navViewUpdateUiThread();
        // Toast.makeText(getContext(),"BLE IS NOT ENABLE ! \n go to the settings-tab and enable BLE.", Toast.LENGTH_LONG).show();
        super.onViewCreated(view, savedInstanceState);
        super.onResume();
    }

    /**
     * This function override onResume()
     *
     * @author Ramsauer René
     * @override onResume in class Fragment
     */
    @Override
    public void onResume() {
        updateStatusNavView();
        super.onResume();
    }

    /**
     * This function override onStop()
     *
     * @author Ramsauer René
     * @Override onStop in class Fragment
     */
    @Override
    public void onStop() {
        handler.removeCallbacks(navViewUpdateUiRunnable);
        super.onStop();
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
        /* INIT TextView NAV STATUS TXT*/
        navViewModel.setNavStatusViewText(getString(R.string.fragmentNav_TextView_status_deactivated));
        TextView navStatusViewText = binding.fragmentNavTextViewStatus;
        navViewModel.getNavStatusViewText().observe(getViewLifecycleOwner(), navStatusViewText::setText);

        /* INIT TextView BLE DEVICE ID */
        navViewModel.setNavCoordinateText(" ");
        TextView navCoordinateText = binding.fragmentNavTextViewCoordinates;
        navViewModel.getNavCoordinateText().observe(getViewLifecycleOwner(), navCoordinateText::setText);

        /* INIT TextView BLE DEVICE ID */
        navViewModel.setNavRssiText(getString(R.string.str_placeholder_number_integer));
        TextView navRssiText = binding.fragmentNavTextViewRssi;
        navViewModel.getNavRssiText().observe(getViewLifecycleOwner(), navRssiText::setText);

        /* INIT TextView BLE DEVICE ID */
        navViewModel.setNavDistanceText(getString(R.string.str_placeholder_number_float));
        TextView navDistanceText = binding.fragmentNavTextViewDistance;
        navViewModel.getNavDistanceText().observe(getViewLifecycleOwner(), navDistanceText::setText);

        /* INIT TextView BLE DEVICE ID */
        navViewModel.setNavDegreeText(getString(R.string.str_placeholder_number_integer));
        TextView navDegreeText = binding.fragmentNavTextViewDegree;
        navViewModel.getNavDegreeText().observe(getViewLifecycleOwner(), navDegreeText::setText);

        /* INIT TextView BLE DEVICE ID */
        navViewModel.setNavDegreePointer(0F);
        /* ImageView */
        navDegreePointer = binding.fragmentNavImageViewPointer;
        navViewModel.getNavDegreePointer().observe(getViewLifecycleOwner(), navDegreePointer::setRotation);
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
    private void navViewUpdateUiThread() {
        if (navViewUpdateUiRunnable == null) {
            navViewUpdateUiRunnable = () -> {
                ActualMeasuringData actualMeasuringData = ActualMeasuringData.getInstance();
                switch (navViewModel.getNavStatusView().getValue()) {
                    case MODE_NAV_COMP:
                        updateUiLiveData(
                                StringConverter.coordinateToString(actualMeasuringData.getDeviceCoordinateLatitudeDG(), actualMeasuringData.getDeviceCoordinateLongitudeDG(), coordinateFormat),
                                ActualMeasuringData.getInstance().getDeviceDirection()
                        );
                        break;
                    case MODE_NAV_BLE:
                        updateUiLiveData(
                                actualMeasuringData.getBleRssi(),
                                actualMeasuringData.getBleDistance(),
                                StringConverter.coordinateToString(actualMeasuringData.getDeviceCoordinateLatitudeDG(), actualMeasuringData.getDeviceCoordinateLongitudeDG(), coordinateFormat),
                                actualMeasuringData.getDeviceDirection(),
                                actualMeasuringData.getBleDirection()
                        );
                        break;
                    case MODE_NAV_UWB:
                        updateUiLiveData(
                                actualMeasuringData.getUwbRssi(),
                                actualMeasuringData.getUwbDistanceToa(),
                                StringConverter.coordinateToString(actualMeasuringData.getDeviceCoordinateLatitudeDG(), actualMeasuringData.getDeviceCoordinateLongitudeDG(), coordinateFormat),
                                actualMeasuringData.getDeviceDirection(),
                                actualMeasuringData.getUwbDirection()
                        );
                        break;
                    default:
                        throw new RuntimeException("Incorrect or no declarate value of Navigation MODE: at line #" + Thread.currentThread().getStackTrace()[2].getLineNumber());
                }
                handler.postDelayed(navViewUpdateUiRunnable, NAV_VIEW_UPDATE_UI_INTERVAL);
            };
            handler.postDelayed(navViewUpdateUiRunnable, NAV_VIEW_UPDATE_UI_INTERVAL);
        }
    }

    /**
     * This function update device information of Container
     *
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateContainerBLEVDate("Device Name", "Device ID")
     * @see android.content.SharedPreferences
     * @since <Android API 32 Platform></Android>
     */
    private void updateStatusNavView() {
        //TODO: shared Preference.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (sharedPreferences.getBoolean(getString(R.string.settings_nav_ble_enable_key), false)) {
            navViewModel.setNavStatusViewText(getString(R.string.fragmentNav_TextView_status_ble_active));
            navViewModel.setNavStatusView(MODE_NAV_BLE);
        } else if (sharedPreferences.getBoolean(getString(R.string.settings_nav_uwb_enable_key), false)) {
            navViewModel.setNavStatusViewText(getString(R.string.fragmentNav_TextView_status_uwb_active));
            navViewModel.setNavStatusView(MODE_NAV_UWB);
        } else {
            navViewModel.setNavStatusViewText(getString(R.string.fragmentNav_TextView_status_deactivated));
            navViewModel.setNavStatusView(MODE_NAV_COMP);
        }
    }

    /**
     * This function update device information of Container
     *
     * @param rssi                  Pass the RSSI value to be displayed.
     * @param distance              Pass the distance value to be displayed.
     * @param coordinate            Pass the coordinate to be displayed.
     * @param deviceDirection       Pass the alignment of the device in the z-axis.
     * @param trackerDirectionFromN Transfer the calculated angle to N-pole of the tracker to be ordered.
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateContainerBLEVDate(o.o)
     * @see android.content.SharedPreferences
     * @since <Android API 32 Platform></Android>
     */
    private void updateUiLiveData(double rssi, double distance, String coordinate, double deviceDirection, double trackerDirectionFromN) {
        navViewModel.setNavDegreePointer((float) ((trackerDirectionFromN + deviceDirection + 180/* "+180" because image shows down*/) % 360)); // Pointer
        navViewModel.setNavDegreeText(String.valueOf((int) trackerDirectionFromN % 360)); // Direction in °
        navViewModel.setNavRssiText(String.valueOf((int) rssi)); // RSSI
        navViewModel.setNavDistanceText(String.valueOf(distance)); // Distance
        navViewModel.setNavCoordinateText(coordinate); // Coordinate
    }

    /**
     * This function update device information of Container
     *
     * @param coordinate      safdaf
     * @param deviceDirection sdaffd
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: updateContainerBLEVDate("Device Name", "Device ID")
     * @see android.content.SharedPreferences
     * @since <Android API 32 Platform></Android>
     */
    private void updateUiLiveData(String coordinate, double deviceDirection) {
        navViewModel.setNavDegreePointer((float) ((deviceDirection + 180) % 360)); // Pointer
        navViewModel.setNavDegreeText(String.valueOf(((int) deviceDirection) % 360)); // Direction in °
        navViewModel.setNavRssiText(getString(R.string.str_placeholder_number_integer)); // RSSI
        navViewModel.setNavDistanceText(getString(R.string.str_placeholder_number_float)); // Distance
        navViewModel.setNavCoordinateText(coordinate); // Coordinate
    }
}