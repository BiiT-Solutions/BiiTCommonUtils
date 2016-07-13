package com.biit.logger.mail;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

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

import com.biit.logger.BiitCommonLogger;

/**
 * This class gives all the functionality to create a multipart email, Ascii
 * text/html/files.
 * 
 */
public class Postman {

	private Properties properties;
	private Session session;
	private Multipart multipart;
	private String subject;

	public Postman(String smtpServer, final String username, final String password, final String port) {
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
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(text);
		multipart.addBodyPart(messageBodyPart);
	}

	public void addHtml(String html) throws MessagingException {
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(html, "text/html; charset=utf-8");
		multipart.addBodyPart(messageBodyPart);
	}

	public void addAttachment(String filename) throws MessagingException {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);
		multipart.addBodyPart(messageBodyPart);
	}

	public void addAttachment(File file, String filename) throws MessagingException {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(file);
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
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		for (String singleTo : to) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(singleTo));
		}
		if (toCC != null) {
			for (String singleTo : toCC) {
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(singleTo));
			}
		}
		if (toCCO != null) {
			for (String singleTo : toCCO) {
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(singleTo));
			}
		}
		if (subject != null) {
			message.setSubject(subject);
		}
		message.setContent(multipart);

		try {
			Transport.send(message);
		} catch (MessagingException me) {
			BiitCommonLogger.severe(this.getClass().getName(), me);
			// If email is not configured launch a UnknownHostException, then do
			// nothing.
			if (!(me.getNextException() instanceof UnknownHostException)) {
				throw me;
			}
		}
	}
}