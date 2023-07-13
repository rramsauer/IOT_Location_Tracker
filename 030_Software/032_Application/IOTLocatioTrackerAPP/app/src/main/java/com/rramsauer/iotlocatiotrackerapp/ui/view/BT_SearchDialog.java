package com.rramsauer.iotlocatiotrackerapp.ui.view;
/* Android imports */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.rramsauer.iotlocatiotrackerapp.R;
import com.rramsauer.iotlocatiotrackerapp.ui.adapter.BT_DialogListAdapter;
import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class provides the UI for bluetooth devices search.
 * This class requires the following implementations:
 * - BT_DialogListAdapter.java
 * - custom_list_dialog.xml
 *
 * @author Ramsauer René
 * @version V1.6
 * @see BT_DialogListAdapter
 */
public class BT_SearchDialog extends DialogFragment {
    private static final String TAG = "BT_SearchDialog";
    /* Context */
    private Context context;
    /* Interface */
    private DialogAction l;
    /* View */
    private Dialog bluetoothSearchDialog;
    private TextView header;
    private TextView subHeader;
    private Button searchButton;
    private Button stopButton;
    private Button cancelButton;
    private ListView listView;
    private ProgressBar progressBar;
    private BT_DialogListAdapter dialogListAdapter;
    private HashSet<String> bluetoothDeviceAddressSet;
    private ArrayList<BluetoothDevice> bluetoothDevicesList;

    /**
     * Constructor class BT_SearchDialog
     *
     * @author Ramsauer René
     */
    public BT_SearchDialog(Context context, DialogAction l) {
        this.context = context;
        this.l = l;
    }

    /**
     * This Function Start the dialog and display it on screen.
     * The window is placed in the application layer and opaque.
     *
     * @author Ramsauer René
     * @version V1.6
     * @see android.app.Dialog
     */
    public void show() {
        bluetoothSearchDialog.show();
        Logger.d(TAG, "Dialog Search Bluetooth devices was called", Thread.currentThread().getStackTrace()[2]);
    }

    public void dismiss() {
        bluetoothSearchDialog.dismiss();
    }

    /**
     * This Function filter device and add it to the ListView.
     * This function adds only bluetooth devices to the list,
     * which have a device name and do not exist in the list.
     *
     * @author Ramsauer René
     * @version V0.2
     * @see android.app.Dialog
     */
    @SuppressLint("MissingPermission")
    public void addElementToList(BluetoothDevice device) {
        Log.d("SCANN_BLE_BLE", device.getAddress());
        if (device != null && device.getName() != null) {
            if (!bluetoothDeviceAddressSet.contains(device.getAddress())) {
                bluetoothDeviceAddressSet.add(device.getAddress());
                bluetoothDevicesList.add(device);
                dialogListAdapter.notifyDataSetChanged();
                Logger.v(TAG, "add Device" + device.getName() + " " + device.getAddress() + " to the list ", Thread.currentThread().getStackTrace()[2]);
            }
        }
    }

    /**
     * This Function clear all elements in the ListView.
     *
     * @author Ramsauer René
     * @version V0.1
     * @see android.app.Dialog
     */
    public void clearList() {
        bluetoothDeviceAddressSet.clear();
        bluetoothDevicesList.clear();
        dialogListAdapter.notifyDataSetChanged();
    }

    /**
     * This Function init the Dialog.
     *
     * @author Ramsauer René
     * @version V0.9
     * @see android.app.Dialog
     */
    public void initCustomDialog() {
        /* Call custom dialog view */
        bluetoothSearchDialog = new Dialog(context);
        bluetoothSearchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bluetoothSearchDialog.setCancelable(false);
        bluetoothSearchDialog.setContentView(R.layout.custom_list_dialog);
        /* Get elements of custom list dialog view */
        initTextView();
        initButton();
        initListView();
        initProgressBar();
    }

