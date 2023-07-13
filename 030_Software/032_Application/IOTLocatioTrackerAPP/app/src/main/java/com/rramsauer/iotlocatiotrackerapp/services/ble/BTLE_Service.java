package com.rramsauer.iotlocatiotrackerapp.services.ble;

import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.rramsauer.iotlocatiotrackerapp.MainActivity;
import com.rramsauer.iotlocatiotrackerapp.util.calc.distance.CalculatorDistanceRSSI;
import com.rramsauer.iotlocatiotrackerapp.util.calc.filter.AverageFilter;
import com.rramsauer.iotlocatiotrackerapp.util.calc.filter.KalmanFilter;
import com.rramsauer.iotlocatiotrackerapp.util.geo.TrackerDirectionCalculator;
import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;
import com.rramsauer.iotlocatiotrackerapp.util.str.StringFormatCheck;

import java.util.List;
import java.util.UUID;


/**
 * This class provides a service to manage the connection and data
 * communication with a GATT server located on a specific Bluetooth LE device.
 * As well as a RSSI listerner for capturing the RSSI values of the connected device.
 * This service has been implemented to provide communication with bluetooth tracker.
 * <p>
 * This class is based on the implementation
 * https://github.com/artikcloud/sample-android-ble/blob/master/app/src/main/java/cloud/artik/example/ble/BluetoothLeService.java.
 * Since a complete service is implemented in the above link, no further references are given in the methods.
 *
 * @author Ramsauer René
 * @version V6.1
 * @implNote Here, only the particularities of the implementation are mentioned.
 * 1. Add the follower permission to the AndroidManifest.xml:
 * <uses-permission android:name="android.permission.BLUETOOTH" />
 * <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 * <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
 * 2. Add the follower service to the AndroidManifest.xml:
 * <service
 * android:name=".services.ble.BTLE_Service"
 * android:enabled="true"
 * android:exported="true" />
 * 3. The following implementations must also be carried out:
 * private final ServiceConnection serviceConnection = new ServiceConnection()
 * private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
 * @Override public void onReceive(Context context, Intent intent) {
 * final String action = intent.getAction();
 * if (BTLE_Service.ACTION_GATT_CONNECTED.equals(action)) {
 * } else if (BTLE_Service.ACTION_GATT_DISCONNECTED.equals(action)) {
 * } else if (BTLE_Service.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
 * } else if (BTLE_Service.ACTION_DATA_AVAILABLE.equals(action)) {
 * }
 * }
 * };
 * 4. Add your gatt Services and characteristics in the btle_supported_gatt_attributes.
 * @see android.app.Service
 * @see android.bluetooth
 */
public class BTLE_Service extends Service {

