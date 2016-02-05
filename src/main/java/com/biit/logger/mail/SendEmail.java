package com.biit.logger.mail;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.biit.logger.BiitCommonLogger;
import com.biit.logger.configuration.EmailConfigurationReader;
import com.biit.logger.mail.SendEmailThread.ThreadExceptionListener;
import com.biit.logger.mail.exceptions.EmailNotSentException;
import com.biit.logger.mail.exceptions.InvalidEmailAddressException;

public class SendEmail {

	public static void sendEmail(List<String> mailToList, String subject, String htmlContent)
			throws EmailNotSentException, InvalidEmailAddressException {
		for (String mailTo : mailToList) {
			sendEmail(mailTo, subject, htmlContent);
			BiitCommonLogger.info(SendEmail.class, "Sending email to " + mailTo);
		}
	}

	public static void sendEmail(String mailTo, String subject, String htmlContent)
			throws EmailNotSentException, InvalidEmailAddressException {
		sendEmail(EmailConfigurationReader.getInstance().getSmtpServer(),
				EmailConfigurationReader.getInstance().getEmailUser(),
				EmailConfigurationReader.getInstance().getEmailPassword(),
				EmailConfigurationReader.getInstance().getEmailSender(), mailTo, subject, htmlContent);
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
	public static void sendEmail(String smtpServer, String emailUser, String emailPassword, String emailSender,
			String mailTo, String subject, String htmlContent)
					throws EmailNotSentException, InvalidEmailAddressException {
		if (!isValidEmailAddress(emailSender)) {
			throw new InvalidEmailAddressException("Address email '" + emailSender + "' is invalid");
		}

		try {
			SendEmailThread sendEmailThread = new SendEmailThread();
			sendEmailThread.addExceptionListener(new ThreadExceptionListener() {

				@Override
				public void exceptionLaunched(MessagingException e) {
					BiitCommonLogger.severe(SendEmail.class.getName(), e);
					// Catch exception, convert to Runtime to take out from the
					// thread and catch and convert again to
					// exception.
					throw new RuntimeException(e);
				}
			});

			sendEmailThread.setSmtpServer(smtpServer);
			sendEmailThread.setEmailUser(emailUser);
			sendEmailThread.setEmailPassword(emailPassword);
			sendEmailThread.setEmailSender(emailSender);
			sendEmailThread.setMailTo(mailTo);
			sendEmailThread.setSubject(subject);
			sendEmailThread.setHtmlContent(htmlContent);

			sendEmailThread.run();
		} catch (Throwable exc) {
			BiitCommonLogger.severe(SendEmail.class.getName(), exc);
			EmailNotSentException emailNotSentException = new EmailNotSentException(exc.getMessage());
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
	private static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (NullPointerException | AddressException ex) {
			result = false;
		}
		return result;
	}
}
