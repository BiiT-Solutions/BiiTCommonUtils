package com.biit.logger;

import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public abstract class BasicLogger extends BiitLogger {

    protected BasicLogger() {
        super();
    }

    public static void warning(Logger logger, String messageTemplate, Object... args) {
        logger.warn(messageTemplate, args);
    }

    public static void warning(Logger logger, String className, String messageTemplate, Object... arguments) {
        if (logger.isWarnEnabled()) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] != null) {
                    arguments[i] = arguments[i].toString().replaceAll("[\n\r\t]", "_");
                }
            }
            final String templateWithClass = className + ": " + messageTemplate;
            logger.warn(templateWithClass.replaceAll("[\n\r]", "_"), arguments);
        }
    }

    public static void info(Logger logger, String messageTemplate, Object... arguments) {
        if (logger.isInfoEnabled()) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] != null) {
                    arguments[i] = arguments[i].toString().replaceAll("[\n\r\t]", "_");
                }
            }
            logger.info(messageTemplate.replaceAll("[\n\r]", "_"), arguments);
        }
    }

    public static void info(Logger logger, String className, String messageTemplate, Object... args) {
        info(logger, className + ": " + messageTemplate, args);
    }

    public static void debug(Logger logger, String messageTemplate, Object... arguments) {
        if (logger.isDebugEnabled()) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] != null) {
                    arguments[i] = arguments[i].toString().replaceAll("[\n\r\t]", "_");
                }
            }
            logger.debug(messageTemplate.replaceAll("[\n\r]", "_"), arguments);
        }
    }

    public static void debug(Logger logger, String className, String messageTemplate, Object... arguments) {
        if (logger.isDebugEnabled()) {
            // Replace pattern-breaking characters
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] != null) {
                    arguments[i] = arguments[i].toString().replaceAll("[\n\r\t]", "_");
                }
            }
            logger.debug(String.format("%s: %s", className, messageTemplate), arguments); //NOSONAR
        }
    }

    protected static void severe(Logger logger, String messageTemplate, Object... arguments) {
        if (logger.isErrorEnabled()) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] != null) {
                    arguments[i] = arguments[i].toString().replaceAll("[\n\r\t]", "_");
                }
            }
            logger.error(messageTemplate.replaceAll("[\n\r]", "_"), arguments);
        }
    }

    public static void severe(Logger logger, String className, String messageTemplate, Object... args) {
        severe(logger, className + ": " + messageTemplate, args);
    }

    public static void errorMessageNotification(Logger logger, String messageTemplate, Object... args) {
        severe(logger, messageTemplate, args);
    }

    public static void errorMessageNotification(Logger logger, String className, String messageTemplate, Object... args) {
        severe(logger, className, messageTemplate, args);
    }

    public static void errorMessageNotification(Logger logger, String className, Throwable throwable) {
        logger.error("Exception on class {}:\n", className, throwable);
    }

    public static String getStackTrace(Throwable throwable) {
        final Writer writer = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }
}
