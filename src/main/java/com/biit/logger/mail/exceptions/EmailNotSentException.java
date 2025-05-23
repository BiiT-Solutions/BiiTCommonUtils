package com.biit.logger.mail.exceptions;


import java.io.Serial;

public class EmailNotSentException extends Exception {
    @Serial
    private static final long serialVersionUID = 3809091821239068978L;

    public EmailNotSentException(String message) {
        super(message);
    }

    public EmailNotSentException(Class<?> clazz, String message) {
        super(clazz + ": " + message);
    }

    public EmailNotSentException(Class<?> clazz, String message, Throwable cause) {
        super(clazz + ": " + message, cause);
    }
}
