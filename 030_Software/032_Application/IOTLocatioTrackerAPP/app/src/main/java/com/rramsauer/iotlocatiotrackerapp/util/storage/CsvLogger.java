package com.rramsauer.iotlocatiotrackerapp.util.storage;

/* Android imports */

import android.os.Handler;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.rramsauer.iotlocatiotrackerapp.util.log.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * This class provides a service which allows to write data in a previously defined interval for a previously defined time.
 * All settings like:
 * <ul>
 *     <li>File name</li>
 *     <li>Directory</li>
 *     <li>logging interval</li>
 *     <li>number of row per file (creates a new file when reached and saves this file with the help of a file counter)</li>
 *     <li>recording time</li>
 * </ul>
 * are passed via the constructor.
 * <p>
 * Future extensions:
 * ToDo: different mode
 * ToDo: create csv with ArrayList
 * <p>
 * Attention: Pay attention to the number of lines, because android can process a maximum of 2GB effectively.
 *
 * @param <T> Java stateful bean object typ.
 * @author Ramsauer René
 * @version V1.9
 * @implNote Attention: This class can  only work with java stateful bean objects.<p>
 * <p>
 * Attention: The logging level was defined into the Logger class.
 * This means if you use this class in a other project you must impl the logging level
 * or clear cmd-logging-code from this code<p>
 * <p>
 * Furthermore, this class requires the following Gradler imports:
 * <ul>
 *     <li>implementation 'com.opencsv:opencsv:5.7.1'</li>
 * </ul>
 * <p>
 * The follow permission in Manifest.xml:
 * <ul>
 *     <li>uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"</li>
 *     <li>uses-permission android:name="android.permission.WAKE_LOCK"</li>
 * </ul>
 * <p>
 * And need the following Classes:
 * <ul>
 *     <li>com.rramsauer.iotlocatiotrackerapp.util.conv.StringConverter</li>
 *     <li>com.rramsauer.iotlocatiotrackerapp.util.storage.FileManagementHelper</li>
 * </ul>
 * @see com.rramsauer.iotlocatiotrackerapp.util.str.StringConverter
 * @see com.rramsauer.iotlocatiotrackerapp.util.storage.FileManagementHelper
 */
public class CsvLogger<T> {

    public static final String FILE_EXTENSION = "csv";
    public static final double CSV_MAX_SIZE_ROW_BYTE = 1.0578947368;
    public static final int CSV_NUMBER_COLUM = 23;
    /* DEBUG */
    public static String LOG_TAG_CSV = "CSV_WRITER";
    private final Handler loggingHandler = new Handler();
    long startTime;
    long actualTime;
    double durationInSecond;
    /* File */
    private String csvFileName;
    private String csvDirectoryName;
    private File csvFile;
    private int fileCounter;
    private int rowCounter;
    private int numberOfRowPerFile;
    /* CSV Writer and Bean*/
    private Writer writer;
    private StatefulBeanToCsv<T> beanToCsv;
    /* Thread */
    private Runnable loggingRunnable;
    private int loggingInterval;
    /* Timer */
    private int recordingTime;
    /* Context */
    private boolean isRun = false;
    /* Interface */
    private CsvLoggerWriter<T> l;

    /**
     * CONSTRUCTOR
     * initializes a CsvLogger with a context. A CompassAssistant initialized with this
     * constructor will not point to the geographic, but to the magnetic north.
     *
     * @param l                  The location to which the CSVLogger should refer its pointing. (this)
     * @param csvDirectoryName   Passed the directory name of your csv-file.
     * @param csvFileName        Passed the csv-file name.
     * @param loggingInterval    Define the loggingInterval.
     * @param numberOfRowPerFile Define the max number of row per file.
     * @param recordingTime      Define the recording time of csvLogger
     * @author Ramsauer René
     * @version V1.6
     */
    public CsvLogger(CsvLoggerWriter<T> l, String csvDirectoryName, String csvFileName, int loggingInterval, int numberOfRowPerFile, int recordingTime) {
        this.l = l;
        this.csvDirectoryName = csvDirectoryName;
        this.csvFileName = csvFileName;
        this.loggingInterval = loggingInterval;
        this.numberOfRowPerFile = numberOfRowPerFile;
        this.recordingTime = recordingTime;
    }

    /**
     * This function calculate the size of a csv-file.
     * Attention the size may change depending on the size of the row.
     * Determine the data size of a row and add the value to CSV_MAX_SIZE_ROW_BYTE.
     *
     * @param rows Passe the the count of rows per file.
     * @return Return the byte sice of the calc. file.
     * @author Ramsauer René
     * @version V1.9
     * @implNote Ex.: csvSizeCalculator(1500)
     * @value CSV_MAX_SIZE_ROW_BYTE
     * @value CSV_NUMBER_COLUM
     */
    public static double csvSizeCalculator(int rows) {
        return CSV_MAX_SIZE_ROW_BYTE * CSV_NUMBER_COLUM * rows;
    }

