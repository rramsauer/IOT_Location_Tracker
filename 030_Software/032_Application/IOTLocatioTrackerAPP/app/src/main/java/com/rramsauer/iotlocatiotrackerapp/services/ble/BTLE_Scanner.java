package com.rramsauer.iotlocatiotrackerapp.services.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;

import com.rramsauer.iotlocatiotrackerapp.MainActivity;
import com.rramsauer.iotlocatiotrackerapp.R;
import com.rramsauer.iotlocatiotrackerapp.ui.view.BT_SearchDialog;
import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;
import com.rramsauer.iotlocatiotrackerapp.util.settings.SharedPreferenceHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class provides an bluetooth low energy scan with ui.
 * The logic of the ui is implemented in the class BT_SearchDialog.
 * This class sets the choosen device address directly in the SharedPreference.
 * When you call this function start a popup appears which shows the scanned device to the user.
 * In this popup the user can then call the desired device.
 * The implementation of the ui logic is implied in the BT_ScannerDialog.
 *
 * @author Ramsauer René
 * @version V4
 * @implNote To implement this class, you just need to initiate the class with the constructor and
 * then call the start() function when you want to start scanning
 * and stop() when you want to stop scanning.
 * For example:
 * - BTLE_Scanner btle_scanner = new BTLE_Scanner(this,bluetoothAdapter,BTLE_Scanner.SCAN_PERIOD_LONG);
 * - btle_scanner.start()
 * - btle_scanner.stop();
 * <p>
 * Information: The scan filter wos define in the scanning class.
 * @see android.bluetooth.le.BluetoothLeScanner
 * @see com.rramsauer.iotlocatiotrackerapp.ui.view.BT_SearchDialog;
 */
public class BTLE_Scanner extends AppCompatActivity implements BT_SearchDialog.DialogAction {
    public static final int SCAN_PERIOD_LONG = 10000;
    public static final int SCAN_PERIOD_NORMAL = 5000;
    public static final int SCAN_PERIOD_SHORT = 2000;
    /* LOG-Tag */
    private static final String TAG = "BTLE_Scanner";
    private final int scanDuration;
    /* Define Class Variables */
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private BluetoothLeScanner bluetoothLeScanner;
    private BT_SearchDialog bt_searchDialog;
    /**
     * A ScanCallback implementation that listens for BLE (Bluetooth Low Energy)
     * scan results. This implementation adds discovered Bluetooth devices to the
     * list displayed by the bt_searchDialog object and logs the transmit power of
     * the device.
     *
     * @author Ramsauer René
     */
    private final ScanCallback scanCallback = new ScanCallback() {

        /**
         * This method is called whenever a BLE scan result is received.
         * @param callbackType The type of callback. Not used in this implementation.
         * @param result The scan result containing the discovered device and signal
         */
        @SuppressLint("NewApi")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice bluetoothDevice = result.getDevice();
            bt_searchDialog.addElementToList(bluetoothDevice);
            Logger.i(TAG, "txpower" + String.valueOf(result.getTxPower()));
        }

