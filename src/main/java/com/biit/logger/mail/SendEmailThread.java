package com.biit.logger.mail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import com.biit.logger.BiitCommonLogger;

public class SendEmailThread implements Runnable {
	private String smtpServer;
	private String emailUser;
	private List<String> emailTo;
	private List<String> emailCc;
	private List<String> emailCco;
	private String emailPassword;
	private String emailPort;
	private String emailSender;
	private String subject;
	private String htmlContent;

	private Set<ThreadExceptionListener> exceptionListeners;

	interface ThreadExceptionListener {
		void exceptionLaunched(MessagingException e);
	}

	public SendEmailThread() {
		exceptionListeners = new HashSet<>();
	}

	public void addExceptionListener(ThreadExceptionListener listener) {
		exceptionListeners.add(listener);
	}

	@Override
	public void run() {
		final Postman postman = new Postman(smtpServer, emailPort, emailUser, emailPassword);
		try {
			postman.setSubject(subject);
			postman.addHtml(htmlContent);
			// Avoiding javax.activation.UnsupportedDataTypeException: no object
			// DCH for MIME type multipart/mixed;
			Thread.currentThread().setContextClassLoader(SendEmail.class.getClassLoader());
			if ((emailTo != null && !emailTo.isEmpty()) || (emailCc != null && !emailCc.isEmpty()) || (emailCco != null && !emailCco.isEmpty())) {
				postman.sendMail(emailTo, emailCc, emailCco, emailSender);
			} else {
				BiitCommonLogger.warning(this.getClass(), "Sending email failed. No destination emails sets!");
			}
		} catch (MessagingException e) {
			BiitCommonLogger.severe(this.getClass(),
					"Sending email failed: smtpServer '" + smtpServer + "', emailUser '" + emailUser + "', emailPassword '" + emailPassword + "' ");
			// throw new EmailNotSentException(e.getMessage());
			for (final ThreadExceptionListener listener : exceptionListeners) {
				listener.exceptionLaunched(e);
			}
		}
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public void setEmailSender(String emailSender) {
		this.emailSender = emailSender;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public void setExceptionListeners(Set<ThreadExceptionListener> exceptionListeners) {
		this.exceptionListeners = exceptionListeners;
	}

	public void setEmailPort(String emailPort) {
		this.emailPort = emailPort;
	}

	public void setEmailTo(List<String> emailTo) {
		while (emailTo.remove(null))
			;
		this.emailTo = emailTo;
	}

	public void setEmailCc(List<String> emailCc) {
		while (emailTo.remove(null))
			;
		this.emailCc = emailCc;
	}

	public void setEmailCco(List<String> emailCco) {
		while (emailTo.remove(null))
			;
		this.emailCco = emailCco;
	}

}
