package com.biit.logger;

import org.apache.log4j.Logger;

public class BiitCommonLogger {

	private static Logger logger = Logger.getLogger(BiitCommonLogger.class);

	public static void severe(String name, Throwable e) {
		LoggerUtils.severe(logger, LoggerUtils.getStackTrace(e));
	}

	public static void info(Class<?> clazz, String message) {
		LoggerUtils.info(logger, message);
	}

	public static void errorMessageNotification(Class<?> clazz,	Throwable e) {
		LoggerUtils.errorMessageNotification(logger, clazz.getName(), LoggerUtils.getStackTrace(e));	
	}

	public static void warning(Class<?> clazz, String message) {
		LoggerUtils.warning(logger, message);
	}

	public static void errorMessageNotification(Class<?> clazz, String message) {
		LoggerUtils.errorMessageNotification(logger, clazz.getName(), message);
	}

	public static void severe(Class<?> clazz, String message) {
		LoggerUtils.severe(logger, message);
	}

	public static void debug(Class<?> clazz, String message) {
		LoggerUtils.debug(logger, clazz.getName(),message);
	}
	
}