        /**
         * This method is called when a batch of scan results are received.
         * Not used in this implementation.
         * @param results A list of scan results.
         */
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        /**
         * This method is called if a BLE scan fails.
         * Not used in this implementation.
         * @param errorCode The error code indicating why the scan failed.
         */
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "Scanning Failed " + errorCode);
            bt_searchDialog.setProgressBarVisible(View.INVISIBLE);
        }
    };

    /**
     * CONSTRUCTOR
     * initialization of BTLE_Scanner.
     *
     * @param context          The context to use. Usually your android.app.Application or android.app.
     * @param bluetoothAdapter Pass an BluetoothAdapter.
     * @param scanDuration     Define the scan duration.
     * @author Ramsauer René
     */
    public BTLE_Scanner(Context context, BluetoothAdapter bluetoothAdapter, @BluetoothLEScannerPeriod final int scanDuration) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.scanDuration = scanDuration;
        bt_searchDialog = new BT_SearchDialog(context, this);
        bt_searchDialog.initCustomDialog();
        bt_searchDialog.setSearchButtonVisible(View.VISIBLE);
        bt_searchDialog.setSearchButtonEnable(false);
        bt_searchDialog.setCancelButtonEnable(false);
    }

    /**
     * This function start the scanning process
     *
     * @author Ramsauer René
     * @implNote Ex.: start()
     */
    public void start() {
        if (bluetoothAdapter.isEnabled()) {
            bt_searchDialog.show();
            scanning(scanDuration);
        }
    }

    /**
     * This function stop the scanning process
     *
     * @author Ramsauer René
     * @implNote Ex.: stop()
     * @SuppressLint("MissingPermission") This annotation suppresses the warning
     * for not having declared the Bluetooth permission in the manifest, because
     * the permission is checked before calling this method.
     */
    @SuppressLint("MissingPermission")
    public void stop() {
        bluetoothLeScanner.stopScan(scanCallback);
        bt_searchDialog.setSearchButtonEnable(false);
        bt_searchDialog.dismiss();
    }

    /**
     * This function provides the logic of the scanning process
     *
     * @param scanPeriod Pass the scan period
     * @author Ramsauer René
     * @implNote Ex.: scanning(SCAN_PERIOD_LONG)
     * @SuppressLint("MissingPermission") This annotation suppresses the warning
     * for not having declared the Bluetooth permission in the manifest, because
     * the permission is checked before calling this method.
     */
    @SuppressLint("MissingPermission")
    private void scanning(@BluetoothLEScannerPeriod final int scanPeriod) {
        bt_searchDialog.clearList();
        for (BluetoothDevice bluetoothDevice : bluetoothAdapter.getBondedDevices()) {
            bt_searchDialog.addElementToList(bluetoothDevice);
        }
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        Handler mHandler = new Handler();
        final ScanSettings settings = new ScanSettings.Builder().build();
        List<ScanFilter> scanFilters = new ArrayList<>();
        for (Map.Entry<String, String> entry : MainActivity.getBtleGattAttributes().getServiceHashMap().entrySet()) {
            scanFilters.add(new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(entry.getKey())).build());
        }
        mHandler.postDelayed(() -> {
            bluetoothLeScanner.stopScan(scanCallback);
            bt_searchDialog.setSearchButtonEnable(true);
            bt_searchDialog.setCancelButtonEnable(true);
            bt_searchDialog.setProgressBarVisible(View.INVISIBLE);
        }, (long) scanPeriod);
        bluetoothLeScanner.startScan(scanFilters, settings, scanCallback);
        bt_searchDialog.setSearchButtonEnable(false);
        bt_searchDialog.setCancelButtonEnable(false);
        bt_searchDialog.setProgressBarVisible(View.VISIBLE);
    }

    /**
     * This function override onCanceled()
     *
     * @author Ramsauer René
     * @override onCanceled in class BT_SearchDialog
     * @SuppressLint("MissingPermission") This annotation suppresses the warning
     * for not having declared the Bluetooth permission in the manifest, because
     * the permission is checked before calling this method.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onCanceled() {
        bluetoothLeScanner.stopScan(scanCallback);
        SharedPreferenceHelper.changeStringValueSharedPreference(context, R.string.settings_ble_dialog_choose_device_key, context.getString(R.string.settings_ble_list_preference_no_device_connected));
    }

    /**
     * This function override onClickItem()
     *
     * @author Ramsauer René
     * @override onClickItem in class BT_SearchDialog
     * @SuppressLint("MissingPermission") This annotation suppresses the warning
     * for not having declared the Bluetooth permission in the manifest, because
     * the permission is checked before calling this method.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onClickItem(BluetoothDevice device) {
        bluetoothLeScanner.stopScan(scanCallback);
        SharedPreferenceHelper.changeStringValueSharedPreference(context, R.string.settings_ble_dialog_choose_device_key, device.getAddress());
    }

    /**
     * This function override onClickStartScan()
     *
     * @author Ramsauer René
     * @override onClickStartScan in class BT_SearchDialog
     */
    @Override
    public void onClickStartScan() {
        scanning(scanDuration);
    }

    /**
     * Denotes that an integer, field or method return value is expected to be an BluetoothLEScannerPeriod typ reference.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SCAN_PERIOD_LONG, SCAN_PERIOD_NORMAL, SCAN_PERIOD_SHORT})
    public @interface BluetoothLEScannerPeriod {
    }
}
