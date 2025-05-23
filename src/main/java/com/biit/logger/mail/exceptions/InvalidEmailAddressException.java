package com.biit.logger.mail.exceptions;


public class InvalidEmailAddressException extends Exception {
    private static final long serialVersionUID = 3182975812992201906L;

    public InvalidEmailAddressException(String message) {
        super(message);
    }


    public InvalidEmailAddressException(Class<?> clazz, String message) {
        super(clazz + ": " + message);
    }

    public InvalidEmailAddressException(Class<?> clazz, String message, Throwable cause) {
        super(clazz + ": " + message, cause);
    }
}
