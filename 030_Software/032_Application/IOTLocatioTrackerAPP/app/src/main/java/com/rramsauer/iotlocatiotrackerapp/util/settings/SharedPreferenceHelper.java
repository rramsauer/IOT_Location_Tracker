package com.rramsauer.iotlocatiotrackerapp.util.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.StringRes;
import androidx.preference.PreferenceManager;

/**
 * This class provides helper methods for managing SharedPreferences.
 *
 * @author Ramsauer René
 * @version 1.2
 */
public class SharedPreferenceHelper {
    /**
     * Changes the string value of the shared preference with the specified resource id.
     *
     * @param context The context of the application.
     * @param resId   The resource id of the preference key.
     * @param value   The new value to be stored in the shared preference.
     * @author Ramsauer René
     */
    public static void changeStringValueSharedPreference(Context context, @StringRes int resId, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(resId), value);
        editor.commit();
    }

    /**
     * Changes the boolean value of the shared preference with the specified resource id.
     *
     * @param context The context of the application.
     * @param resId   The resource id of the preference key.
     * @param value   The new value to be stored in the shared preference.
     * @author Ramsauer René
     */
    public static void changeBooleanValueSharedPreference(Context context, @StringRes int resId, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(resId), value);
        editor.commit();
        Log.d("ChangePreff", context.getString(resId) + value);
    }

    /**
     * Retrieves the boolean value of the shared preference with the specified resource id.
     *
     * @param context The context of the application.
     * @param resId   The resource id of the preference key.
     * @return The boolean value stored in the shared preference.
     * If the key is not found, returns false.
     * @author Ramsauer René
     */
    public static boolean getBooleanValueOfSharedPreference(Context context, @StringRes int resId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(resId), false);
    }

    /**
     * Retrieves the string value of the shared preference with the specified resource id.
     *
     * @param context The context of the application.
     * @param resId   The resource id of the preference key.
     * @return The string value stored in the shared preference.
     * If the key is not found, returns an empty string.
     * @author Ramsauer René
     */
    public static String getStringValueOfSharedPreference(Context context, @StringRes int resId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(resId), "");
    }

}
