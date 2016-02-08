package com.biit.logger.configuration;

import java.util.ArrayList;
import java.util.List;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.configuration.ConfigurationReader;
import com.biit.utils.configuration.PropertiesSourceFile;
import com.biit.utils.configuration.SystemVariablePropertiesSourceFile;

/**
 * Jar applications cannot have configuration files inside JAR file, must be in
 * an external folder to allow editing.
 */
public class EmailConfigurationReader extends ConfigurationReader {
	private static final String CONFIG_FILE = "settings.conf";
	private static final String SYSTEM_VARIABLE_CONFIG = "BIIT_LOGGER_CONFIG";

	private final static String EMAIL_ENABLED_TAG = "mail.enabled";
	private final static String EMAIL_TO_TAG = "mail.to";
	private final static String EMAIL_SMTP_SERVER_TAG = "mail.smtpserver";
	private final static String EMAIL_USERNAME_TAG = "mail.username";
	private final static String EMAIL_PASSWORD_TAG = "mail.password";
	private final static String EMAIL_SENDER_TAG = "mail.sender";

	private final static String DEFAULT_EMAIL_SMTP_SERVER = "smtp.mail.com";
	private final static String DEFAULT_EMAIL_USERNAME = "noreply@email.com";
	private final static String DEFAULT_EMAIL_PASSWORD = "password";
	private final static String DEFAULT_EMAIL_SENDER = "info@biit-solutions.com";

	private static EmailConfigurationReader instance;

	private EmailConfigurationReader() {
		super();

		addProperty(EMAIL_SMTP_SERVER_TAG, DEFAULT_EMAIL_SMTP_SERVER);
		addProperty(EMAIL_USERNAME_TAG, DEFAULT_EMAIL_USERNAME);
		addProperty(EMAIL_PASSWORD_TAG, DEFAULT_EMAIL_PASSWORD);
		addProperty(EMAIL_SENDER_TAG, DEFAULT_EMAIL_SENDER);
		addProperty(EMAIL_ENABLED_TAG, false);
		addProperty(EMAIL_TO_TAG, "");

		addPropertiesSource(new PropertiesSourceFile(CONFIG_FILE));
		addPropertiesSource(new SystemVariablePropertiesSourceFile(SYSTEM_VARIABLE_CONFIG, CONFIG_FILE));

		readConfigurations();
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
		return getPropertyLogException(EMAIL_SMTP_SERVER_TAG);
	}

	public synchronized String getEmailUser() {
		return getPropertyLogException(EMAIL_USERNAME_TAG);
	}

	public synchronized String getEmailPassword() {
		return getPropertyLogException(EMAIL_PASSWORD_TAG);
	}

	public synchronized String getEmailSender() {
		return getPropertyLogException(EMAIL_SENDER_TAG);
	}

	public boolean isEmailEnabled() {
		try {
			return Boolean.parseBoolean(getPropertyLogException(EMAIL_ENABLED_TAG));
		} catch (Exception e) {
			return false;
		}
	}

	public List<String> getEmailTo() {
		String emailToListCommaSeparated = getPropertyLogException(EMAIL_TO_TAG);
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
