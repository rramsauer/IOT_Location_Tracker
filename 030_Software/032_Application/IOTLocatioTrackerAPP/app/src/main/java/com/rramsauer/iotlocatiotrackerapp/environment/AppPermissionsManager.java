package com.rramsauer.iotlocatiotrackerapp.environment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manage permission request and
 * provides several Function for manage Permissions.
 *
 * @author Ramsauer René
 * @version V1.9
 * @implNote New permissions are defined in the addPermissionToPermissionList()
 * function as follows:
 * addPermissionToPermissionList() {
 * permissionList.add(new AppPermission(Manifest.permission.INTERNET,NEW_PERMISSION));
 * }
 * Attention: Each permission must also be created in the AndroidManifest.xml.
 */
public class AppPermissionsManager extends Activity {
    /* Define permission-group typ */
    public static final int ALL_PERMISSION = 0;
    public static final int GENERAL_PERMISSION = 1;
    public static final int BLUETOOTH_PERMISSION = 2;
    public static final int NAVIGATION_PERMISSION = 3;
    public static final int STORAGE_PERMISSION = 4;
    /* Log-Tag */
    private static final String TAG = "AppPermissionsManager";
    /* Context */
    private final Context context;
    /* Interface */
    private final OnPermissionCage l;
    /* Activity Result */
    private ActivityResultLauncher<String[]> permissionResultLauncher;
    /* Permission list */
    private ArrayList<AppPermission> permissionList = new ArrayList<>();

    /**
     * CONSTRUCTOR
     * initialization of AppPermissionsManager.
     *
     * @param context The context to use. Usually your android.app.Application or android.app
     * @author Ramsauer René
     */
    public AppPermissionsManager(Context context) {
        this.context = context;
        this.l = (OnPermissionCage) context;
        addPermissionToPermissionList();
    }

    /**
     * This static function provides an permission request.
     *
     * @param activity         The activity context to use.
     *                         Usually your android.app.Application or android.app
     * @param permissionStrArr Pass an String array of the permissions.
     * @author Ramsauer René
     * @implNote Ex.: requestPermission(getActivityContext(), {...,permission string array,...})
     */
    public static void requestPermission(Activity activity, String[] permissionStrArr) {
        ActivityCompat.requestPermissions(activity, permissionStrArr, PackageManager.PERMISSION_GRANTED);
    }

