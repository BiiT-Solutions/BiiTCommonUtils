package com.biit.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.Logger;

import com.biit.logger.configuration.EmailConfigurationReader;
import com.biit.logger.mail.ErrorMailGeneration;
import com.biit.logger.mail.SendEmail;
import com.biit.logger.mail.exceptions.EmailNotSentException;
import com.biit.logger.mail.exceptions.InvalidEmailAddressException;

/**
 * Defines basic log behavior. Uses log4j.properties. For change the name of the logger, create a class that extends
 * this class with this code: static { setLogger(Logger.getLogger(new Object() { }.getClass().getEnclosingClass())); }
 */
public abstract class BiitLogger {
	private static Logger logger;

	public static Logger getLogger() {
		return logger;
	}

	protected static void setLogger(Logger newLogger) {
		logger = newLogger;
	}

	/**
	 * Events that have business meaning (i.e. creating category, deleting form, ...). To follow user actions.
	 * 
	 * @param message
	 */
	protected static void info(String message) {
		logger.info(message);
	}

	/**
	 * Events that have business meaning (i.e. creating category, deleting form, ...). To follow user actions.
	 */
	public static void info(String className, String message) {
		info(className + ": " + message);
	}

	/**
	 * Shows not critical errors. I.e. Email address not found, permissions not allowed for this user, ...
	 * 
	 * @param message
	 */
	protected static void warning(String message) {
		logger.warn(message);
	}

	/**
	 * Shows not critical errors. I.e. Email address not found, permissions not allowed for this user, ...
	 * 
	 * @param message
	 */
	public static void warning(String className, String message) {
		warning(className + ": " + message);
	}

	/**
	 * For following the trace of the execution. I.e. Knowing if the application access to a method, opening database
	 * connection, etc.
	 * 
	 * @param message
	 */
	protected static void debug(String message) {
		if (isDebugEnabled()) {
			logger.debug(message);
		}
	}

	/**
	 * For following the trace of the execution. I.e. Knowing if the application access to a method, opening database
	 * connection, etc.
	 */
	public static void debug(String className, String message) {
		debug(className + ": " + message);
	}

	/**
	 * To log any not expected error that can cause application malfuncionality. I.e. couldn't open database connection,
	 * etc..
	 * 
	 * @param message
	 */
	protected static void severe(String message) {
		logger.error(message);
	}

	/**
	 * To log any not expected error that can cause application malfuncionality.
	 * 
	 * @param message
	 */
	public static void severe(String className, String message) {
		severe(className + ": " + message);
	}

	/**
	 * Used for debugging when accessing to a method.
	 * 
	 * @param className
	 * @param method
	 */
	public static void entering(String className, String method) {
		debug(className, "ENTRY (" + method + ")");
	}

	/**
	 * Used for debugging when exiting from a method.
	 * 
	 * @param className
	 * @param method
	 */
	public static void exiting(String className, String method) {
		debug(className, "RETURN (" + method + ")");
	}

	/**
	 * To log java exceptions and log also the stack trace. If enabled, also can send an email to the administrator to
	 * alert of the error.
	 * 
	 * @param className
	 * @param throwable
	 */
	public static void errorMessage(String className, Throwable throwable) {
		String error = getStackTrace(throwable);
		errorMessage(className, error);
	}

	public static void errorMessage(String className, String error) {
		severe(className, error);
		sendByEmail(className, error);
	}

	private static void sendByEmail(String className, String throwable) {
		if (EmailConfigurationReader.getInstance().isEmailEnabled()) {
			try {
				SendEmail.sendEmail(EmailConfigurationReader.getInstance().getEmailToList(),
						ErrorMailGeneration.getSubject(), ErrorMailGeneration.getHtmlMailContent(className, throwable));
			} catch (EmailNotSentException | InvalidEmailAddressException e) {
				severe(BiitLogger.class.getName(), getStackTrace(e));
			}
		}
	}

	public static String getStackTrace(Throwable throwable) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		throwable.printStackTrace(printWriter);
		return writer.toString();
	}

	public static boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}
}
