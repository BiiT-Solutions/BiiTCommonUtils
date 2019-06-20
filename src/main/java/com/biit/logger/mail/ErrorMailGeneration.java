package com.biit.logger.mail;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Functions to generate error report mail
 * 
 */
public class ErrorMailGeneration {
	private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	private static String localhostName;

	public static String getSubject() {
		return "Error in '" + getLocalHostName() + "'.";
	}

	public static String getHtmlMailContent(String className, String errorStackTrace) {
		String htmlMailContent = "";
		htmlMailContent += generateHtmlMailTitle();
		htmlMailContent += generateMailBody(className, errorStackTrace);

		return htmlMailContent;
	}

	private static String generateHtmlMailTitle() {
		return "<h1>Error in '" + getLocalHostName() + "' at '" + getDate() + "'.</h1>";
	}

	private static String generateMailBody(String className, String errorStackTrace) {
		String body = "<table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 100%;\"><tr><td><strong>Class: '"
				+ className + "'</strong></td></tr>";
		body += "<tr><td>"
				+ errorStackTrace.replaceAll("(\r\n|\n)", "<br />").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
				+ "</td></tr>";
		body += "</table>";
		return body;
	}

	private static String getLocalHostName() {
		if (localhostName == null) {
			try {
				localhostName = java.net.InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				localhostName = "unknown";
			}
		}
		return localhostName;
	}

	private static String getDate() {
		final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		final Date date = new Date();
		return dateFormat.format(date);
	}
}
