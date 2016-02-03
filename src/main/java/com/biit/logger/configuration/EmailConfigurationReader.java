package com.biit.logger.configuration;

import java.util.ArrayList;
import java.util.List;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.configuration.ConfigurationReader;

/**
 * Jar applications cannot have configuration files inside JAR file, must be in
 * an external folder to allow editing.
 */
public class EmailConfigurationReader extends ConfigurationReader {
	private final String EMAIL_ENABLED = "mail.enabled";
	private final String EMAIL_TO = "mail.to";
	private final String EMAIL_SMTP_SERVER_TAG = "mail.smtpserver";
	private final String EMAIL_USERNAME_TAG = "mail.username";
	private final String EMAIL_PASSWORD_TAG = "mail.password";
	private final String EMAIL_SENDER_TAG = "mail.sender";

	private final String DEFAULT_EMAIL_SMTP_SERVER = "smtp.mail.com";
	private final String DEFAULT_EMAIL_USERNAME = "noreply@email.com";
	private final String DEFAULT_EMAIL_PASSWORD = "password";
	private final String DEFAULT_EMAIL_SENDER = "info@biit-solutions.com";

	private static EmailConfigurationReader instance;

	private EmailConfigurationReader() {
		super();

		addProperty(EMAIL_SMTP_SERVER_TAG, DEFAULT_EMAIL_SMTP_SERVER);
		addProperty(EMAIL_USERNAME_TAG, DEFAULT_EMAIL_USERNAME);
		addProperty(EMAIL_PASSWORD_TAG, DEFAULT_EMAIL_PASSWORD);
		addProperty(EMAIL_SENDER_TAG, DEFAULT_EMAIL_SENDER);
		addProperty(EMAIL_ENABLED, false);
		addProperty(EMAIL_TO, "");
	}

	public static EmailConfigurationReader getInstance() {
		if (instance == null) {
			synchronized (EmailConfigurationReader.class) {
				if (instance == null) {
					instance = new EmailConfigurationReader();
				}
			}
		}
		return instance;
	}

	private String getPropertyLogException(String propertyId) {
		try {
			return getProperty(propertyId);
		} catch (Exception e) {
			BiitCommonLogger.errorMessageNotification(this.getClass(), e.getMessage());
			return null;
		}
	}

	public synchronized String getSmtpServer() {
		return getPropertyLogException(EMAIL_USERNAME_TAG);
	}

	public synchronized String getEmailUser() {
		return getPropertyLogException(DEFAULT_EMAIL_USERNAME);
	}

	public synchronized String getEmailPassword() {
		return getPropertyLogException(DEFAULT_EMAIL_PASSWORD);
	}

	public synchronized String getEmailSender() {
		return getPropertyLogException(DEFAULT_EMAIL_SENDER);
	}

	public boolean isEmailEnabled() {
		try {
			return Boolean.parseBoolean(getPropertyLogException(EMAIL_SENDER_TAG));
		} catch (Exception e) {
			return false;
		}
	}

	public List<String> getEmailTo() {
		String emailToListCommaSeparated = getPropertyLogException(EMAIL_TO);
		List<String> emailToList = new ArrayList<>();
		if (emailToListCommaSeparated != null && emailToListCommaSeparated.length() > 0) {
			String[] users = emailToListCommaSeparated.split(",");
			for (String user : users) {
				emailToList.add(user.trim());
			}
		}
		return emailToList;
	}

}
