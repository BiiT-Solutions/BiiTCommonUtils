package com.biit.logger;

import org.apache.log4j.Logger;

public class BiitPoolLogger extends BiitLogger {

	private static Logger logger = Logger.getLogger(BiitPoolLogger.class);

	public static void severe(String name, Throwable e) {
		severe(logger, BiitLogger.getStackTrace(e));
	}

	public static void info(Class<?> clazz, String message) {
		info(logger, message);
	}

	public static void errorMessageNotification(Class<?> clazz, Throwable e) {
		errorMessageNotification(logger, clazz.getName(), BiitLogger.getStackTrace(e));
	}

	public static void warning(Class<?> clazz, String message) {
		warning(logger, message);
	}

	public static void errorMessageNotification(Class<?> clazz, String message) {
		errorMessageNotification(logger, clazz.getName(), message);
	}

	public static void severe(Class<?> clazz, String message) {
		severe(logger, message);
	}

	public static void debug(Class<?> clazz, String message) {
		debug(logger, clazz.getName(), message);
	}

}
