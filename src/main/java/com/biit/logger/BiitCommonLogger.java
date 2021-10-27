package com.biit.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BiitCommonLogger extends BiitLogger {

    private static Logger logger = LoggerFactory.getLogger(BiitCommonLogger.class);

    public static void severe(String name, Throwable e) {
        severe(logger, BiitLogger.getStackTrace(e));
    }

    public static void info(Class<?> clazz, String message) {
        info(logger, clazz.getName(), message);
    }

    public static void errorMessageNotification(Class<?> clazz, Throwable e) {
        errorMessageNotification(logger, clazz.getName(), BiitLogger.getStackTrace(e));
    }

    public static void warning(Class<?> clazz, String message) {
        warning(logger, clazz.getName(), message);
    }

    public static void errorMessageNotification(Class<?> clazz, String message) {
        errorMessageNotification(logger, clazz.getName(), message);
    }

    public static void severe(Class<?> clazz, String message) {
        severe(logger, clazz.getName(), message);
    }

    public static void debug(Class<?> clazz, String message) {
        debug(logger, clazz.getName(), message);
    }

}
