package com.biit.logger.mail;

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
import com.biit.logger.configuration.EmailConfigurationReader;
import com.biit.logger.mail.exceptions.EmailNotSentException;
import com.biit.logger.mail.exceptions.InvalidEmailAddressException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SendEmail {

    private SendEmail() {

    }

    public static void sendEmail(List<String> mailTo, String subject, String htmlContent, String plainTextContent)
            throws EmailNotSentException, InvalidEmailAddressException {
        if (EmailConfigurationReader.getInstance().getEmailCopy() != null) {
            sendEmail(mailTo, null, new ArrayList<>(Collections.singletonList(EmailConfigurationReader.getInstance().getEmailCopy())), subject,
                    htmlContent, plainTextContent);
        } else {
            sendEmail(mailTo, null, null, subject, htmlContent, plainTextContent);
        }
    }

    public static void sendEmail(String mailTo, String subject, String htmlContent, String plainTextContent)
            throws EmailNotSentException, InvalidEmailAddressException {
        sendEmail(new ArrayList<>(Collections.singletonList(mailTo)), subject, htmlContent, plainTextContent);
    }

    public static void sendEmail(List<String> mailTo, List<String> mailCc, List<String> mailCco, String subject, String htmlContent, String plainTextContent)
            throws EmailNotSentException, InvalidEmailAddressException {
        sendEmail(EmailConfigurationReader.getInstance().getSmtpServer(), EmailConfigurationReader.getInstance().getEmailPort(),
                EmailConfigurationReader.getInstance().getEmailUser(), EmailConfigurationReader.getInstance().getEmailPassword(),
                EmailConfigurationReader.getInstance().getEmailSender(), mailTo, mailCc, mailCco, subject, htmlContent, plainTextContent);
    }

    public static void sendEmail(String smtpServer, String smtpPort, String emailUser, String emailPassword, String emailSender, String mailTo, String mailCc,
                                 String mailCco, String subject, String htmlContent, String plainTextContent)
            throws EmailNotSentException, InvalidEmailAddressException {
        sendEmail(smtpServer, smtpPort, emailUser, emailPassword, emailSender,
                mailTo != null ? Stream.of(mailTo.split(",")).map(String::trim).collect(Collectors.toList()) : null,
                mailCc != null ? Stream.of(mailCc.split(",")).map(String::trim).collect(Collectors.toList()) : null,
                mailCco != null ? Stream.of(mailCco.split(",")).map(String::trim).collect(Collectors.toList()) : null,
                subject, htmlContent, plainTextContent);
    }

    public static void sendEmail(String smtpServer, String smtpPort, String emailUser, String emailPassword, String emailSender, List<String> mailTo,
                                 List<String> mailCc, List<String> mailCco, String subject, String htmlContent, String plainTextContent)
            throws EmailNotSentException, InvalidEmailAddressException {
        sendEmail(smtpServer, smtpPort, emailUser, emailPassword, emailSender, mailTo, mailCc, mailCco, subject, htmlContent, plainTextContent,
                null, null, null);
    }

    public static void sendEmail(String smtpServer, String smtpPort, String emailUser, String emailPassword, String emailSender, String mailTo,
                                 String mailCc, String mailCco, String subject, String htmlContent, String plainTextContent,
                                 byte[] attachment, String attachmentType, String attachmentName) throws EmailNotSentException, InvalidEmailAddressException {
        sendEmail(smtpServer, smtpPort, emailUser, emailPassword, emailSender,
                mailTo != null ? Stream.of(mailTo.split(",")).map(String::trim).collect(Collectors.toList()) : null,
                mailCc != null ? Stream.of(mailCc.split(",")).map(String::trim).collect(Collectors.toList()) : null,
                mailCco != null ? Stream.of(mailCco.split(",")).map(String::trim).collect(Collectors.toList()) : null,
                subject, htmlContent, plainTextContent, attachment, attachmentType, attachmentName);
    }


    public static void sendEmail(String smtpServer, String smtpPort, String emailUser, String emailPassword, String emailSender, List<String> mailTo,
                                 List<String> mailCc, List<String> mailCco, String subject, String htmlContent, String plainTextContent,
                                 byte[] attachment, String attachmentType, String attachmentName)
            throws EmailNotSentException, InvalidEmailAddressException {
        if (!isValidEmailAddress(emailSender)) {
            throw new InvalidEmailAddressException("Address email '" + emailSender + "' is invalid");
        }

        try {
            BiitCommonLogger.info(SendEmail.class, "Sending email to " + mailTo);
            final SendEmailThread sendEmailThread = new SendEmailThread();

            sendEmailThread.setSmtpServer(smtpServer);
            sendEmailThread.setEmailUser(emailUser);
            sendEmailThread.setEmailPassword(emailPassword);
            sendEmailThread.setEmailSender(emailSender);
            sendEmailThread.setEmailCco(mailCco != null ? mailCco : new ArrayList<>());
            sendEmailThread.setEmailCc(mailCc != null ? mailCc : new ArrayList<>());
            sendEmailThread.setEmailTo(mailTo != null ? mailTo : new ArrayList<>());
            sendEmailThread.setSubject(subject);
            sendEmailThread.setHtmlContent(htmlContent);
            sendEmailThread.setPlainTextContent(plainTextContent);
            sendEmailThread.setEmailPort(smtpPort);
            sendEmailThread.setAttachment(attachment);
            sendEmailThread.setAttachmentType(attachmentType);
            sendEmailThread.setAttachmentName(attachmentName);

            sendEmailThread.run();
        } catch (Throwable exc) {
            BiitCommonLogger.severe(SendEmail.class.getName(), exc);
            final EmailNotSentException emailNotSentException = new EmailNotSentException(exc.getMessage());
            emailNotSentException.setStackTrace(exc.getStackTrace());
            throw emailNotSentException;
        }
    }

    /**
     * This method is not too strong, some invalid emails will pass this test. Only
     * for very basic validation of email
     *
     * @param email
     * @return
     */
    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            final InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (NullPointerException | AddressException ex) {
            result = false;
        }
        return result;
    }

    public static void basicMailSender(String smtpServer, String smtpPort, String emailUser, String emailPassword, String emailSender, String mailTo,
                                       String subject, String htmlContent) throws MessagingException {
        final Postman postman = new Postman(smtpServer, smtpPort, emailUser, emailPassword);
        final List<String> to = new ArrayList<>(Arrays.asList(new String[]{mailTo}));
        postman.setSubject(subject);
        postman.addHtml(htmlContent);
        // Avoiding javax.activation.UnsupportedDataTypeException: no object
        // DCH for MIME type multipart/mixed;
        Thread.currentThread().setContextClassLoader(SendEmail.class.getClassLoader());
        postman.sendMail(to, null, null, emailSender);
    }
}