    /**
     * This function calculate the max row for a limit file size.
     * Attention: Pay attention to the number of lines,
     * because android can process a maximum of 2GB effectively.
     *
     * @param maxSizeOfByte Passe size if csv-file.
     * @return Returns the maximum number of lines.
     * @author Ramsauer René
     * @version V1.9
     * @implNote Ex.: getMaxRow(1500)
     * @value CSV_MAX_SIZE_ROW_BYTE
     * @value CSV_NUMBER_COLUM
     */
    public static int getMaxRow(int maxSizeOfByte) {
        return (int) (maxSizeOfByte / (CSV_MAX_SIZE_ROW_BYTE * CSV_NUMBER_COLUM));
    }

    /**
     * This function calculates the maximum number of lines for a max 2GB csv-file.
     * Attention: Pay attention to the number of lines,
     * because android can process a maximum of 2GB effectively.
     *
     * @return Returns the maximum number of lines.
     * @author Ramsauer René
     * @version V1.9
     * @implNote Ex.: csvSizeCalculator(1500)
     * @link {@value CSV_MAX_SIZE_ROW_BYTE}
     * @value CSV_MAX_SIZE_ROW_BYTE
     * @value CSV_NUMBER_COLUM
     */
    public static int getMaxRow() {
        return (int) (2e+9 / (CSV_MAX_SIZE_ROW_BYTE * CSV_NUMBER_COLUM));
    }

    /**
     * This function init and start the CSV-Logger
     *
     * @author Ramsauer René
     * @version V1.1
     * @implNote Ex.: start()
     * @see com.rramsauer.iotlocatiotrackerapp.util.storage.FileManagementHelper
     * @see com.rramsauer.iotlocatiotrackerapp.util.str.StringConverter
     */
    public void start() {
        Logger.v(LOG_TAG_CSV, "CSV-Logger was started. ...", Thread.currentThread().getStackTrace()[2]);
        this.csvFile = FileManagementHelper.newCounterFile(csvDirectoryName, csvFileName, FILE_EXTENSION, fileCounter = 0);
        if (csvFile == null) {
            Logger.e(LOG_TAG_CSV, "Create File object was not successful. Stop CSV-Logger ...", Thread.currentThread().getStackTrace()[2]);
            this.stop();
        } else {
            if (!intWriterAndStatefulBeanToCsv(csvFile)) {
                Logger.e(LOG_TAG_CSV, "init File-Writer ore StatefulBeanToCsv was not successful. Stop CSV-Logger ...", Thread.currentThread().getStackTrace()[2]);
                this.stop();
            } else {
                l.onCsvLoggerStarted();
                loggingHandler.postDelayed(loggingRunnable, loggingInterval);
                Logger.e(LOG_TAG_CSV, "TEST TEST", Thread.currentThread().getStackTrace()[2]);
                this.mainDataThread();
            }
        }
    }

    /**
     * This function stop the CSV-Logger and close and clear the FileWriter
     *
     * @author Ramsauer René
     * @version V1.1
     * @implNote Ex.: stop()
     * @see com.rramsauer.iotlocatiotrackerapp.util.str.StringConverter
     */
    public void stop() {
        l.onCsvLoggerStopped();
        isRun = false;
        Logger.v(LOG_TAG_CSV, "CSV-Logger was stopped. ...", Thread.currentThread().getStackTrace()[2]);
        loggingHandler.removeCallbacks(loggingRunnable);
        this.closWriterAndStatefulBeanToCsv();
    }

    /**
     * This function manage the CsvLogging-Process
     *
     * @author Ramsauer René
     * @version V1.9
     * @implNote Ex.: mainDataThread()
     */
    private void mainDataThread() {
        startTime = System.nanoTime();
        rowCounter = 0;
        fileCounter = 1;
        isRun = true;
        if (loggingRunnable == null) {
            loggingRunnable = () -> {
                /* START THREAD*/
                actualTime = System.nanoTime();
                durationInSecond = (actualTime - startTime) / 1e+9;
                if (((int) durationInSecond) <= recordingTime) {
                    if ((rowCounter % numberOfRowPerFile) == 0 && rowCounter != 0) {
                        fileCounter++;
                        if (!closWriterAndStatefulBeanToCsv()) {
                            isRun = false;
                            stop();
                        }
                        if (!intWriterAndStatefulBeanToCsv(FileManagementHelper.newCounterFile(csvDirectoryName, csvFileName, FILE_EXTENSION, fileCounter))) {
                            isRun = false;
                            stop();
                        }
                    }
                    rowCounter++;
                    addRowToCSV(l.onRunCsvLogger());
                } else {
                    isRun = false;
                    stop();
                }
                if (isRun) loggingHandler.postDelayed(loggingRunnable, loggingInterval);
                /* END THREAD*/
            };
            if (isRun) loggingHandler.postDelayed(loggingRunnable, loggingInterval);
        }
    }

