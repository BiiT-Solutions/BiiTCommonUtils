package com.biit.logger.mail.exceptions;


public class InvalidEmailAddressException extends Exception {
	private static final long serialVersionUID = 3182975812992201906L;

	public InvalidEmailAddressException(String message) {
		super(message);
	}
}
