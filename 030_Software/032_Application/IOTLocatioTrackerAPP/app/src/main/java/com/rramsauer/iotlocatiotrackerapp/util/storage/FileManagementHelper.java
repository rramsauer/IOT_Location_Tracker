package com.rramsauer.iotlocatiotrackerapp.util.storage;

/* Android imports */

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Class FileManagementHelper.
 * This class provides some function for management files in a android application.
 *
 * @author Ramsauer René
 * @version V1.0
 * @implNote ATTENTION: This class works on Android API 32 - In the last api updates it was changed
 * @see java.io.File
 * @see java.io.FileInputStream
 * @see java.io.FileOutputStream
 * @see java.io.IOException
 * @see java.nio.channels.FileChannel
 * @see androidx.core.content.ContextCompat
 */
public class FileManagementHelper {
    public static String LOG_TAG_CSV = "FILE MANAGEMENT";

    /**
     * This function create a new directory if not exist.<p>
     *
     * @param directory Transfer the directory path.
     * @return Return true if Copy was success | Returns false if copy was not success
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: newDirectory(directory) <p>
     * ATTENTION: Set Primision: <p>
     * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * @see java.io.File
     * @since <Android API 32 Platform></Android>
     */
    public static boolean newDirectory(File directory) {
        if (!directory.exists()) {
            Logger.v(LOG_TAG_CSV, "Create directory if not exist: \"" + directory + "\"", Thread.currentThread().getStackTrace()[2]);
            directory.mkdir();
        }
        /* Test if directory exist */
        if (!directory.exists()) {
            Logger.e(LOG_TAG_CSV, "Create directory was not successful: \"" + directory + "\"", Thread.currentThread().getStackTrace()[2]);
            return false;
        }
        return true;
    }

    /**
     * DeleteFile or Folder if exist..<p>
     *
     * @param path The file or folder to be deleted
     * @return Return true if Copy was success | Returns false if copy was not success
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: deleteItem(path) <p>
     * ATTENTION: Set Primision: <p>
     * uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" <p>
     * or <p>
     * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * @see java.io.File
     * @since <Android API 32 Platform></Android>
     */
    public static boolean deleteItem(File path) {
        if (path.exists()) {
            path.delete();
            if (!path.exists()) {
                return true;
            } else {
                Logger.d("Delete-Item", path.getAbsolutePath() + " The wasn't successful.");
                return false;
            }
        } else {
            Logger.d("Delete-Item", path.getAbsolutePath() + "The file could not be deleted since it does not exist.");
            return true;
        }
    }

    /**
     * This function returns true if path is available.<p>
     *
     * @param path Pass the path to be tested
     * @return Return true if path exist | Returns false if path not exist
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: testPath(path) <p>
     * ATTENTION: Set Primision: <p>
     * uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" <p>
     * or <p>
     * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * @see java.io.File
     * @since <Android API 32 Platform></Android>
     */
    public static boolean testPath(File path) {
        if (path.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This function returns the root directory of the SSD card.<p>
     * This function returns an empty string if no SD card is available.<p>
     * ATTENTION: Write on sd-card don't work with api 32.
     * ATTENTION: The file cannot be bigger than 2 GB.
     *
     * @param context         the context
     * @param sourceFile      Transfer the source file.
     * @param destinationFile Transfer the destination file.
     * @return Return true if Copy was success | Returns false if copy was not success
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: copyFile(context, sourceFile, destinationFile) <p>
     * ATTENTION: Set Primision: <p>
     * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * @see java.io.File
     * @see androidx.core.content.ContextCompat
     * @since <Android API 32 Platform></Android>
     */
    public static boolean copyFile(Context context, File sourceFile, File destinationFile) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        boolean copySuccess = true;
        try {
            fileInputStream = new FileInputStream(sourceFile);
            fileOutputStream = new FileOutputStream(destinationFile);
            FileChannel inChannel = fileInputStream.getChannel();
            FileChannel outChannel = fileOutputStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException ioe) {
            Toast.makeText(context, "SAVE CSV-FILE WOSENT SUCESS. In case of repeated errors please contact the support team", Toast.LENGTH_LONG).show();
            copySuccess = false;
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                Logger.d("IOException copyItem", "Can not Close inputStream");
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                Logger.d("IOException copyItem", "Can not Close outputStream");
            }
        }
        return copySuccess;
    }

    /**
     * This function returns the root directory of the SSD card.<p>
     * This function returns an empty string if no SD card is available.
     *
     * @param context          the context
     * @param writeUserMessage Enable or disable user message (enable = TRUE | disable = FALSE)
     * @return Return the string oft the root directory if SD-Card is available and empty string if not available
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: getSDCardRootDirectory(context,true) <p>
     * ATTENTION: Set Primision: <p>
     * uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" <p>
     * or <p>
     * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * @see java.io.File
     * @see androidx.core.content.ContextCompat
     * @since <Android API 32 Platform></Android>
     */
    public static String getSDCardRootDirectory(Context context, boolean writeUserMessage) {
        if (isSDCardAvailable(context, writeUserMessage)) {
            File[] storages = ContextCompat.getExternalFilesDirs(context, null);
            return storages[1].getAbsolutePath().split("/Android/data/" + context.getApplicationContext().getPackageName() + "/files")[0];
        } else {
            return "";
        }
    }