    /**
     * This function init FileWriter and StatefulBeanToCsv
     *
     * @param file Pass the File with should be initialized.
     * @return Return true if init was success | Returns false if init was not success
     * @author Ramsauer René
     * @version V1.9
     * @implNote Ex.: intWriterAndStatefulBeanToCsv(file)
     * @see com.rramsauer.iotlocatiotrackerapp.util.str.StringConverter
     */
    private boolean intWriterAndStatefulBeanToCsv(File file) {
        Logger.v(LOG_TAG_CSV, "Initialization of FileWriter and StatefulBeanToCsvBuilder.", Thread.currentThread().getStackTrace()[2]);
        try {
            this.writer = new FileWriter(file.getPath());
            this.beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withSeparator(',')
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            Logger.v(LOG_TAG_CSV, "FileWriter and StatefulBeanToCsvBuilder was successful init.", Thread.currentThread().getStackTrace()[2]);
        } catch (IOException e) {
            Logger.e(LOG_TAG_CSV, "IOException at create FileWriter!", Thread.currentThread().getStackTrace()[2]);
            e.printStackTrace();
            this.stop();
            return false;
        }
        return true;
    }

    /**
     * This function close FileWriter and StatefulBeanToCsv
     *
     * @return Return true if init was success | Returns false if init was not success
     * @author Ramsauer René
     * @version V1.9
     * @implNote Ex.: closWriterAndStatefulBeanToCsv(file)
     * @see com.rramsauer.iotlocatiotrackerapp.util.str.StringConverter
     */
    private boolean closWriterAndStatefulBeanToCsv() {
        if (beanToCsv != null)
            beanToCsv.getCapturedExceptions();
        try {
            Logger.v(LOG_TAG_CSV, "Close CSV FileWriter...", Thread.currentThread().getStackTrace()[2]);
            writer.close();
            return true;
        } catch (IOException | NullPointerException e) {
            Logger.e(LOG_TAG_CSV, "IOException at Close CSV FileWriter", Thread.currentThread().getStackTrace()[2]);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This function close FileWriter and StatefulBeanToCsv
     *
     * @param element Passe the java-stateful-bean of a row.
     * @return Return true if init was success | Returns false if init was not success
     * @author Ramsauer René
     * @version V1.9
     * @implNote Ex.: addRowToCSV(T element)
     * @see com.rramsauer.iotlocatiotrackerapp.util.str.StringConverter
     */
    private boolean addRowToCSV(T element) {
        try {
            this.beanToCsv.write(element);
            Logger.v(LOG_TAG_CSV, "Add row to csv: " + element.toString(), Thread.currentThread().getStackTrace()[2]);
            return true;
        } catch (NullPointerException e) {
            Logger.e(LOG_TAG_CSV, "Cannot ride row to csv.", Thread.currentThread().getStackTrace()[2]);
            e.printStackTrace();
            this.stop();
            return false;
        } catch (CsvDataTypeMismatchException e) {
            Logger.e(LOG_TAG_CSV, "CsvDataTypeMismatchException at \"beanToCsv.write(element).\".", Thread.currentThread().getStackTrace()[2]);
            e.printStackTrace();
            this.stop();
            return false;
        } catch (CsvRequiredFieldEmptyException e) {
            Logger.e(LOG_TAG_CSV, "CsvRequiredFieldEmptyException at \"beanToCsv.write(element).\".", Thread.currentThread().getStackTrace()[2]);
            e.printStackTrace();
            this.stop();
            return false;
        }
    }

    /**
     * This is an interface that allows to capture and write data line by line.
     * Furthermore this interface allows to execute actions in the course of the modes (start, runn, stop).
     * Any object that initializes CsvLogger must implement this interface.
     *
     * @param <T> Java stateful bean object typ.
     * @author Ramsauer René
     * @version V1.6
     */
    public interface CsvLoggerWriter<T> {
        /**
         * is getting called when the compass was started.
         */
        void onCsvLoggerStarted();

        /**
         * is called every iteration when csv logger is running.
         * This function is used to pass the data for one line.
         * Return the data in form of a java stateful bean object.
         *
         * @return data in form of a StatefulBean. Pass da dater of a row to the CSV-Logger.
         */
        T onRunCsvLogger();

        /**
         * is getting called when the compass was stopped.
         */
        void onCsvLoggerStopped();
    }
}