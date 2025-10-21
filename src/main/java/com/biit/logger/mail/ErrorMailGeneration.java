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

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Functions to generate error report mail
 */
public final class ErrorMailGeneration {
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private static String localhostName;

    private ErrorMailGeneration() {
    }

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