    /**
     * This function returns the root directory of external storage
     *
     * @return root directory of external storage
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: getExternalStorageRootDirectory() <p>
     * ATTENTION: Set Primision: <p>
     * uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" <p>
     * or <p>
     * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * @see android.os.Environment;
     * @since <Android API 32 Platform></Android>
     */
    public static File getExternalStorageRootDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * This function checks if an sd card is available.<p>
     * This function return an boolean and Ride an message to the User.
     *
     * @param context          the context
     * @param writeUserMessage Enable or disable user message (enable = TRUE | disable = FALSE)
     * @return Return TRUE if SD-Card is available and FALSE if not available
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: isSDCardAvailable(context,true) <p>
     * ATTENTION: Set Primision: <p>
     * uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" <p>
     * or <p>
     * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * @see java.io.File
     * @see androidx.core.content.ContextCompat
     * @since <Android API 32 Platform></Android>
     */
    public static boolean isSDCardAvailable(Context context, boolean writeUserMessage) {
        File[] storages = ContextCompat.getExternalFilesDirs(context, null);
        if (storages.length > 1 && storages[0] != null && storages[1] != null) { //storages[1] = SDCard | storages[0] = Interner Speicher
            return true;
        } else {
            if (writeUserMessage) {
                Toast.makeText(context, "SD Card is not Available:\nPlease insert an SD card into your smartphone.", Toast.LENGTH_LONG).show();
            }
            return false;
        }
    }

    /**
     * This function return a counter file.<p>
     *
     * @param directoryName Passed directory.
     * @param fileName      Passed file name.
     * @param fileExtension Passed file extension.
     * @param fileCounter   Passed file counter.
     * @return Returns a file if no teacher string has been passed.
     * @author Ramsauer René
     * @version V1.0
     * @implNote Example: newCounterCsvFile(directoryName, fileName, fileExtension, fileCounter) <p>
     * ATTENTION: Set Primision: <p>
     * uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" <p>
     * or <p>
     * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     * @see java.io.File
     * @see androidx.core.content.ContextCompat
     * @since <Android API 32 Platform></Android>
     */
    public static File newCounterFile(String directoryName, String fileName, String fileExtension, int fileCounter) {
        File tempFile = null;
        String tempFileExtension;

        /* Test if empty string is passed */
        if (directoryName.equals("") || fileName.equals("") || fileExtension.equals("")) {
            Logger.e(LOG_TAG_CSV, "Passed zero string at newCounterCsvFile()", Thread.currentThread().getStackTrace()[2]);
            return tempFile;
        }

        /* Create log directory */
        if (!FileManagementHelper.newDirectory(new File(directoryName))) {
            Logger.e(LOG_TAG_CSV, "Create directory was not successful \"" + directoryName + "\"", Thread.currentThread().getStackTrace()[2]);
            return tempFile;
        }

        /* Test if fileExtension contains "." */
        if (fileExtension.contains(".")) {
            tempFileExtension = fileExtension.split("\\.")[1];
        } else {
            tempFileExtension = fileExtension;
        }

        if (fileCounter == 0) {
            /* Create File if fileCounter = 0 */
            if (fileName.contains(".")) {
                tempFile = new File(directoryName, fileName.split("\\.")[0] + "." + tempFileExtension);
                Logger.v(LOG_TAG_CSV, "Return files : \"" + tempFile + "\"", Thread.currentThread().getStackTrace()[2]);
                return tempFile;
            } else {
                tempFile = new File(directoryName, fileName + "." + tempFileExtension);
                Logger.v(LOG_TAG_CSV, "Return files : \"" + tempFile + "\"", Thread.currentThread().getStackTrace()[2]);
                return tempFile;
            }
        } else {
            /* Create File if fileCounter > 0 */
            if (fileName.contains(".")) {
                tempFile = new File(directoryName, fileName.split("\\.")[0] + "_" + String.valueOf(fileCounter) + "." + tempFileExtension);
                Logger.v(LOG_TAG_CSV, "Return files : \"" + tempFile + "\"", Thread.currentThread().getStackTrace()[2]);
                return tempFile;
            } else {
                tempFile = new File(directoryName, fileName + "_" + String.valueOf(fileCounter) + "." + tempFileExtension);
                Logger.v(LOG_TAG_CSV, "Return files : \"" + tempFile + "\"", Thread.currentThread().getStackTrace()[2]);
                return tempFile;
            }
        }
    }
}