    /* Define Intent identifier gatt */
    public final static String ACTION_GATT_CONNECTED = "com.rramsauer.iotlocatiotrackerapp.services.ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.rramsauer.iotlocatiotrackerapp.services.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.rramsauer.iotlocatiotrackerapp.services.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.rramsauer.iotlocatiotrackerapp.services.ble.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA_UWB_SHORT_ADR = "com.rramsauer.iotlocatiotrackerapp.services.ble.EXTRA_DATA_UWB_SHORT_ADR";
    public final static String EXTRA_DATA_UWB_UID = "com.rramsauer.iotlocatiotrackerapp.services.ble.EXTRA_DATA_UWB_UID";
    /* Define UUID */
    public final static UUID UUID_UWB_UID_CHARACTERISTIC = UUID.fromString(MainActivity.getBtleGattAttributes().getString("gatt_ultra_wide_band_identifier_characteristic"));
    public final static UUID UUID_UWB_ADDRESS_SHORT_CHARACTERISTIC = UUID.fromString(MainActivity.getBtleGattAttributes().getString("gatt_ultra_wide_band_short_address"));
    public final static int RSSI_MEASURING_INTERVAL = 100;
    /* LOG TAG */
    private static final String TAG = "BTLE_Service";
    private static final int STATE_DISCONNECT = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    /* Interface for a remote object */
    private final IBinder binder = new LocalBinder();
    private final Handler readRssiHandler = new Handler();
    /* Connected Bluetooth LE Device*/
    BluetoothDevice connectedBluetoothDevice;
    /* RSSI Measuring */
    public static final double BLE_MEASURED_TX_POWER = -60;
    public static final double BLE_PROPAGATION_EXPONENT = 2.5;
    RemoteRssiListener l;
    KalmanFilter kalmanFilter1 = new KalmanFilter(0.125d, 0.50d);
    AverageFilter averageFilter = new AverageFilter(2, -60);
    TrackerDirectionCalculator trackerDirectionCalculator = new TrackerDirectionCalculator(3);
    /* Bluetooth Device Address*/
    private String bluetoothDeviceAddress;
    /* Bluetooth Adapter */
    private BluetoothAdapter bluetoothAdapter;
    /* Bluetooth Manager */
    private BluetoothManager bluetoothManager;
    /* Bluetooth Gatt */
    private BluetoothGatt bluetoothGatt;
    /* State */
    private int connectionState = STATE_DISCONNECT;
    private int measuringInterval;
    private Runnable rssiThread;
    /**
     * Init Bluetooth Gatt Callback
     * Implements callback methods for significant GATT events.
     *
     * @author Ramsauer René
     */
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        /**
         * This function is called when the connection state is change.
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Logger.d(TAG, "onConnectionStateChange " + newState);
            String intentAction;
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    intentAction = ACTION_GATT_CONNECTED;
                    connectionState = STATE_CONNECTED;
                    broadcastUpdate(intentAction);
                    Logger.i(TAG, "Connected to GATT server.");
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                        Logger.w(TAG, "WARNING: Miss permission -> BLUETOOTH_ADMIN ", Thread.currentThread().getStackTrace()[2]);
                        return;
                    }
                    Logger.i(TAG, "Attempting to start service discovery:" + bluetoothGatt.discoverServices());
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    intentAction = ACTION_GATT_DISCONNECTED;
                    connectionState = STATE_DISCONNECTED;
                    Logger.i(TAG, "Disconnected from GATT server.");
                    broadcastUpdate(intentAction);
                    break;
            }
        }

        /**
         * This function is called when the service is discovered
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Logger.d(TAG, "onServicesDiscovered " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Logger.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        /**
         * Callback reporting the result of a characteristic read operation.
         * Call: readCharacteristic(gattCharacteristic)
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Logger.v(TAG, "onCharacteristicRead Characteristic: " + characteristic.getUuid() + " Status: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        /**
         * Callback indicating the result of a characteristic write operation.
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Logger.d(TAG, "onCharacteristicWrite Characteristic: " + characteristic.getUuid() + " Status: " + status);
        }

        /**
         * Callback triggered as a result of a remote characteristic notification.
         * This method was deprecated in API level 33. Use
         * BluetoothGattCallback#onCharacteristicChanged(BluetoothGatt,
         * BluetoothGattCharacteristic, byte[]) as it is memory safe by
         * providing the characteristic value at the time of notification.
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Logger.d(TAG, "onCharacteristicChanged");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        /**
         * This method was deprecated in API level 33. Use
         * BluetoothGattCallback#onDescriptorRead(BluetoothGatt, BluetoothGattDescriptor,
         * int, byte[]) as it is memory safe by providing the descriptor
         * value at the time it was read.
         */
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Logger.d(TAG, "onDescriptorRead Descriptor: " + descriptor.getUuid() + " Status: " + status);
        }

        /**
         * Callback indicating the result of a descriptor write operation.
         */
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Logger.d(TAG, "onDescriptorWrite Descriptor: " + descriptor.getUuid() + " Status: " + status);

        }

        /**
         * This function override onReliableWriteCompleted()
         */
        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Logger.d(TAG, "onReliableWriteCompleted " + status);
        }

        /**
         * Callback reporting the RSSI for a remote device connection.
         */
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (l != null) {
                double rssiFiltered1 = kalmanFilter1.applyFilter(rssi);
                double rssiFiltered2 = averageFilter.addRSSI(rssiFiltered1);
                l.onNewRssi(gatt, rssi,
                        rssiFiltered2,
                        CalculatorDistanceRSSI.calculateDistance(
                                BLE_MEASURED_TX_POWER,
                                rssiFiltered2,
                                BLE_PROPAGATION_EXPONENT
                        ),
                        status
                );
            }
        }

        /**
         * Callback indicating the MTU for a given device connection has changed.
         */
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Logger.d(TAG, "onMtuChanged MTU: " + mtu + " Status: " + status);
        }

    };

    /**
     * Returns the BluetoothAdapter instance for the device. This method retrieves
     * the BluetoothManager instance from the system service and returns its adapter.
     *
     * @param context The context of the application.
     * @return The BluetoothAdapter instance for the device.
     */
    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        BluetoothManager mBluetoothManager = (BluetoothManager) context.getSystemService(context.BLUETOOTH_SERVICE);
        return mBluetoothManager.getAdapter();
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt == null) return null;
        return bluetoothGatt.getServices();
    }

    /**
     * This Function manage the Update of specify gat characteristics.
     * Should you implement a new characteristic you will edit this method.
     *
     * @param action         Pass the intent action string.
     * @param characteristic Pass the BluetoothGattCharacteristic
     */
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        /* UWB-Device short address */
        if (UUID_UWB_UID_CHARACTERISTIC.equals(characteristic.getUuid())) {
            final String receivedUID = characteristic.getStringValue(0);
            Logger.i(TAG, "broadcastUpdate() ->" + receivedUID);
            intent.putExtra(EXTRA_DATA_UWB_UID, receivedUID);
        }
        if (UUID_UWB_ADDRESS_SHORT_CHARACTERISTIC.equals(characteristic.getUuid())) {
            final String receivedShortAddress = characteristic.getStringValue(0);
            Logger.i(TAG, "broadcastUpdate() ->" + receivedShortAddress);
            intent.putExtra(EXTRA_DATA_UWB_SHORT_ADR, receivedShortAddress);
        }
        sendBroadcast(intent);
    }

    /**
     * This Function create an new Intent with the intent string and
     * send the broadcast.
     *
     * @param action Pass the intent action string.
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * Returns an IBinder instance that allows communication between components
     * of the Android application.
     *
     * @param intent The Intent passed to bindService(Intent, ServiceConnection, int).
     * @return An IBinder instance that can be used to communicate with the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Unbind the Service
     *
     * @param intent The Intent that was used to bind to this service.
     * @return boolean Return true if you would like to have the service's
     */
    @Override
    public boolean onUnbind(Intent intent) {
        // if the user interface is disconnected from the service close
        close();
        return super.onUnbind(intent);
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            Logger.w(TAG, "WARNING: Miss permission -> BLUETOOTH_ADMIN ", Thread.currentThread().getStackTrace()[2]);
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Logger.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            Logger.w(TAG, "WARNING: Miss permission -> BLUETOOTH_ADMIN ", Thread.currentThread().getStackTrace()[2]);
            return;
        }
        bluetoothGatt.disconnect();
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        bluetoothAdapter = getBluetoothAdapter(this);

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     * @SuppressLint("MissingPermission") This annotation suppresses the warning
     * for not having declared the Bluetooth permission in the manifest, because
     * the permission is checked before calling this method.
     */
    @SuppressLint("MissingPermission")
    public boolean connect(final String address) {
        if (StringFormatCheck.isBluetoothAddress(address)) {
            if (bluetoothAdapter == null || address == null) {
                Logger.w(TAG, "BluetoothAdapter not initialized or unspecified address.", Thread.currentThread().getStackTrace()[2]);
                return false;
            }
            /* Try to reconnect. */
            if (bluetoothDeviceAddress != null && address.equals(bluetoothDeviceAddress) && bluetoothGatt != null) {
                Logger.d(TAG, "Trying to use an existing bluetoothGatt for connection.", Thread.currentThread().getStackTrace()[2]);
                if (bluetoothGatt.connect()) {
                    connectionState = STATE_CONNECTING;
                    return true;
                } else {
                    return false;
                }
            }
            connectedBluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            if (connectedBluetoothDevice == null) {
                Logger.w(TAG, "Device not found.", Thread.currentThread().getStackTrace()[2]);
                return false;
            }
            // We want to directly connect to the device, so we are setting the autoConnect
            // parameter to false.
            bluetoothGatt = connectedBluetoothDevice.connectGatt(this, false, bluetoothGattCallback);
            Logger.d(TAG, "Trying to create a new connection.", Thread.currentThread().getStackTrace()[2]);
            bluetoothDeviceAddress = address;
            connectionState = STATE_CONNECTING;
        }
        return true;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     * @SuppressLint("MissingPermission") This annotation suppresses the warning
     * for not having declared the Bluetooth permission in the manifest, because
     * the permission is checked before calling this method.
     */
    @SuppressLint("MissingPermission")
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Logger.v(TAG, "BluetoothAdapter not initialized");
        } else {
            Logger.v(TAG, "Read bluetooth gatt characteristic");
            bluetoothGatt.readCharacteristic(characteristic);
        }

    }

    /**
     * Enables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     * @SuppressLint("MissingPermission") This annotation suppresses the warning
     * for not having declared the Bluetooth permission in the manifest, because
     * the permission is checked before calling this method.
     */
    @SuppressLint("MissingPermission")
    public void setCharacteristicNotification(@NonNull BluetoothGattCharacteristic characteristic, boolean enabled) {
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    /**
     * Reads the RSSI (Received Signal Strength Indication) value of the connected
     * Bluetooth device. If there is no connected device or GATT server, this method
     * does nothing.
     *
     * @SuppressLint("MissingPermission") This annotation suppresses the warning
     * for not having declared the Bluetooth permission in the manifest, because
     * the permission is checked before calling this method.
     */
    @SuppressLint("MissingPermission")
    public void readRssi() {
        if (connectedBluetoothDevice != null && bluetoothGatt != null) {
            bluetoothGatt.readRemoteRssi();
        }
    }

    /**
     * Starts listening for changes in the RSSI (Received Signal Strength Indication)
     * value of the connected Bluetooth device. The listener passed as parameter will
     * be called whenever a new RSSI value is received, with the value as parameter.
     * The measuring interval parameter determines how often the RSSI value is read.
     *
     * @param l                 The listener that will be called with the new RSSI value.
     * @param measuringInterval The interval (in milliseconds) between RSSI readings.
     */
    public void startRssiListener(RemoteRssiListener l, int measuringInterval) {
        this.measuringInterval = measuringInterval;
        this.l = l;
        if (rssiThread == null) {
            rssiThread = () -> {
                readRssi();
                readRssiHandler.postDelayed(rssiThread, RSSI_MEASURING_INTERVAL);
            };
            readRssiHandler.postDelayed(rssiThread, RSSI_MEASURING_INTERVAL);
        }
    }

    /**
     * Stops listening for changes in the RSSI (Received Signal Strength Indication)
     * value of the connected Bluetooth device. The RSSI readings will no longer be
     * sent to the listener.
     */
    public void stopRssiListener() {
        readRssiHandler.removeCallbacks(rssiThread);
        rssiThread = null;
    }

    /**
     * Returns the Bluetooth device to which this service is connected.
     *
     * @return the BluetoothDevice object representing the connected device, or null if not connected
     */
    public BluetoothDevice getConnectedBluetoothDevice() {
        return connectedBluetoothDevice;
    }

    /**
     * This interface provides a RemotRssiListener.
     * Which enables the determination of the current Rssi.
     * The interval of the data collection was defined with RSSI_MEASURING_INTERVAL.
     * This interface is initialized with the function startRssiListener.
     *
     * @author Ramsauer René
     */
    public interface RemoteRssiListener {
        /**
         * is getting called when the compass was started.
         */
        void onNewRssi(BluetoothGatt gatt, double rssi, double rssiFiltered, double distance, int status);
    }

    /**
     * An extension of the Binder class that provides a reference to the
     * enclosing BTLE_Service instance. This class is used to provide clients
     * with access to the service's public methods.
     */
    public class LocalBinder extends Binder {
        /**
         * Returns a reference to the BTLE_Service instance that created this
         * LocalBinder object. This method is typically used by clients to access
         * the public methods provided by the BTLE_Service class.
         *
         * @return The BTLE_Service instance that created this LocalBinder object.
         */
        public BTLE_Service getService() {
            return BTLE_Service.this;
        }
    }
}