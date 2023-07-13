package com.rramsauer.iotlocatiotrackerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.rramsauer.iotlocatiotrackerapp.databinding.ActivityMainBinding;
import com.rramsauer.iotlocatiotrackerapp.environment.AppPermission;
import com.rramsauer.iotlocatiotrackerapp.environment.AppPermissionsManager;
import com.rramsauer.iotlocatiotrackerapp.services.ble.BTLE_GattAttributes;
import com.rramsauer.iotlocatiotrackerapp.services.ble.BTLE_Scanner;
import com.rramsauer.iotlocatiotrackerapp.services.ble.BTLE_Service;
import com.rramsauer.iotlocatiotrackerapp.services.ble.BT_StateBroadcastReceiver;
import com.rramsauer.iotlocatiotrackerapp.services.usb.UsbSerialReader;
import com.rramsauer.iotlocatiotrackerapp.storage.csv.CsvMeasuringData;
import com.rramsauer.iotlocatiotrackerapp.storage.state.ActualMeasuringData;
import com.rramsauer.iotlocatiotrackerapp.util.compassAssistant.CompassAssistant;
import com.rramsauer.iotlocatiotrackerapp.util.geo.TrackerDirectionCalculator;
import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;
import com.rramsauer.iotlocatiotrackerapp.util.notification.CustomUserInformation;
import com.rramsauer.iotlocatiotrackerapp.util.settings.SharedPreferenceHelper;
import com.rramsauer.iotlocatiotrackerapp.util.storage.CsvLogger;
import com.rramsauer.iotlocatiotrackerapp.util.storage.FileManagementHelper;
import com.rramsauer.iotlocatiotrackerapp.util.str.StringFormatCheck;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The MainActivity class is the main activity of the application. It extends the AppCompatActivity
 * class and implements several interfaces including CompassAssistant.CompassAssistantListener,
 * AppPermissionsManager.OnPermissionCage, CsvLogger.CsvLoggerWriter<CsvMeasuringData>,
 * and BTLE_Service.RemoteRssiListener.
 * <p>
 * The class contains variables for the activity, permissions, storage, compass, current location,
 * and Bluetooth Low Energy (BLE) communication.
 * <p>
 * The locationListener inner class implements the LocationListener interface to monitor location
 * changes. When a location change is detected, this class updates the device's location data with
 * the new coordinates and updates the ActualMeasuringData instance with this new data.
 * <p>
 * The MainActivity class also includes a Runnable for location updates,
 * a Handler for location updates, and a Criteria object for location updates.
 * <p>
 * The BLE variables in the class include a Bluetooth adapter, state broadcast receiver,
 * scanner, GATT attributes, and a BLE service connection.
 * <p>
 * And many more.
 * <p>
 * Overall, the MainActivity class is responsible for managing the the various components
 * of the application.
 *
 * @author Ramsauer René
 * @version 2.5
 */
