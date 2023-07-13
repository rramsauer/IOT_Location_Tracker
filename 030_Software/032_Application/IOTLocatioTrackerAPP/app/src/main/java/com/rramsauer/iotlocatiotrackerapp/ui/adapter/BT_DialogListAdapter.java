package com.rramsauer.iotlocatiotrackerapp.ui.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rramsauer.iotlocatiotrackerapp.R;

import java.util.List;

/**
 * Adapter class for Bluetooth devices list in a dialog
 *
 * @author Ramsauer René
 * @version V1.3
 * @see android.widget.BaseAdapter
 */
public class BT_DialogListAdapter extends BaseAdapter {

    private Context context;
    private List<BluetoothDevice> bluetoothDevices;

    /**
     * Constructor for creating a new instance of the adapter
     *
     * @param context          the context of the activity using the adapter
     * @param bluetoothDevices the list of Bluetooth devices to display
     * @author Ramsauer René
     */
    public BT_DialogListAdapter(Context context, List<BluetoothDevice> bluetoothDevices) {
        this.context = context;
        this.bluetoothDevices = bluetoothDevices;
    }

    /**
     * Returns the number of items in the list
     *
     * @return the number of items in the list
     * @author Ramsauer René
     */
    @Override
    public int getCount() {
        return bluetoothDevices.size();
    }

    /**
     * Returns the BluetoothDevice object at the specified position in the list
     *
     * @param position the position of the item in the list
     * @return the BluetoothDevice object at the specified position in the list
     * @author Ramsauer René
     */
    @Override
    public BluetoothDevice getItem(int position) {
        return bluetoothDevices.get(position);
    }

    /**
     * Returns the ID of the item at the specified position in the list
     *
     * @param i the position of the item in the list
     * @return the ID of the item at the specified position in the list
     * @author Ramsauer René
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Returns the view to display for the specified position in the list
     *
     * @param position the position of the item in the list
     * @param view     the view to be reused, if possible
     * @param parent   the parent view group that the view will be attached to
     * @return the view to display for the specified position in the list
     * @author Ramsauer René
     * @SuppressLint("MissingPermission") This annotation suppresses the warning
     * for not having declared the Bluetooth permission in the manifest, because
     * the permission is checked before calling this method.
     */
    @SuppressLint("MissingPermission")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Inflate the layout for each item in the list
        View bluetoothDeviceItemView = LayoutInflater.from(context).inflate(R.layout.scan_bluetooth_item, parent, false);

        // Get references to the text views in the layout
        TextView bluetoothNameTextView = bluetoothDeviceItemView.findViewById(R.id.list_dialog_element_header);
        TextView bluetoothAddressTextView = bluetoothDeviceItemView.findViewById(R.id.list_dialog_element_sub_header);

        // Get the BluetoothDevice object at the specified position in the list
        BluetoothDevice bluetoothDevice = this.getItem(position);

        // Set the name and address of the Bluetooth device in the text views
        bluetoothNameTextView.setText(bluetoothDevice.getName());
        bluetoothAddressTextView.setText(bluetoothDevice.getAddress());

        return bluetoothDeviceItemView;
    }
}
