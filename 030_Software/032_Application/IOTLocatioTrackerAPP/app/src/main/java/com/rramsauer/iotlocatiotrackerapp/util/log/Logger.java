package com.rramsauer.iotlocatiotrackerapp.util.log;

import android.util.Log;

import androidx.annotation.NonNull;

import com.rramsauer.iotlocatiotrackerapp.util.str.StringConverter;

import java.util.Date;

/**
 * The Logger class provides a simple way to log messages with different log levels.
 * The log level is set by changing the value of the LOGLEVEL constant.
 * The different log levels are ERROR, INFORMATION, WARNING, VERBOSE, and DEBUG.
 * Each log level has two versions of the method, one that takes only a tag and a message and one that also takes a StackTraceElement parameter
 * that provides information about the location of the log statement in the code.
 * This class extends the log information of the class Log.
 *
 * @author Ramsauer René
 */
public class Logger {
    /**
     * Define your LogLevel
     */
    public static int LOGLEVEL = 5;

    public static boolean ERROR = LOGLEVEL > 0;
    public static boolean INFORMATION = LOGLEVEL > 1;
    public static boolean WARNING = LOGLEVEL > 2;
    public static boolean VERBOSE = LOGLEVEL > 4;
    public static boolean DEBUG = LOGLEVEL > 5;

    /**
     * Logs a verbose message with the given tag and message.
     * The current time in the format yyyy-MM-dd hh:mm:ss is added to the message.
     *
     * @param tag The tag to use for the log statement
     * @param msg The message to log
     * @author Ramsauer René
     */
    public static void v(String tag, String msg) {
        if (VERBOSE) {
            Log.v(tag, "<" + StringConverter.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss") + "> " + msg);
        }
    }

    /**
     * Logs a verbose message with the given tag, message, and StackTraceElement.
     * The current time in the format yyyy-MM-dd hh:mm:ss is added to the message, along with the
     * class name, method name, and line number provided by the StackTraceElement.
     *
     * @param tag The tag to use for the log statement
     * @param msg The message to log
     * @param ste The StackTraceElement that provides information about the location of the log statement in the code
     * @author Ramsauer René
     */
    public static void v(String tag, String msg, @NonNull StackTraceElement ste) {
        if (VERBOSE) {
            Log.v(tag, "<" + StringConverter.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss") + "> " + msg + "  [" + ste.getClassName() + "|" + ste.getMethodName() + "|" + ste.getLineNumber() + "]");
        }
    }

    /**
     * Logs a debug message with the given tag and message.
     * The current time in the format yyyy-MM-dd hh:mm:ss is added to the message.
     *
     * @param tag The tag to use for the log statement
     * @param msg The message to log
     * @author Ramsauer René
     */
    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, "<" + StringConverter.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss") + "> " + msg);
        }
    }

    /**
     * Logs a debug message with the given tag, message, and StackTraceElement.
     * The current time in the format yyyy-MM-dd hh:mm:ss is added to the message,
     * along with the class name, method name, and line number provided by the StackTraceElement.
     *
     * @param tag The tag to use for the log statement
     * @param msg The message to log
     * @param ste The StackTraceElement that provides information about the location of the log statement in the code
     * @author Ramsauer René
     */
    public static void d(String tag, String msg, @NonNull StackTraceElement ste) {
        if (DEBUG) {
            Log.d(tag, "<" + StringConverter.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss") + "> " + msg + "  [" + ste.getClassName() + "|" + ste.getMethodName() + "|" + ste.getLineNumber() + "]");
        }
    }

    /**
     * Logs an information message with the given tag and message.
     * The current time in the format yyyy-MM-dd hh:mm:ss is added to the message.
     *
     * @param tag the tag to use for the log message
     * @param msg the message to log
     */
    public static void i(String tag, String msg) {
        if (INFORMATION) {
            Log.v(tag, "<" + StringConverter.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss") + "> " + msg);
        }
    }

    /**
     * Logs an information message with the given tag, message, and stack trace element.
     * The current time in the format yyyy-MM-dd hh:mm:ss is added to the message,
     * along with the class name, method name, and line number provided by the StackTraceElement.
     *
     * @param tag the tag to use for the log message
     * @param msg the message to log
     * @param ste the stack trace element to include in the log message
     * @author Ramsauer René
     */
    public static void i(String tag, String msg, @NonNull StackTraceElement ste) {
        if (INFORMATION) {
            Log.i(tag, "<" + StringConverter.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss") + "> " + msg + "  [" + ste.getClassName() + "|" + ste.getMethodName() + "|" + ste.getLineNumber() + "]");
        }
    }

    /**
     * Logs a warning message with the given tag and message.
     * The current time in the format yyyy-MM-dd hh:mm:ss is added to the message.
     *
     * @param tag the tag to use for the log message
     * @param msg the message to log
     * @author Ramsauer René
     */
    public static void w(String tag, String msg) {
        if (WARNING) {
            Log.v(tag, "<" + StringConverter.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss") + "> " + msg);
        }
    }

    /**
     * Logs a warning message with the given tag, message, and stack trace element.
     * The current time in the format yyyy-MM-dd hh:mm:ss is added to the message,
     * along with the class name, method name, and line number provided by the StackTraceElement.
     *
     * @param tag the tag to use for the log message
     * @param msg the message to log
     * @param ste the stack trace element to include in the log message
     * @author Ramsauer René
     */
    public static void w(String tag, String msg, @NonNull StackTraceElement ste) {
        if (WARNING) {
            Log.v(tag, "<" + StringConverter.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss") + "> " + msg + "  [" + ste.getClassName() + "|" + ste.getMethodName() + "|" + ste.getLineNumber() + "]");
        }
    }

    /**
     * Logs an error message with the given tag and message.
     * The current time in the format yyyy-MM-dd hh:mm:ss is added to the message.
     *
     * @param tag the tag to use for the log message
     * @param msg the message to log
     * @author Ramsauer René
     */
    public static void e(String tag, String msg) {
        if (ERROR) {
            Log.v(tag, "<" + StringConverter.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss") + "> " + msg);
        }
    }

    /**
     * Logs an error message with the given tag, message, and stack trace element.
     * The current time in the format yyyy-MM-dd hh:mm:ss is added to the message,
     * along with the class name, method name, and line number provided by the StackTraceElement.
     *
     * @param tag the tag to use for the log message
     * @param msg the message to log
     * @param ste the stack trace element to include in the log message
     * @author Ramsauer René
     */
    public static void e(String tag, String msg, @NonNull StackTraceElement ste) {
        if (ERROR) {
            Log.v(tag, "<" + StringConverter.dateToString(new Date(), "yyyy-MM-dd hh:mm:ss") + "> " + msg + "  [" + ste.getClassName() + "|" + ste.getMethodName() + "|" + ste.getLineNumber() + "]");
        }
    }


}
