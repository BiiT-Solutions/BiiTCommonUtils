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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * This class gives all the functionality to create a multipart email, Ascii
 * text/html/files.
 */
public class Postman {

    private Properties properties;
    private Session session;
    private Multipart multipart;
    private String subject;

    public Postman(String smtpServer, final String port, final String username, final String password) {
        properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", smtpServer);
        properties.put("mail.smtp.port", port);

        session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        multipart = new MimeMultipart();

    }

    public void addText(String text) throws MessagingException {
        final BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(text);
        multipart.addBodyPart(messageBodyPart);
    }

    public void addHtml(String html) throws MessagingException {
        final BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(html, "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);
    }

    public void addAttachment(String filename) throws MessagingException {
        final BodyPart messageBodyPart = new MimeBodyPart();
        final DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);
    }

    public void addAttachment(File file, String filename) throws MessagingException {
        final BodyPart messageBodyPart = new MimeBodyPart();
        final DataSource source = new FileDataSource(file);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);
    }

    /**
     * @param content  the data
     * @param type     the MIME type
     * @param filename the name of the attached file.
     * @throws MessagingException
     */
    public void addAttachment(byte[] content, String type, String filename) throws MessagingException {
        final BodyPart messageBodyPart = new MimeBodyPart();
        final DataSource source = new ByteArrayDataSource(content, type);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void clearMail() {
        multipart = new MimeMultipart();
        subject = null;
    }

    public void sendMail(List<String> to, List<String> toCC, List<String> toCCO, String from) throws AddressException, MessagingException {
        final MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        for (final String singleTo : to) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(singleTo));
        }
        if (toCC != null) {
            for (final String singleTo : toCC) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(singleTo));
            }
        }
        if (toCCO != null) {
            for (final String singleTo : toCCO) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(singleTo));
            }
        }
        if (subject != null) {
            message.setSubject(subject);
        }
        message.setContent(multipart);
        Transport.send(message);
    }
}
