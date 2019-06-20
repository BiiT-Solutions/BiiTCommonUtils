package com.biit.logger.mail;

import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.biit.logger.BiitCommonLogger;
import com.biit.logger.configuration.EmailConfigurationReader;
import com.biit.logger.mail.exceptions.EmailNotSentException;
import com.biit.logger.mail.exceptions.InvalidEmailAddressException;

public class SendEmail {

	public static void sendEmail(List<String> mailToList, String subject, String htmlContent) throws EmailNotSentException, InvalidEmailAddressException {
		for (final String mailTo : mailToList) {
			sendEmail(mailTo, subject, htmlContent);
			BiitCommonLogger.info(SendEmail.class, "Sending email to " + mailTo);
		}
	}

	public static void sendEmail(String mailTo, String subject, String htmlContent) throws EmailNotSentException, InvalidEmailAddressException {
		sendEmail(EmailConfigurationReader.getInstance().getSmtpServer(), EmailConfigurationReader.getInstance().getEmailPort(), EmailConfigurationReader
				.getInstance().getEmailUser(), EmailConfigurationReader.getInstance().getEmailPassword(), EmailConfigurationReader.getInstance()
				.getEmailSender(), mailTo, subject, htmlContent);
	}

	/**
	 * Sends Mail
	 * 
	 * @param smtpServer
	 * @param emailUser
	 * @param emailPassword
	 * @param emailSender
	 * @param mailTo
	 * @param subject
	 * @param htmlContent
	 * @throws EmailNotSentException
	 * @throws InvalidEmailAddressException
	 */
	public static void sendEmail(String smtpServer, String smtpPort, String emailUser, String emailPassword, String emailSender, String mailTo, String subject,
			String htmlContent) throws EmailNotSentException, InvalidEmailAddressException {
		if (!isValidEmailAddress(emailSender)) {
			throw new InvalidEmailAddressException("Address email '" + emailSender + "' is invalid");
		}

		try {
			final SendEmailThread sendEmailThread = new SendEmailThread();

			sendEmailThread.setSmtpServer(smtpServer);
			sendEmailThread.setEmailUser(emailUser);
			sendEmailThread.setEmailPassword(emailPassword);
			sendEmailThread.setEmailSender(emailSender);
			sendEmailThread.setMailTo(mailTo);
			sendEmailThread.setSubject(subject);
			sendEmailThread.setHtmlContent(htmlContent);
			sendEmailThread.setEmailPort(smtpPort);

			sendEmailThread.run();
		} catch (Throwable exc) {
			BiitCommonLogger.severe(SendEmail.class.getName(), exc);
			final EmailNotSentException emailNotSentException = new EmailNotSentException(exc.getMessage());
			emailNotSentException.setStackTrace(exc.getStackTrace());
			throw emailNotSentException;
		}
	}

	/**
	 * This method is not too strong, some invalid emails will pass this test.
	 * Only for very basic validation of email
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
		final List<String> to = Arrays.asList(new String[] { mailTo });
		postman.setSubject(subject);
		postman.addHtml(htmlContent);
		// Avoiding javax.activation.UnsupportedDataTypeException: no object
		// DCH for MIME type multipart/mixed;
		Thread.currentThread().setContextClassLoader(SendEmail.class.getClassLoader());
		postman.sendMail(to, null, null, emailSender);
	}
}