public class MainActivity
        /* Extends */
        extends AppCompatActivity
        /* Implements */
        implements
        CompassAssistant.CompassAssistantListener,
        AppPermissionsManager.OnPermissionCage,
        CsvLogger.CsvLoggerWriter<CsvMeasuringData>,
        BTLE_Service.RemoteRssiListener {
    /* Variable for STORAGE */
    public static final File APP_STORAGE_ROOT_DIR = new File(Environment.getExternalStorageDirectory().getPath(), "/Iot Location Tracker APP");
    /* Variable for CURRENT LOCATION */
    final static int LOCATION_UPDATE_INTERVAL = 200;
    /* LOG TAG */
    private static final String TAG = "MainActivity";
    public static int USB_VENDOR_ID_ESP32 = 4292;
    private static BTLE_GattAttributes btle_gattAttributes;
    private final Handler locationHandler = new Handler();
    LocationManager locationManager;
    TrackerDirectionCalculator bleTrackerDirectionCalculator = new TrackerDirectionCalculator(3);
    /* Variable for Activity */
    private ActivityMainBinding binding;
    /* Variable for Permission */
    private AppPermissionsManager appPermissions;
    private boolean permissionBLE = false;
    private CsvLogger<CsvMeasuringData> csvLogger;
    private boolean isCsvLoggerRun;
    /* Variable for COMPASS */
    private CompassAssistant compassAssistant;
    private float degreeBefore = 0;
    private float currentDegree = 0;
    /**
     * This class implements a LocationListener interface to monitor location changes.
     * When a location change is detected, this class updates the device's location data
     * with the new coordinates and updates the ActualMeasuringData instance with this new data.
     *
     * @author Ramsauer René
     */
    private LocationListener locationListener = new LocationListener() {
        /**
         * Called when the location of the device has changed.
         * This method updates the device's coordinates and location in the ActualMeasuringData instance.
         * @param location The new location of the device.
         */
        @Override
        public void onLocationChanged(Location location) {
            ActualMeasuringData.getInstance().setDeviceCoordinateLongitudeDG(location.getLongitude());
            ActualMeasuringData.getInstance().setDeviceCoordinateLatitudeDG(location.getLatitude());
            ActualMeasuringData.getInstance().setDeviceLocation(location);
        }

        /**
         * Called when the provider status changes.
         * @param provider The name of the location provider associated with this update.
         * @param status The status constant indicating the new status of the provider.
         * @param extras An optional Bundle containing additional provider-specific status updates.
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        /**
         * Called when the provider is enabled.
         * @param provider The name of the location provider associated with this update.
         */
        @Override
        public void onProviderEnabled(String provider) {
        }

        /**
         * Called when the provider is disabled.
         * @param provider The name of the location provider associated with this update.
         */
        @Override
        public void onProviderDisabled(String provider) {
        }
    };
    private Runnable locationUpdateRunnable;
    private Criteria criteria = new Criteria();
    /* Variable for BLE */
    private BluetoothAdapter bluetoothAdapter;
    /* Variable for BLE State */
    private BT_StateBroadcastReceiver bt_stateBroadcastReceiver;
    /* Variable for BLE Scanner */
    private BTLE_Scanner btle_scanner;
    private String btleDeviceAddress;
    /* Variable for BLE Service */
    private boolean isConnectedWithBleDevice = false;
    private BluetoothGattCharacteristic notifyCharacteristicUwbShortAddress;
    private BluetoothGattCharacteristic notifyCharacteristicUwbUid;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private ArrayList<BluetoothGattCharacteristic> characteristicTest = new ArrayList<>();
    private BTLE_Service btle_service;
    /**
     * This class implements a ServiceConnection interface to connect to a Bluetooth Low Energy (BLE) service.
     * When the service is connected, this class initializes the BLE service and automatically connects to the device
     * using the device name from shared preferences.
     *
     * @author Ramsauer René
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        /**
         * Called when the service is connected.
         * This method initializes the BLE service and connects to the device.
         * @param componentName The name of the component that was connected.
         * @param service The IBinder of the service that was connected.
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            btle_service = ((BTLE_Service.LocalBinder) service).getService();
            if (!btle_service.initialize()) {
                Logger.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            btle_service.connect(SharedPreferenceHelper.getStringValueOfSharedPreference(getApplicationContext(), R.string.settings_ble_dialog_choose_device_key));
        }

        /**
         * Called when the service is disconnected.
         * This method sets the btle_service to null.
         * @param componentName The name of the component that was disconnected.
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            btle_service = null;
        }
    };
    /* Variable for USB */
    private UsbSerialReader usbSerialReader;
    /**
     * This class implements a BroadcastReceiver to receive updates from the BTLE service.
     * The onReceive() method is called when an update is received.
     * The method checks the action received, and performs the appropriate action.
     *
     * @author Ramsauer René
     */
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BTLE_Service.ACTION_GATT_CONNECTED.equals(action)) {
                Logger.w(TAG, "BluetoothDevice connected ");
                invalidateOptionsMenu();
                btle_service.startRssiListener(MainActivity.this, 100);
                ActualMeasuringData.getInstance().setBleDeviceIsConnected(true);
                ActualMeasuringData.getInstance().setBleDeviceName(btle_service.getConnectedBluetoothDevice().getName());
                ActualMeasuringData.getInstance().setBleDeviceId(btle_service.getConnectedBluetoothDevice().getAddress());
            } else if (BTLE_Service.ACTION_GATT_DISCONNECTED.equals(action)) {
                ActualMeasuringData.getInstance().setBleDeviceIsConnected(false);
                btle_service.stopRssiListener();
                Logger.w(TAG, "BluetoothDevice disconnected");
                ActualMeasuringData.getInstance().setBleDeviceName(getString(R.string.settings_ble_list_preference_no_device_connected));
                ActualMeasuringData.getInstance().setBleDeviceId(getString(R.string.str_not_date));
                ActualMeasuringData.getInstance().setBleRssi(0.0d);
                ActualMeasuringData.getInstance().setBleRssiFiltered(0.0d);
                ActualMeasuringData.getInstance().setBleDistance(0.0d);
                ActualMeasuringData.getInstance().setBleDirection(0.0);
            } else if (BTLE_Service.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                readGatServices(btle_service.getSupportedGattServices());
            } else if (BTLE_Service.ACTION_DATA_AVAILABLE.equals(action)) {
                readCallbackGattAttributeValue(intent);
            }
        }
    };

    /* Override methods of Activity */

    /**
     * Gets the Bluetooth Low Energy (BLE) GATT attributes.
     *
     * @return The BLE GATT attributes.
     * @author Ramsauer René
     */
    public static BTLE_GattAttributes getBtleGattAttributes() {
        return btle_gattAttributes;
    }

    /**
     * This method returns an IntentFilter with the possible actions for GATT updates.
     * The actions are GATT_CONNECTED, GATT_DISCONNECTED, GATT_SERVICES_DISCOVERED and DATA_AVAILABLE.
     *
     * @return An IntentFilter with the possible actions for GATT updates.
     */
    private static IntentFilter GattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BTLE_Service.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BTLE_Service.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BTLE_Service.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BTLE_Service.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    /**
     * This method is called when the activity is first created.
     * It initializes the application UI, location criteria and requests user permission.
     *
     * @param savedInstanceState a saved instance of the application state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* CURRENT LOCATION */
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_LOW);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
        /* Call user permission request */
        appPermissions = new AppPermissionsManager(this);
        /* Create application root directory if not exist.*/
        FileManagementHelper.newDirectory(APP_STORAGE_ROOT_DIR);
        /* Initialize application UI */
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.ble_fragment,
                R.id.uwb_fragment,
                R.id.information_fragment,
                R.id.navigate_fragment,
                R.id.settings_fragment
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        /* Initialize CompassAssistant */
        compassAssistant = new CompassAssistant(MainActivity.this);
        compassAssistant.addListener(MainActivity.this);
        /* Init supported ble gatt attributes */
        btle_gattAttributes = new BTLE_GattAttributes(this);
        btle_gattAttributes.readSupportedGattAttributesFromXMl(R.xml.btle_supported_gatt_attributes);
        /* Init BLE-State */
        bluetoothAdapter = BTLE_Service.getBluetoothAdapter(MainActivity.this);
        if (bluetoothAdapter != null) {
            bt_stateBroadcastReceiver = new BT_StateBroadcastReceiver(this, findViewById(R.id.nav_view));
            if (bluetoothAdapter.isEnabled()) {
                SharedPreferenceHelper.changeBooleanValueSharedPreference(this, R.string.settings_ble_enable_bluetooth_key, true);
            } else {
                SharedPreferenceHelper.changeBooleanValueSharedPreference(this, R.string.settings_ble_enable_bluetooth_key, false);
            }
        }
        Intent gattServiceIntent = new Intent(this, BTLE_Service.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        usbSerialReader = new UsbSerialReader(this, UsbSerialReader.BAUD_RATE_115200, USB_VENDOR_ID_ESP32);
    }

    /**
     * This method is called when the activity is started.
     * It registers the Bluetooth state broadcast receiver, sets the USB serial reader listener, initializes the location manager
     * and starts the location update thread.
     */
    @Override
    protected void onStart() {
        super.onStart();
        /* Bluetooth LE */
        appPermissions.requestPermission(AppPermissionsManager.ALL_PERMISSION);
        registerReceiver(bt_stateBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        /* USB Serial */
        usbSerialReader.setListener();
        /* CURRENT LOCATION */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationHandler.postDelayed(locationUpdateRunnable, LOCATION_UPDATE_INTERVAL);
        locationUpdateThread();
    }

    /**
     * Called when the activity is resumed. Starts the compass and checks if the device supports Bluetooth Low Energy (BLE).
     * If the device does not support BLE, a toast message is displayed and the activity is finished.
     * Registers the gattUpdateReceiver for BLE updates, and tries to connect to a BLE device using the btle_service.
     */
    @Override
    protected void onResume() {
        super.onResume();
        /* Compass Assistant */
        compassAssistant.start();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Your devices don't support BLE", Toast.LENGTH_LONG).show();
            finish();
        }
        /* Bluetooth LE */
        registerReceiver(gattUpdateReceiver, GattUpdateIntentFilter());
        if (btle_service != null) {
            final boolean result = btle_service.connect(SharedPreferenceHelper.getStringValueOfSharedPreference(getApplicationContext(), R.string.settings_ble_dialog_choose_device_key));
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    /**
     * Called when the activity is paused. Stops the compass and unregisters the gattUpdateReceiver.
     */
    @Override
    protected void onPause() {
        super.onPause();
        /* Compass Assistant */
        compassAssistant.stop();
        /* Bluetooth LE */
        unregisterReceiver(gattUpdateReceiver);
    }

    /**
     * Called when the activity is stopped. Unregisters the bt_stateBroadcastReceiver, stops the CSV logger, and removes
     * any pending location updates.
     */
    @Override
    protected void onStop() {
        super.onStop();
        /* Bluetooth LE */
        unregisterReceiver(bt_stateBroadcastReceiver);
        /* CSV Logger */
        stopCSVLogger();
        /* CURRENT LOCATION */
        locationHandler.removeCallbacks(locationUpdateRunnable);
    }

    /**
     * Called when the activity is destroyed. Disables the CSV logger, stops the BLE scanner and RSSI listener, unbinds
     * the service connection, and closes the USB serial reader.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* CSV Logger */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(getString(R.string.settings_csv_enable_key), false).commit();
        /* Bluetooth LE */
        if (btle_scanner != null) {
            btle_service.stopRssiListener();
            btle_scanner.stop();
        }
        unbindService(serviceConnection);
        btle_service = null;
        /* USB Serial */
        usbSerialReader.close();
    }

    /**
     * This method is called when a permission has changed.
     *
     * @param permission The permission that has changed.
     */
    @Override
    public void onPermissionChange(AppPermission permission) {
        if (permission.getCategory() == AppPermissionsManager.BLUETOOTH_PERMISSION) {
            Logger.i(TAG, "PERMISSION ON CHANGE: " + permission.getPermission() + " -> " + permission.getCategory());
        }
    }

    /**
     * This method enables Bluetooth.
     *
     * @param view The view calling the method.
     * @author Ramsauer René
     */
    @SuppressLint("MissingPermission")
    public void enableBluetooth(View view) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();
    }

    /**
     * This method starts Bluetooth LE scanning.
     *
     * @author Ramsauer René
     */
    public void startScanning() {
        AppPermissionsManager.requestPermission(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION});
        if (AppPermissionsManager.getPermissionStateByName(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            btle_scanner = new BTLE_Scanner(this, bluetoothAdapter, BTLE_Scanner.SCAN_PERIOD_LONG);
            btle_scanner.start();
        }
        btle_service.connect(SharedPreferenceHelper.getStringValueOfSharedPreference(getApplicationContext(), R.string.settings_ble_dialog_choose_device_key));
    }

    /**
     * Connects to the Bluetooth Low Energy (BLE) service with the device specified in the app preferences,
     * and starts the connection and service.
     *
     * @author Ramsauer René
     */
    public void connectBLEService() {
        btle_service.connect(SharedPreferenceHelper.getStringValueOfSharedPreference(getApplicationContext(), R.string.settings_ble_dialog_choose_device_key));
        connectDevice();
        connectService();
    }

    /**
     * Disconnects from the BLE service and changes the app preferences to show that no device is connected.
     *
     * @author Ramsauer René
     */
    public void disconnectBLEService() {
        btle_service.disconnect();
        SharedPreferenceHelper.changeStringValueSharedPreference(this, R.string.settings_ble_dialog_choose_device_key, getString(R.string.settings_ble_list_preference_no_device_connected));
    }

    /**
     * Callback for when new RSSI values are received from the Bluetooth Low Energy (BLE) GATT server. Sets the
     * RSSI, filtered RSSI, and distance to the actual measuring data instance, and logs the RSSI values.
     *
     * @param gatt         The GATT server.
     * @param rssi         The RSSI value.
     * @param rssiFiltered The filtered RSSI value.
     * @param distance     The distance value.
     * @param status       The status.
     * @author Ramsauer René
     */
    @Override
    public void onNewRssi(BluetoothGatt gatt, double rssi, double rssiFiltered, double distance, int status) {
        ActualMeasuringData.getInstance().setBleRssi(Math.round(rssi * 100.0) / 100.0);
        ActualMeasuringData.getInstance().setBleRssiFiltered(Math.round(rssiFiltered * 100.0) / 100.0);
        ActualMeasuringData.getInstance().setBleDistance(Math.round(distance * 100.0) / 100.0);
        ActualMeasuringData.getInstance().setBleDirection(bleTrackerDirectionCalculator.addGeo(
                rssiFiltered,
                ActualMeasuringData.getInstance().getDeviceCoordinateLatitudeDG(),
                ActualMeasuringData.getInstance().getDeviceCoordinateLongitudeDG()
        ));
        Logger.d(TAG, rssi + " " + rssiFiltered + " " + distance);
    }

    /**
     * Reads the callback GATT attribute value from the BLE service's intent. If the value is a UWB short address
     * or UID, it sets the value to the actual measuring data instance and logs the value. If the value is not in
     * the correct format, it logs a warning.
     *
     * @param intent The BLE service intent containing the GATT attribute value.
     * @author Ramsauer René
     */
    private void readCallbackGattAttributeValue(Intent intent) {
        String uwbShortAddress = intent.getStringExtra(BTLE_Service.EXTRA_DATA_UWB_SHORT_ADR);
        String uwbUid = intent.getStringExtra(BTLE_Service.EXTRA_DATA_UWB_UID);
        if (uwbShortAddress != null) {
            if (StringFormatCheck.isUltraWideBandShortAddress(uwbShortAddress)) {
                Logger.v(TAG, "Get value from ble gatt attribute (UWB short address):" + uwbShortAddress);
                if (!SharedPreferenceHelper.getBooleanValueOfSharedPreference(this, R.string.settings_uwb_manuele_devices_choice_key)) {
                    Logger.v(TAG, "Write received value of UWB short address to ActualMeasuringData: " + uwbShortAddress);
                    ActualMeasuringData.getInstance().setUwbDeviceShortAddress(uwbShortAddress);
                    if (usbSerialReader != null) {
                        setUwbShortAddress(uwbShortAddress);
                    }
                }
            } else {
                Logger.w(TAG, "UWB short address(" +
                        uwbShortAddress +
                        ") has not the ride format. Please check you implementation2"
                );
            }
        }
        if (uwbUid != null) {
            if (StringFormatCheck.isUltraWideBandUid(uwbUid)) {
                Logger.v(TAG, "Get value from ble gatt attribute (UWB unique identifier):" + uwbUid);
                if (!SharedPreferenceHelper.getBooleanValueOfSharedPreference(this, R.string.settings_uwb_manuele_devices_choice_key)) {
                    Logger.v(TAG, "Write received value of UWB unique identifier to ActualMeasuringData: " + uwbShortAddress);
                    ActualMeasuringData.getInstance().setUwbDeviceUid(uwbUid);
                }
            } else {
                Logger.w(TAG, "UWB unique identifier(" +
                        uwbUid +
                        ") has not the ride format. Please check you implementation1"
                );
            }
        }
    }

    /**
     * This method reads the GATT services and characteristics from the provided list.
     * It iterates over the services and then over the characteristics of each service.
     * If a specific characteristic is found, it is read and stored in a local variable for later use.
     *
     * @param gattServices List of BluetoothGattService to be read
     * @author Ramsauer René
     */
    private void readGatServices(List<BluetoothGattService> gattServices) {
        Logger.v(TAG, "logGetService()");
        /* Test if service list is null */
        if (gattServices == null) {
            Logger.v(TAG, "NO Service: List<BluetoothGattService> gattServices = null");
            return;
        }
        /* Service local variable */
        String temp_serviceUuid = null;
        String temp_serviceName = "unknown service";
        /* Characteristic local variable */
        String temp_characteristicUuid = null;
        String temp_characteristicName = "unknown characteristic";
        /* Iterate over the services */
        for (BluetoothGattService gattService : gattServices) {
            temp_serviceUuid = gattService.getUuid().toString();
            temp_serviceName = btle_gattAttributes.lookup(BTLE_GattAttributes.GATT_SERVICE, temp_serviceUuid);
            Logger.v(TAG, "logGetService() Service=" + temp_serviceName + " {" + temp_serviceUuid + "}");
            if (temp_serviceName != null) {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

                /* Iterate over the characteristic */
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                    temp_characteristicUuid = gattCharacteristic.getUuid().toString();
                    temp_characteristicName = btle_gattAttributes.lookup(BTLE_GattAttributes.GATT_CHARACTERISTIC, temp_characteristicUuid);
                    Logger.v(TAG, "logGetService() Characteristic= " + temp_characteristicName + " " + temp_characteristicUuid + " ");
                    characteristicTest.add(gattCharacteristic);
                    if (gattCharacteristic.getUuid().toString().equals(btle_gattAttributes.getString("gatt_ultra_wide_band_identifier_characteristic"))) {
                        btle_service.readCharacteristic(gattCharacteristic);
                        notifyCharacteristicUwbUid = gattCharacteristic;
                        /* Wait for reading characteristic Recommended time*/
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (gattCharacteristic.getUuid().toString().equals(btle_gattAttributes.getString("gatt_ultra_wide_band_short_address"))) {
                        btle_service.readCharacteristic(gattCharacteristic);
                        notifyCharacteristicUwbShortAddress = gattCharacteristic;
                    }
                }
            }
        }
    }

    /**
     * This method reads the stored BLE characteristics.
     * It reads the UWB UID characteristic and the UWB Short Address characteristic.
     * This method is called to read the latest data from the device.
     *
     * @author Ramsauer René
     */
    public void readBleCharacteristic() {
        if (btle_service != null && ActualMeasuringData.getInstance().isBleDeviceIsConnected()) {
            btle_service.readCharacteristic(notifyCharacteristicUwbUid);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            btle_service.readCharacteristic(notifyCharacteristicUwbShortAddress);
        }
    }

    public void connectDevice() {
        Intent gattServiceIntent = new Intent(MainActivity.this, BTLE_Service.class);
        bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    public void connectService() {
        if (notifyCharacteristicUwbShortAddress != null) {
            final int charaProp = notifyCharacteristicUwbShortAddress.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                btle_service.readCharacteristic(notifyCharacteristicUwbShortAddress);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                btle_service.setCharacteristicNotification(notifyCharacteristicUwbShortAddress, true);
            }
        }
        if (notifyCharacteristicUwbUid != null) {
            final int charaProp = notifyCharacteristicUwbUid.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                btle_service.readCharacteristic(notifyCharacteristicUwbUid);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                btle_service.setCharacteristicNotification(notifyCharacteristicUwbUid, true);
            }
        }
    }

    /**
     * This method change the uwb short address fot UsbSerialReader
     * to identification the uwb device
     * @author Ramsauer René
     */
    public void setUwbShortAddress(String uwbShortAddress) {
        usbSerialReader.setUwbShortAddress(uwbShortAddress);
    }

    /**
     * Implementation of the {@link CompassAssistant.CompassAssistantListener} interface.
     * Provides methods to handle compass events and update device direction.
     */
    @Override
    public void onNewDegreesToNorth(float degrees) {
    }

    /**
     * Implementation of the {@link CompassAssistant.CompassAssistantListener} interface.
     * Updates the device direction based on the current and previous degrees to north.
     *
     * @param degrees The current smoothed degrees to north.
     */
    @Override
    public void onNewSmoothedDegreesToNorth(float degrees) {
        if (ActualMeasuringData.getInstance().getDeviceDirection() != degrees) {
            currentDegree = (float) ActualMeasuringData.getInstance().getDeviceDirection();
            ActualMeasuringData.getInstance().setDeviceDirection((degreeBefore + currentDegree + degrees) / 3);
            degreeBefore = currentDegree;
            currentDegree = degrees;
        }
    }

    /**
     * Implementation of the {@link CompassAssistant.CompassAssistantListener} interface.
     * No action needed for this method when compass stops.
     */
    @Override
    public void onCompassStopped() {
    }

    /**
     * Implementation of the {@link CompassAssistant.CompassAssistantListener} interface.
     * No action needed for this method when compass starts.
     */
    @Override
    public void onCompassStarted() {
    }

    @SuppressLint("MissingPermission")
    private void locationUpdateThread() {
        if (locationUpdateRunnable == null) {
            locationUpdateRunnable = () -> {
                locationManager.requestSingleUpdate(criteria, locationListener, null);
                locationHandler.postDelayed(locationUpdateRunnable, LOCATION_UPDATE_INTERVAL);
            };
            locationHandler.postDelayed(locationUpdateRunnable, LOCATION_UPDATE_INTERVAL);
        }
    }

    /**
     * This function override onCsvLoggerStarted()
     *
     * @author Ramsauer René
     * @override CsvLogger.CsvLoggerWriter<CsvMeasuringData>.onCsvLoggerStarted()
     * @author Ramsauer René
     */
    @Override
    public void onCsvLoggerStarted() {
        // Not needed in this case.
    }

    /**
     * This function override onRunCsvLogger()
     *
     * @author Ramsauer René
     * @override CsvLogger.CsvLoggerWriter<CsvMeasuringData>.onRunCsvLogger()
     * @author Ramsauer René
     */
    @Override
    public CsvMeasuringData onRunCsvLogger() {
        ActualMeasuringData tempLiveData = ActualMeasuringData.getInstance();
        return new CsvMeasuringData(
                new Date(),                                         /* time:Date */
                /* Bluetooth LE */
                tempLiveData.getBleDeviceName(),                    /* bleDeviceName:String */
                tempLiveData.getBleDeviceId(),                      /* bleDeviceId:String */
                tempLiveData.getBleRssi(),                          /* bleRssi:String */
                tempLiveData.getBleRssiFiltered(),                  /* bleRssiFiltered:Double */
                tempLiveData.getBleDistance(),                      /* bleDistance:Double */
                tempLiveData.getBleDirection(),                     /* bleDirection:Double */
                /* Ultra-Wide-band */
                tempLiveData.getUwbDeviceShortAddress(),            /* uwbDeviceShortAddress:String */
                tempLiveData.getUwbDeviceUid(),                     /* uwbDeviceId:String */
                tempLiveData.getUwbRssi(),                          /* uwbRssi:Double */
                tempLiveData.getUwbRssiFiltered(),                  /* uwbRssiFiltered:Double */
                tempLiveData.getUwbDistanceToa(),                   /* uwbDistanceToa:Double */
                tempLiveData.getUwbDistanceRssi(),                  /* uwbDistanceRssi:Double */
                tempLiveData.getUwbDirection(),                     /* uwbDirection:Double */
                /* Navigation */
                tempLiveData.getDeviceDirection(),                  /* deviceDirection:Double */
                tempLiveData.getDeviceGeoDirection(),               /* getDeviceGeoDirection:Double */
                tempLiveData.getDeviceMovementDirection(),          /* getDeviceMovementDirection:Double */
                tempLiveData.getDeviceCoordinateLongitudeDG(),      /* coordinateLongitudeDG:Double */
                tempLiveData.getDeviceCoordinateLatitudeDG(),       /* coordinateLatitudeDG:Double */
                /* Status */
                tempLiveData.isUwbDeviceIsConnected(),              /* uwbConnected:Boolean */
                tempLiveData.isBleDeviceIsConnected()               /* bleConnected:Boolean */
        );
    }

    /**
     * This function override onCsvLoggerStopped()
     *
     * @author Ramsauer René
     * @override CsvLogger.CsvLoggerWriter<CsvMeasuringData>.onCsvLoggerStopped()
     * @author Ramsauer René
     */
    @Override
    public void onCsvLoggerStopped() {
        SharedPreferenceHelper.changeBooleanValueSharedPreference(this, R.string.settings_csv_enable_key, false);
    }

    /**
     * Starts a CSV logger that logs sensor data to a CSV file at a set interval.
     * The CSV file is saved in a directory specified by the user in the app settings.
     *
     * @throws IllegalArgumentException if logging interval, number of rows, or recording time is not a valid integer.
     * @author Ramsauer René
     */
    public void startCSVLogger() {
        if (!isCsvLoggerRun) {
            FileManagementHelper.newDirectory(new File(MainActivity.APP_STORAGE_ROOT_DIR, SharedPreferenceHelper.getStringValueOfSharedPreference(this, R.string.settings_csv_directory_key)));
            // Initialize CsvLogger with user specified settings
            csvLogger = new CsvLogger<CsvMeasuringData>(
                    this,
                    Environment.getExternalStorageDirectory().getPath() + "/Iot Location Tracker APP/" + SharedPreferenceHelper.getStringValueOfSharedPreference(this, R.string.settings_csv_directory_key),    /* parentDirectory:String */
                    SharedPreferenceHelper.getStringValueOfSharedPreference(this, R.string.settings_csv_file_key),  /* csvFileName:String */
                    Integer.parseInt(SharedPreferenceHelper.getStringValueOfSharedPreference(this, R.string.settings_csv_interval_ms_key)), /* loggingInterval:int */
                    Integer.parseInt(SharedPreferenceHelper.getStringValueOfSharedPreference(this, R.string.settings_csv_number_row_key)),   /* numberOfRow:int */
                    Integer.parseInt(SharedPreferenceHelper.getStringValueOfSharedPreference(this, R.string.settings_csv_time_s_key)) /* recordingTime */
            );
            // Start CSV logger and show snackbar message to indicate that logger is running
            csvLogger.start();
            CustomUserInformation.showSnackbarApplication(
                    this,
                    findViewById(R.id.nav_view),
                    "CSV-Logger is running ...",
                    "Logging time: " + SharedPreferenceHelper.getStringValueOfSharedPreference(this, R.string.settings_csv_time_s_key) + " seconds.",
                    Snackbar.LENGTH_SHORT, Gravity.BOTTOM
            );
            isCsvLoggerRun = true;
        }
    }

    /**
     * Stops the CSV logger if it is running.
     * Shows a snackbar message indicating that the logger was stopped and where the log files were stored.
     * Sets the isCsvLoggerRun flag to false.
     *
     * @author Ramsauer René
     */
    public void stopCSVLogger() {
        if (csvLogger != null) {
            if (isCsvLoggerRun) {
                CustomUserInformation.showSnackbarApplication(
                        this,
                        findViewById(R.id.nav_view),
                        "CSV-logger was stopped.",
                        "Log files were stored in the directory.",
                        Snackbar.LENGTH_SHORT, Gravity.BOTTOM
                );
            }
            csvLogger.stop();
        }
        isCsvLoggerRun = false;
    }
}