    /**
     * This static function provides an permission check.
     *
     * @param context    The context to use.
     *                   Usually your android.app.Application or android.app
     * @param permission Pass the permissions.
     * @author Ramsauer René
     * @implNote Ex.: getPermissionStateByName(getContext, permission)
     */
    public static boolean getPermissionStateByName(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * This Function collect all adds to the permissionList.
     * Attention: Each permission must also be created in the AndroidManifest.xml.
     *
     * @author Ramsauer René
     * @see com.rramsauer.iotlocatiotrackerapp.environment.AppPermission
     */
    private void addPermissionToPermissionList() {
        /* General permission */
        permissionList.add(new AppPermission(Manifest.permission.INTERNET, GENERAL_PERMISSION));
        /* Bluetooth permission */
        permissionList.add(new AppPermission(Manifest.permission.BLUETOOTH, BLUETOOTH_PERMISSION));
        permissionList.add(new AppPermission(Manifest.permission.BLUETOOTH_ADMIN, BLUETOOTH_PERMISSION));
        permissionList.add(new AppPermission(Manifest.permission.ACCESS_FINE_LOCATION, BLUETOOTH_PERMISSION));
        permissionList.add(new AppPermission(Manifest.permission.BLUETOOTH_CONNECT, BLUETOOTH_PERMISSION));
        /* Navigation permission */
        permissionList.add(new AppPermission(Manifest.permission.ACCESS_COARSE_LOCATION, NAVIGATION_PERMISSION));
        /* Storage permission */
        permissionList.add(new AppPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION));
        permissionList.add(new AppPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION));
        permissionList.add(new AppPermission(Manifest.permission.WAKE_LOCK, STORAGE_PERMISSION));
    }

    /**
     * This function provides an permission request.
     *
     * @param category pass permission-group typ (@PermissionTyp)
     * @author Ramsauer René
     * @implNote Ex.: requestPermission(AppPermissionsManager.GENERAL_PERMISSION)
     */
    public void requestPermission(@PermissionTyp int category) {
        /* Init Permission Launcher */
        initPermissionLauncher();
        /* Check Permission */
        checkPermission(category);
        /* Request Permission */
        List<String> requestPermission = addPermissionToRequestsList(category);
        if (!requestPermission.isEmpty()) {
            permissionResultLauncher.launch(requestPermission.toArray(new String[0]));
        }
    }

    /**
     * This function provides an permission check of an permission category type.
     *
     * @param category pass permission-group typ (@PermissionTyp)
     * @return Returns the success of the check.
     * @author Ramsauer René
     * @implNote Ex.: requestPermission(AppPermissionsManager.GENERAL_PERMISSION)
     */
    public boolean checkPermission(@PermissionTyp int category) {
        boolean result = true;
        for (AppPermission permission : permissionList) {
            if ((permission.getCategory() == category) && (ALL_PERMISSION != category)) {
                permission.setState(permissionIsGranted(permission.getPermission()));
                result = (result && permission.isState());
            } else if (ALL_PERMISSION == category) {
                permission.setState(permissionIsGranted(permission.getPermission()));
                result = (result && permission.isState());
            } else {
                /*Category not exist return false*/
                result = false;
            }
        }
        return result;
    }

    /**
     * This function provides an single permission check.
     *
     * @param permission Pass the permission to be checked.
     * @return Returns the success of the check.
     * @author Ramsauer René
     * @implNote Ex.: requestPermission(AppPermissionsManager.GENERAL_PERMISSION)
     */
    private boolean permissionIsGranted(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * This function returns all not granted permissions.
     * This is a private function and supports the functionality of the AppPermissionManager.
     *
     * @param category pass permission-group typ (@PermissionTyp)
     * @return Returns an ArrayList of all permission you was not granted.
     * @author Ramsauer René
     * @implNote Ex.: addPermissionToRequestsList(AppPermissionsManager.GENERAL_PERMISSION)
     */
    private ArrayList<String> addPermissionToRequestsList(@PermissionTyp int category) {
        ArrayList<String> permissionForRequest = new ArrayList<>();
        for (AppPermission permission : permissionList) {
            if (!permission.isState()) {
                if ((permission.getCategory() == category) && (ALL_PERMISSION != category)) {
                    permissionForRequest.add(permission.getPermission());
                } else if (ALL_PERMISSION == category) {
                    permissionForRequest.add(permission.getPermission());
                }

            }
        }
        return permissionForRequest;
    }

    /**
     * This function init the PermissionLauncher.
     * This is a private function and supports the functionality of the AppPermissionManager.
     *
     * @author Ramsauer René
     * @implNote Ex.: initPermissionLauncher ()
     */
    private void initPermissionLauncher() {
        permissionResultLauncher = ((AppCompatActivity) context).registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            for (AppPermission permission : permissionList) {
                if (result.get(permission.getPermission()) != null) {
                    permission.setState(result.get(permission.getPermission()));
                    Logger.w(TAG, "OnPermissionChange -> Name:" + permission.getPermission() + " Sate:" + permission.isState());
                    l.onPermissionChange(permission);
                }
            }
        });
    }

    /**
     * This interface provides onPermissionChangeListener of groups of permission
     *
     * @author Ramsauer René
     */
    public interface OnPermissionCage {
        /**
         * is calling if a permission is change.
         */
        void onPermissionChange(AppPermission permission);

    }

    /**
     * Denotes that an integer, field or method return value is expected to be an permission-group typ reference
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ALL_PERMISSION, GENERAL_PERMISSION, BLUETOOTH_PERMISSION, NAVIGATION_PERMISSION, STORAGE_PERMISSION})
    public @interface PermissionTyp {
    }

}