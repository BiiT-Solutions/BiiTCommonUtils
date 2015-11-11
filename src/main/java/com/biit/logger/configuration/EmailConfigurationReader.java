package com.biit.logger.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.file.PropertiesFile;

/**
 * Jar applications cannot have configuration files inside JAR file, must be in an external folder to allow editing.
 */
public class EmailConfigurationReader {
	private final String CONFIG_FILE = "settings.conf";

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

	private boolean emailEnabled = false;
	private String smtpServer;
	private String emailUser;
	private String emailPassword;
	private String emailSender;
	private List<String> emailToList;

	private static EmailConfigurationReader instance;

	private EmailConfigurationReader() {
		readConfig();
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

	/**
	 * Read database config from resource and update default connection parameters.
	 */
	private void readConfig() {
		Properties prop = new Properties();
		String emailToListCommaSeparated = "";
		try {
			prop = PropertiesFile.load(CONFIG_FILE);
			smtpServer = prop.getProperty(EMAIL_SMTP_SERVER_TAG);
			emailUser = prop.getProperty(EMAIL_USERNAME_TAG);
			emailPassword = prop.getProperty(EMAIL_PASSWORD_TAG);
			emailSender = prop.getProperty(EMAIL_SENDER_TAG);
			emailEnabled = Boolean.parseBoolean(prop.getProperty(EMAIL_ENABLED));
			emailToListCommaSeparated = prop.getProperty(EMAIL_TO);
		} catch (IOException e) {
			// Do nothing.
			BiitCommonLogger.severe(this.getClass().getName(), e);
		}

		if (smtpServer == null || smtpServer.length() == 0) {
			smtpServer = DEFAULT_EMAIL_SMTP_SERVER;
		}

		if (emailUser == null || emailUser.length() == 0) {
			emailUser = DEFAULT_EMAIL_USERNAME;
		}

		if (emailPassword == null || emailPassword.length() == 0) {
			emailPassword = DEFAULT_EMAIL_PASSWORD;
		}

		if (emailSender == null || emailSender.length() == 0) {
			emailSender = DEFAULT_EMAIL_SENDER;
		}

		if (emailToList == null) {
			emailToList = new ArrayList<>();
			if (emailToListCommaSeparated != null && emailToListCommaSeparated.length() > 0) {
				String[] users = emailToListCommaSeparated.split(",");
				for (String user : users) {
					emailToList.add(user.trim());
				}
			}
		}
	}

	public synchronized String getSmtpServer() {
		return smtpServer;
	}

	public synchronized String getEmailUser() {
		return emailUser;
	}

	public synchronized String getEmailPassword() {
		return emailPassword;
	}

	public synchronized String getEmailSender() {
		return emailSender;
	}

	public boolean isEmailEnabled() {
		return emailEnabled;
	}

	public List<String> getEmailToList() {
		return emailToList;
	}

}
