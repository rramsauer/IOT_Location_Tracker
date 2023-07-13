package com.rramsauer.iotlocatiotrackerapp.ui.preference;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import java.util.Set;

/**
 * A preference for selecting a Bluetooth device from a list of paired devices.
 *
 * @author Ramsauer René
 * @version V1.2
 */
public class BluetoothDevicePreference extends ListPreference {
    /**
     * Constructor for the BluetoothDevicePreference class.
     *
     * @param context The context of the preference.
     * @param attrs   The attribute set of the preference.
     * @author Ramsauer René
     */
    @SuppressLint("MissingPermission")
    public BluetoothDevicePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
            // If the adapter is not null, get the list of paired devices and set the entries and entry values of the preference.
            if (bta != null) {
                Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
                CharSequence[] entries = new CharSequence[pairedDevices.size()];
                CharSequence[] entryValues = new CharSequence[pairedDevices.size()];
                int i = 0;
                for (BluetoothDevice dev : pairedDevices) {
                    entries[i] = dev.getName();
                    entryValues[i] = dev.getAddress();
                    i++;
                }
                setEntries(entries);
                setEntryValues(entryValues);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Constructor for the BluetoothDevicePreference class.
     *
     * @param context The context of the preference.
     * @author Ramsauer René
     */
    public BluetoothDevicePreference(Context context) {
        this(context, null);
    }
}
