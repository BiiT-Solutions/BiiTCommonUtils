package com.biit.logger.configuration;

/*-
 * #%L
 * Generic utilities used in all Biit projects.
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.logger.BiitCommonLogger;
import com.biit.utils.configuration.ConfigurationReader;
import com.biit.utils.configuration.PropertiesSourceFile;
import com.biit.utils.configuration.SystemVariablePropertiesSourceFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Jar applications cannot have configuration files inside JAR file, must be in
 * an external folder to allow editing.
 */
public final class EmailConfigurationReader extends ConfigurationReader {
    private static final String CONFIG_FILE = "settings.conf";
    private static final String SYSTEM_VARIABLE_CONFIG = "BIIT_LOGGER_CONFIG";

    private static final String EMAIL_ENABLED_TAG = "mail.enabled";
    private static final String EMAIL_TO_TAG = "mail.to";
    private static final String EMAIL_SMTP_SERVER_TAG = "mail.smtpserver";
    private static final String EMAIL_USERNAME_TAG = "mail.username";
    private static final String EMAIL_PASSWORD_TAG = "mail.password";
    private static final String EMAIL_SENDER_TAG = "mail.sender";
    private static final String EMAIL_COPY_ADDRESS = "mail.copy";
    private static final String EMAIL_PORT_TAG = "mail.port";

    private static final String DEFAULT_EMAIL_SMTP_SERVER = "smtp.mail.com";
    private static final String DEFAULT_EMAIL_PORT = "587";
    private static final String DEFAULT_EMAIL_USERNAME = "noreply@email.com";
    private static final String DEFAULT_EMAIL_PASSWORD = "password";
    private static final String DEFAULT_EMAIL_SENDER = "info@test.com";

    private static EmailConfigurationReader instance;

    private EmailConfigurationReader() {
        super();

        addProperty(EMAIL_SMTP_SERVER_TAG, DEFAULT_EMAIL_SMTP_SERVER);
        addProperty(EMAIL_USERNAME_TAG, DEFAULT_EMAIL_USERNAME);
        addProperty(EMAIL_PASSWORD_TAG, DEFAULT_EMAIL_PASSWORD);
        addProperty(EMAIL_SENDER_TAG, DEFAULT_EMAIL_SENDER);
        addProperty(EMAIL_PORT_TAG, DEFAULT_EMAIL_PORT);
        addProperty(EMAIL_COPY_ADDRESS, null);
        addProperty(EMAIL_ENABLED_TAG, false);
        addProperty(EMAIL_TO_TAG, "");
    }

    private void readConfigurationFiles() {
        addPropertiesSource(new PropertiesSourceFile(CONFIG_FILE));
        addPropertiesSource(new SystemVariablePropertiesSourceFile(SYSTEM_VARIABLE_CONFIG, CONFIG_FILE));

        readConfigurations();
    }

    public static synchronized EmailConfigurationReader getInstance() {
        if (instance == null) {
            synchronized (EmailConfigurationReader.class) {
                instance = new EmailConfigurationReader();
                instance.readConfigurationFiles();
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

    public synchronized String getEmailCopy() {
        return getPropertyLogException(EMAIL_COPY_ADDRESS);
    }

    public synchronized String getEmailPort() {
        return getPropertyLogException(EMAIL_PORT_TAG);
    }

    public boolean isEmailEnabled() {
        try {
            return Boolean.parseBoolean(getPropertyLogException(EMAIL_ENABLED_TAG));
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getEmailTo() {
        final String emailToListCommaSeparated = getPropertyLogException(EMAIL_TO_TAG);
        final List<String> emailToList = new ArrayList<>();
        if (emailToListCommaSeparated != null && emailToListCommaSeparated.length() > 0) {
            final String[] users = emailToListCommaSeparated.split(",");
            for (final String user : users) {
                emailToList.add(user.trim());
            }
        }
        return emailToList;
    }

}
