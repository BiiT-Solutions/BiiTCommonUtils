package com.biit.logger;

import com.biit.logger.configuration.EmailConfigurationReader;
import com.biit.logger.mail.ErrorMailGeneration;
import com.biit.logger.mail.SendEmail;
import com.biit.logger.mail.exceptions.EmailNotSentException;
import com.biit.logger.mail.exceptions.InvalidEmailAddressException;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public abstract class BiitLogger {

    /**
     * Shows not critical errors. I.e. Email address not found, permissions not
     * allowed for this user, ...
     *
     * @param message
     */
    public static void warning(Logger logger, String message) {
        logger.warn(message);
    }

    /**
     * Shows not critical errors. I.e. Email address not found, permissions not
     * allowed for this user, ...
     *
     * @param message
     */
    public static void warning(Logger logger, String className, String message) {
        logger.warn(className + ": " + message);
    }

    /**
     * Events that have business meaning (i.e. creating category, deleting form,
     * ...). To follow user actions.
     *
     * @param message
     */
    public static void info(Logger logger, String message) {
        logger.info(message);
    }

    /**
     * Events that have business meaning (i.e. creating category, deleting form,
     * ...). To follow user actions.
     *
     * @param message
     */
    public static void info(Logger logger, String className, String message) {
        info(logger, className + ": " + message);
    }

    /**
     * For following the trace of the execution. I.e. Knowing if the application
     * access to a method, opening database connection, etc.
     *
     * @param message
     */
    public static void debug(Logger logger, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * For following the trace of the execution. I.e. Knowing if the application
     * access to a method, opening database connection, etc.
     *
     * @param message
     */
    public static void debug(Logger logger, String className, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(className + ": " + message);
        }
    }

    /**
     * To log any not expected error that can cause application malfunctions.
     * I.e. couldn't open database connection, etc..
     *
     * @param message
     */
    protected static void severe(Logger logger, String message) {
        logger.error(message);
    }

    /**
     * To log any not expected error that can cause application malfunctions.
     *
     * @param message
     */
    public static void severe(Logger logger, String className, String message) {
        severe(logger, className + ": " + message);
    }

    /**
     * Logs an error and send an email to the email configured in settings.conf
     * file.
     *
     * @param className
     * @param error
     */
    public static void errorMessageNotification(Logger logger, String className, String error) {
        severe(logger, className, error);
        sendByEmail(logger, className, error);
    }

    public static void sendNotification(Logger logger, String className, String message) {
        sendByEmail(logger, className, message);
    }

    private static void sendByEmail(Logger logger, String className, String message) {
        try {
            if (EmailConfigurationReader.getInstance().isEmailEnabled()) {
                try {
                    SendEmail.sendEmail(EmailConfigurationReader.getInstance().getEmailTo(),
                            ErrorMailGeneration.getSubject(),
                            ErrorMailGeneration.getHtmlMailContent(className, message));
                } catch (EmailNotSentException | InvalidEmailAddressException e) {
                    severe(logger, BiitLogger.class.getName(), getStackTrace(e));
                }
            }
        } catch (NullPointerException npe) {
            warning(logger, BiitLogger.class.getName() + ": email configuration is not set!");
        }
    }

    public static String getStackTrace(Throwable throwable) {
        final Writer writer = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }

    /**
     * Shows not critical errors. I.e. Email address not found, permissions not
     * allowed for this user, ...
     *
     * @param messageTemplate string with static text as template.
     * @param arguments       parameters to fill up the template
     */
    public static void warning(Logger logger, String messageTemplate, Object... arguments) {
        logger.warn(messageTemplate, arguments);
    }

    /**
     * Shows not critical errors. I.e. Email address not found, permissions not
     * allowed for this user, ...
     *
     * @param logger          the Logger.
     * @param className       the class to log.
     * @param messageTemplate string with static text as template.
     * @param arguments       parameters to fill up the template
     */
    public static void warning(Logger logger, String className, String messageTemplate, Object... arguments) {
        logger.warn(className + ": " + messageTemplate, arguments);
    }

    /**
     * Events that have business meaning (i.e. creating category, deleting form,
     * ...). To follow user actions.
     *
     * @param messageTemplate string with static text as template.
     * @param arguments       parameters to fill up the template
     */
    public static void info(Logger logger, String messageTemplate, Object... arguments) {
        logger.info(messageTemplate, arguments);
    }

    /**
     * Events that have business meaning (i.e. creating category, deleting form,
     * ...). To follow user actions.
     * <p>
     *
     * @param logger          the Logger.
     * @param className       the class to log.
     * @param messageTemplate string with static text as template.
     * @param arguments       parameters to fill up the template
     */
    public static void info(Logger logger, String className, String messageTemplate, Object... arguments) {
        info(logger, className + ": " + messageTemplate, arguments);
    }

    /**
     * For following the trace of the execution. I.e. Knowing if the application
     * access to a method, opening database connection, etc.
     *
     * @param messageTemplate string with static text as template.
     * @param arguments       parameters to fill up the template
     */
    public static void debug(Logger logger, String messageTemplate, Object... arguments) {
        if (logger.isDebugEnabled()) {
            logger.debug(messageTemplate, arguments);
        }
    }

    /**
     * For following the trace of the execution. I.e. Knowing if the application
     * access to a method, opening database connection, etc.
     *
     * @param logger          the Logger.
     * @param className       the class to log.
     * @param messageTemplate string with static text as template.
     * @param arguments       parameters to fill up the template
     */
    public static void debug(Logger logger, String className, String messageTemplate, Object... arguments) {
        if (logger.isDebugEnabled()) {
            logger.debug(className + ": " + messageTemplate, arguments);
        }
    }

    /**
     * To log any not expected error that can cause application malfunctions.
     * I.e. couldn't open database connection, etc..
     *
     * @param messageTemplate string with static text as template.
     * @param arguments       parameters to fill up the template
     */
    protected static void severe(Logger logger, String messageTemplate, Object... arguments) {
        logger.error(messageTemplate, arguments);
    }

    /**
     * To log any not expected error that can cause application malfunctions.
     *
     * @param logger          the Logger.
     * @param className       the class to log.
     * @param messageTemplate string with static text as template.
     * @param arguments       parameters to fill up the template
     */
    public static void severe(Logger logger, String className, String messageTemplate, Object... arguments) {
        severe(logger, className + ": " + messageTemplate, arguments);
    }

    /**
     * Logs an error and send an email to the email configured in settings.conf
     * file.
     *
     * @param logger          the Logger.
     * @param className       the class to log.
     * @param messageTemplate string with static text as template.
     * @param arguments       parameters to fill up the template
     */
    public static void errorMessageNotification(Logger logger, String className, String messageTemplate, Object... arguments) {
        severe(logger, className, messageTemplate, arguments);
    }

    public static void errorMessageNotification(Logger logger, String className, Throwable throwable) {
        logger.error("Exception on class {}:\n", className, throwable);
    }
}
