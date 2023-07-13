package com.rramsauer.iotlocatiotrackerapp.services.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.rramsauer.iotlocatiotrackerapp.R;
import com.rramsauer.iotlocatiotrackerapp.util.notification.CustomUserInformation;
import com.rramsauer.iotlocatiotrackerapp.util.settings.SharedPreferenceHelper;

/**
 * A BroadcastReceiver implementation that listens for changes in the state
 * of the Bluetooth adapter. This implementation receives the state changes and
 * updates the relevant shared preferences and displays a snackbar message to
 * the user indicating whether Bluetooth is turned on or off.
 * <p>
 * Information: This class bass on an example implementation of gitHup example
 * and Android https://developer.android.com/.
 */
public class BT_StateBroadcastReceiver extends BroadcastReceiver {

    Context context;
    View view;

    /**
     * Constructor for the BT_StateBroadcastReceiver class.
     *
     * @param context The context of the application.
     * @param view    The view in which to display the snackbar message.
     */
    public BT_StateBroadcastReceiver(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    /**
     * This method is called whenever a Bluetooth state change is detected.
     *
     * @param context The context of the application.
     * @param intent  The intent containing the Bluetooth state change information.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    SharedPreferenceHelper.changeBooleanValueSharedPreference(context, R.string.settings_ble_enable_bluetooth_key, false);
                    CustomUserInformation.showSnackbarError(view, "Bluetooth is off", Snackbar.LENGTH_SHORT, Gravity.BOTTOM, true);
                    break;
                case BluetoothAdapter.STATE_ON:
                    SharedPreferenceHelper.changeBooleanValueSharedPreference(context, R.string.settings_ble_enable_bluetooth_key, true);
                    CustomUserInformation.showSnackbarSucess(view, "Bluetooth is on", Snackbar.LENGTH_SHORT, Gravity.BOTTOM, true);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                case BluetoothAdapter.STATE_TURNING_ON:
                    /* Currently not needed. */
                    break;
                default:
                    break;
            }
        }
    }
}
