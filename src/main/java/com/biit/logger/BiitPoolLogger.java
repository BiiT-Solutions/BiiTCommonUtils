package com.biit.logger;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiitPoolLogger extends BiitLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(BiitPoolLogger.class);

    public static void severe(String name, Throwable e) {
        severe(LOGGER, BiitLogger.getStackTrace(e));
    }

    public static void info(Class<?> clazz, String message) {
        info(LOGGER, message);
    }

    public static void errorMessageNotification(Class<?> clazz, Throwable e) {
        errorMessageNotification(LOGGER, clazz.getName(), BiitLogger.getStackTrace(e));
    }

    public static void warning(Class<?> clazz, String message) {
        warning(LOGGER, message);
    }

    public static void errorMessageNotification(Class<?> clazz, String message) {
        errorMessageNotification(LOGGER, clazz.getName(), message);
    }

    public static void severe(Class<?> clazz, String message) {
        severe(LOGGER, message);
    }

    public static void debug(Class<?> clazz, String message) {
        debug(LOGGER, clazz.getName(), message);
    }

}