    /**
     * This Function init the TextViews in the Dialog.
     *
     * @author Ramsauer René
     * @version V0.1
     * @see android.app.Dialog
     */
    private void initTextView() {
        header = bluetoothSearchDialog.findViewById(R.id.list_dialog_header_key);
        subHeader = bluetoothSearchDialog.findViewById(R.id.list_dialog_sub_header_key);
        header.setText(R.string.list_dialog_ble_header);
        subHeader.setText(R.string.list_dialog_ble_sub_header);
    }

    /**
     * This Function init the Buttons in the Dialog.
     *
     * @author Ramsauer René
     * @version V0.1
     * @see android.app.Dialog
     */
    private void initButton() {
        /*  Hidden because actual not used */
        searchButton = bluetoothSearchDialog.findViewById(R.id.list_dialog_button_button_left_key);
        searchButton.setVisibility(View.INVISIBLE);
        searchButton.setText(R.string.list_dialog_ble_button_search);
        searchButton.setOnClickListener((v) -> {
            l.onClickStartScan();
        });
        /* Hidden because actual not used */
        stopButton = bluetoothSearchDialog.findViewById(R.id.list_dialog_button_button_center_key);
        stopButton.setVisibility(View.INVISIBLE);
        stopButton.setText(R.string.list_dialog_ble_button_stop);
        stopButton.setOnClickListener((v) -> {
            l.onClickStartScan();
        });
        /* Cancel dialog button */
        cancelButton = bluetoothSearchDialog.findViewById(R.id.list_dialog_button_button_right_key);
        cancelButton.setText(R.string.list_dialog_ble_button_cancel);
        cancelButton.setOnClickListener((v) -> {
            bluetoothSearchDialog.dismiss();
            l.onCanceled();
            Logger.v(TAG, "cancel bluetooth search dialog", Thread.currentThread().getStackTrace()[2]);
        });
    }

    /**
     * This Function init the ListView in the Dialog.
     *
     * @author Ramsauer René
     * @version V0.3
     * @see android.app.Dialog
     */
    private void initListView() {
        bluetoothDeviceAddressSet = new HashSet<>();
        bluetoothDevicesList = new ArrayList<>();
        listView = bluetoothSearchDialog.findViewById(R.id.list_dialog_list_key);
        dialogListAdapter = new BT_DialogListAdapter(context, bluetoothDevicesList);
        listView.setAdapter(dialogListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                bluetoothSearchDialog.dismiss();
                l.onClickItem(bluetoothDevicesList.get(position));
                Logger.i(TAG, "device: " + bluetoothDevicesList.get(position).getName() + " " + bluetoothDevicesList.get(position).getAddress() + " was selected.", Thread.currentThread().getStackTrace()[2]);
            }
        });
    }

    /**
     * This Function init ProgressBar()
     *
     * @author Ramsauer René
     * @version V0.3
     * @see android.app.Dialog
     */
    private void initProgressBar() {
        progressBar = bluetoothSearchDialog.findViewById(R.id.list_dialog_progress_bar_key);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Set ProgressBarVisible.
     *
     * @author Ramsauer René
     * @version V0.3
     * @see android.app.Dialog
     */
    public void setProgressBarVisible(int visibility) {
        progressBar.setVisibility(visibility);
    }

    /* Setter */
    public void setSearchButtonVisible(int visibility) {
        searchButton.setVisibility(visibility);
    }

    public void setStopButtonVisible(int visibility) {
        stopButton.setVisibility(visibility);
    }

    public void setCancelButtonEnable(boolean enabled) {
        cancelButton.setEnabled(enabled);
    }

    public void setSearchButtonEnable(boolean enabled) {
        searchButton.setEnabled(enabled);
    }

    public void setStopButtonEnable(boolean enabled) {
        stopButton.setEnabled(enabled);
    }

    /**
     * This interface provides user interactions on the UI.
     *
     * @author Ramsauer René
     * @version V0.1
     */
    public interface DialogAction {
        /**
         * Is called in the OnClickListener of the cancel button.
         */
        void onCanceled();

        /**
         * Is called in the OnClickListener of the bluetooth list and provides the selected item.
         */
        void onClickItem(BluetoothDevice device);

        /**
         * Is called in the OnClickListener of the bluetooth list and provides the selected item.
         */
        void onClickStartScan();
    }
}
