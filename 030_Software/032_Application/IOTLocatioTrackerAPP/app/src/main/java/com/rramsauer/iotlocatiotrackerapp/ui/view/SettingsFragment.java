package com.rramsauer.iotlocatiotrackerapp.ui.view;
// TODO: 1) Documentation of the source code.

/* Android imports */

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.rramsauer.iotlocatiotrackerapp.MainActivity;
import com.rramsauer.iotlocatiotrackerapp.R;
import com.rramsauer.iotlocatiotrackerapp.storage.state.ActualMeasuringData;
import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;
import com.rramsauer.iotlocatiotrackerapp.util.str.StringFormatCheck;

/**
 * Class Settings Fragment.
 * This class provides the ui for the fragment settings. Which can be called via the settings button in the navigation bar.
 *
 * @author Ramsauer René
 * @version V1.2
 * @implNote This class was implemented with the current standards and proposed technologies of Android.
 * Furthermore, a fread for updating the UI was implemented.
 * @see android.content.SharedPreferences
 * @see androidx.preference.PreferenceFragmentCompat
 */
@SuppressWarnings({"ConstantConditions", "Convert2Lambda", "SameParameterValue"})
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "SettingsFragment";
    private SharedPreferences sharedPreferences;

    /**
     * This function override onCreatePreferences()
     *
     * @author Ramsauer René
     * @override onCreatePreferences in class PreferenceFragmentCompat
     */
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.root_preferences);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        /* BLE */
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_ble_enable_bluetooth_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_ble_dialog_choose_device_key));

        /* UWB */
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_uwb_short_adr_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_uwb_uid_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_uwb_manuele_devices_choice_key));
        /* NAV */
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_nav_uwb_enable_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_nav_ble_enable_key));
        /* CSV */
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_csv_directory_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_csv_interval_ms_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_csv_enable_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_csv_file_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_csv_time_s_key));
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.settings_csv_number_row_key));
        /* Set only number preference */
        setOnlyNumberPreference(getString(R.string.settings_csv_time_s_key), false);
        setOnlyNumberPreference(getString(R.string.settings_csv_interval_ms_key), false);
        setOnlyNumberPreference(getString(R.string.settings_csv_number_row_key), false);
        setOnlyLetterPreference(getString(R.string.settings_csv_file_key), "[a-zA-Z 0-9 _.]+");
        setOnlyLetterPreference(getString(R.string.settings_csv_directory_key), "[a-zA-Z 0-9 _]+");
        /* On Click listener for start scanning. */
        onBLEPreferenceClickChooseBluetoothDevice();
        onBLEPreferenceClickEnableBluetooth();
    }

    /**
     * This function override onResume()
     *
     * @author Ramsauer René
     * @override onResume in class Fragment
     */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * This function override onDestroyView()
     *
     * @author Ramsauer René
     * @override onDestroyView in class PreferenceFragmentCompat
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * This function restricts the input of the EditTextPreference to numbers
     *
     * @param key                      Passed the string key of the EditTextPreference.
     * @param withFloatingPointNumbers Allow floating point numbers.
     * @author Ramsauer René
     * @implNote Example: setOnlyNumberPreference(key, withFloatingPointNumbers)
     * @since <Android API 32 Platform></Android>
     */
    private void setOnlyNumberPreference(String key, boolean withFloatingPointNumbers) {
        EditTextPreference editTextPreference = getPreferenceManager().findPreference(key);
        editTextPreference.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (withFloatingPointNumbers) {
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
            } else {
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            }
        });
    }

    /**
     * This function restricts the input of the EditTextPreference to specify character.
     *
     * @param key       Passed the string key of the EditTextPreference.
     * @param filterOpt Define the allowed characters.
     * @author Ramsauer René
     * @implNote Example: setOnlyLetterPreference(key, filterOpt)
     * @since <Android API 32 Platform></Android>
     */
    private void setOnlyLetterPreference(String key, String filterOpt) {
        EditTextPreference editTextPreference = getPreferenceManager().findPreference(key);
        editTextPreference.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setFilters(new InputFilter[]{
                    new InputFilter() {
                        public CharSequence filter(CharSequence src, int start,
                                                   int end, Spanned dst, int dstart, int dend) {
                            if (src.toString().matches(filterOpt)) {
                                return src;
                            }
                            return "";
                        }
                    }});
        });
    }

    /**
     * This function override onSharedPreferenceChanged()
     *
     * @author Ramsauer René
     * @override onCreatePreferences() bzw. onSharedPreferenceChanged()
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        onSharedPreferenceChangedBLE(sharedPreferences, key);
        onSharedPreferenceChangedUWB(sharedPreferences, key);
        onSharedPreferenceChangedNAV(sharedPreferences, key);
        onSharedPreferenceChangedCSV(sharedPreferences, key);
    }

    /**
     * This function provides the logic of the preference category BLE for listener
     *
     * @param sharedPreferences Passed the sharedPreferences of the listener.
     * @param key               Passed the key of the listener.
     * @author Ramsauer René
     * @implNote Example: onSharedPreferenceChangedBLE(sharedPreferences, key)
     * @since <Android API 32 Platform></Android>
     */
    private void onSharedPreferenceChangedBLE(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_ble_permission_not_accepted_key))) {
            if (sharedPreferences.getBoolean(key, false)) {
                findPreference(getString(R.string.settings_ble_dialog_choose_device_key)).setVisible(false);
                findPreference(getString(R.string.settings_ble_enable_bluetooth_key)).setEnabled(false);
            } else {
                findPreference(getString(R.string.settings_ble_enable_bluetooth_key)).setEnabled(false);
            }
        }
        if (key.equals(getString(R.string.settings_ble_enable_bluetooth_key))) {
            if (sharedPreferences.getBoolean(key, false)) {
                findPreference(getString(R.string.settings_ble_dialog_choose_device_key)).setVisible(true);
                findPreference(getString(R.string.settings_ble_enable_bluetooth_key)).setVisible(false);
            } else {
                findPreference(getString(R.string.settings_ble_dialog_choose_device_key)).setVisible(false);
                findPreference(getString(R.string.settings_ble_enable_bluetooth_key)).setVisible(true);
            }
        }
        if (key.equals(getString(R.string.settings_ble_dialog_choose_device_key))) {
            Preference preferenceBluetoothDevice = findPreference(key);
            preferenceBluetoothDevice.setSummary(sharedPreferences.getString(getString(R.string.settings_ble_dialog_choose_device_key), ""));
            Logger.i(TAG, "The following SharedPreferences have been changed: " + key + " to " + sharedPreferences.getString(getString(R.string.settings_ble_dialog_choose_device_key), ""));
            ((MainActivity) getActivity()).connectBLEService();
        }
    }

    /**
     * This function provides the logic of the preference category UWB for listener
     *
     * @param sharedPreferences Passed the sharedPreferences of the listener.
     * @param key               Passed the key of the listener.
     * @author Ramsauer René
     * @implNote Example: onSharedPreferenceChangedUWB(sharedPreferences, key)
     * @since <Android API 32 Platform></Android>
     */
    private void onSharedPreferenceChangedUWB(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_uwb_manuele_devices_choice_key))) {
            if (sharedPreferences.getBoolean(key, false)) {
                findPreference(getString(R.string.settings_uwb_short_adr_key)).setVisible(true);
                findPreference(getString(R.string.settings_uwb_uid_key)).setVisible(true);
                findPreference(key).setTitle(R.string.settings_uwb_manuele_devices_choice_auto);
            } else {
                findPreference(getString(R.string.settings_uwb_short_adr_key)).setVisible(false);
                findPreference(getString(R.string.settings_uwb_uid_key)).setVisible(false);
                findPreference(key).setTitle(R.string.settings_uwb_manuele_devices_choice_manuel);
                sharedPreferences.edit().putString(getString(R.string.settings_uwb_short_adr_key), "").commit();
                sharedPreferences.edit().putString(getString(R.string.settings_uwb_uid_key), "").commit();
                ((MainActivity) getActivity()).setUwbShortAddress("");
                ((MainActivity) getActivity()).readBleCharacteristic();
            }
        }
        if (key.equals(getString(R.string.settings_uwb_uid_key))) {
            if (sharedPreferences.getBoolean(getString(R.string.settings_uwb_manuele_devices_choice_key), false)) {
                if (StringFormatCheck.isUltraWideBandUid(sharedPreferences.getString(getString(R.string.settings_uwb_uid_key), " "))) {
                    ActualMeasuringData.getInstance().setUwbDeviceUid(sharedPreferences.getString(getString(R.string.settings_uwb_uid_key), " "));
                }

            }
        }
        if (key.equals(getString(R.string.settings_uwb_short_adr_key))) {
            if (sharedPreferences.getBoolean(getString(R.string.settings_uwb_manuele_devices_choice_key), false)) {
                if (StringFormatCheck.isUltraWideBandShortAddress(sharedPreferences.getString(getString(R.string.settings_uwb_short_adr_key), " "))) {
                    ActualMeasuringData.getInstance().setUwbDeviceShortAddress(sharedPreferences.getString(getString(R.string.settings_uwb_short_adr_key), " "));
                    ((MainActivity) getActivity()).setUwbShortAddress(sharedPreferences.getString(getString(R.string.settings_uwb_short_adr_key), " "));
                }

            }
        }
    }

    /**
     * This function provides the logic of the preference category NAV for listener
     *
     * @param sharedPreferences Passed the sharedPreferences of the listener.
     * @param key               Passed the key of the listener.
     * @author Ramsauer René
     * @implNote Example: onSharedPreferenceChangedNAV(sharedPreferences, key)
     * @since <Android API 32 Platform></Android>
     */
    private void onSharedPreferenceChangedNAV(SharedPreferences sharedPreferences, String key) {
        SwitchPreference preferenceNavBleEn = findPreference(getString(R.string.settings_nav_ble_enable_key));
        SwitchPreference preferenceNavUwbEn = findPreference(getString(R.string.settings_nav_uwb_enable_key));

        /* SWITCH NAV BLE ENABLE */
        if (key.equals(getString(R.string.settings_nav_ble_enable_key))) {
            if (sharedPreferences.getBoolean(key, false)) {
                preferenceNavUwbEn.setChecked(false);
            }
        }
        /* SWITCH NAV UWB ENABLE */
        if (key.equals(getString(R.string.settings_nav_uwb_enable_key))) {
            if (sharedPreferences.getBoolean(key, false)) {
                preferenceNavBleEn.setChecked(false);
            }
        }
    }

    /**
     * This function provides the logic of the preference category CSV for listener
     *
     * @param sharedPreferences Passed the sharedPreferences of the listener.
     * @param key               Passed the key of the listener.
     * @author Ramsauer René
     * @implNote Example: onSharedPreferenceChangedCSV(sharedPreferences, key)
     * @since <Android API 32 Platform></Android>
     */
    private void onSharedPreferenceChangedCSV(SharedPreferences sharedPreferences, String key) {
        /* Set edit number */
        if (key.equals(getString(R.string.settings_csv_enable_key))) {
            if (sharedPreferences.getBoolean(key, false)) {
                EditTextPreference preferenceCsvFile = findPreference(getString(R.string.settings_csv_file_key));
                preferenceCsvFile.setVisible(false);
                EditTextPreference preferenceCsvDirectory = findPreference(getString(R.string.settings_csv_directory_key));
                preferenceCsvDirectory.setVisible(false);
                EditTextPreference preferenceCsvNumberRow = findPreference(getString(R.string.settings_csv_number_row_key));
                preferenceCsvNumberRow.setVisible(false);
                EditTextPreference preferenceCsvIntervalMs = findPreference(getString(R.string.settings_csv_interval_ms_key));
                preferenceCsvIntervalMs.setVisible(false);
                EditTextPreference preferenceCsvTimeS = findPreference(getString(R.string.settings_csv_time_s_key));
                preferenceCsvTimeS.setVisible(false);
                SwitchPreference preferenceCsvEnable = findPreference(key);
                preferenceCsvEnable.setSummary(
                        "CSV-Logger is run ...\n" +
                                "FILE: " + sharedPreferences.getString(getString(R.string.settings_csv_file_key), "") + "\n" +
                                "DIRECTORY: " + sharedPreferences.getString(getString(R.string.settings_csv_directory_key), "") + "\n" +
                                "NUMBER OR ROWS: " + sharedPreferences.getString(getString(R.string.settings_csv_number_row_key), "") + "\n" +
                                "INTERVAL: " + sharedPreferences.getString(getString(R.string.settings_csv_interval_ms_key), "") + "[ms]\n" +
                                "MEASURING TIME: " + sharedPreferences.getString(getString(R.string.settings_csv_time_s_key), "") + "[s]"
                );
                ((MainActivity) getActivity()).startCSVLogger();
            } else {
                SwitchPreference switchPreference = findPreference(getString(R.string.settings_csv_enable_key));
                switchPreference.setChecked(false);
                EditTextPreference preferenceCsvFile = findPreference(getString(R.string.settings_csv_file_key));
                preferenceCsvFile.setVisible(true);
                EditTextPreference preferenceCsvDirectory = findPreference(getString(R.string.settings_csv_directory_key));
                preferenceCsvDirectory.setVisible(true);
                EditTextPreference preferenceCsvNumberRow = findPreference(getString(R.string.settings_csv_number_row_key));
                preferenceCsvNumberRow.setVisible(true);
                EditTextPreference preferenceCsvIntervalMs = findPreference(getString(R.string.settings_csv_interval_ms_key));
                preferenceCsvIntervalMs.setVisible(true);
                EditTextPreference preferenceCsvTimeS = findPreference(getString(R.string.settings_csv_time_s_key));
                preferenceCsvTimeS.setVisible(true);
                SwitchPreference preferenceCsvEnable = findPreference(key);
                preferenceCsvEnable.setSummary("");
                ((MainActivity) getActivity()).stopCSVLogger();
            }
        }
    }

    /**
     * Sets an on-click listener for the Bluetooth device preference,
     * which initiates scanning for nearby Bluetooth devices.
     * When the preference is clicked and no device are connected,
     * the method {@link MainActivity#startScanning()} is called to start scanning for devices.
     * If the device is connected, than the the methode disconnectBLEService() was called.
     *
     * @author Ramsauer René
     */
    private void onBLEPreferenceClickChooseBluetoothDevice() {
        Preference preference = findPreference(getString(R.string.settings_ble_dialog_choose_device_key));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                if (ActualMeasuringData.getInstance().isBleDeviceIsConnected()) {
                    ((MainActivity) getActivity()).disconnectBLEService();
                } else {
                    ((MainActivity) getActivity()).startScanning();
                }
                return false;
            }
        });
    }

    /**
     * Sets an OnClickListener on the Bluetooth enable preference to handle when it is clicked.
     * When clicked, it calls the enableBluetooth method in the MainActivity with the current view.
     *
     * @author Ramsauer René
     */
    private void onBLEPreferenceClickEnableBluetooth() {
        Preference preference = findPreference(getString(R.string.settings_ble_enable_bluetooth_key));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                ((MainActivity) getActivity()).enableBluetooth(SettingsFragment.this.getView());
                return false;
            }
        });
    }

}